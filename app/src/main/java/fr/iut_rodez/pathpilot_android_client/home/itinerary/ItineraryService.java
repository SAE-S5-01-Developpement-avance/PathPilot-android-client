package fr.iut_rodez.pathpilot_android_client.home.itinerary;

import static fr.iut_rodez.pathpilot_android_client.util.VolleyErrorHandler.handleError;
import static fr.iut_rodez.pathpilot_android_client.util.network.NetworkUtils.createAuthenticatedRequest;
import static fr.iut_rodez.pathpilot_android_client.util.network.NetworkUtils.getRequestQueue;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import fr.iut_rodez.pathpilot_android_client.R;
import fr.iut_rodez.pathpilot_android_client.BuildConfig;
import fr.iut_rodez.pathpilot_android_client.R;
import fr.iut_rodez.pathpilot_android_client.home.Home;
import fr.iut_rodez.pathpilot_android_client.home.clients.entity.Client;
import fr.iut_rodez.pathpilot_android_client.home.itinerary.entity.Itinerary;
import fr.iut_rodez.pathpilot_android_client.home.itinerary.entity.Itinerary.ItineraryArrayAdapter;
import fr.iut_rodez.pathpilot_android_client.home.itinerary.entity.ItineraryPage;
import fr.iut_rodez.pathpilot_android_client.util.Parser;
import fr.iut_rodez.pathpilot_android_client.util.network.NetworkUtils;
import fr.iut_rodez.pathpilot_android_client.util.popup.DialogButton;
import fr.iut_rodez.pathpilot_android_client.util.popup.Popup;

/**
 * Service to handle all itinerary related requests
 */
public class ItineraryService extends AppCompatActivity{
    public static final String API_BASE_URL = BuildConfig.API_BASE_URL + "itineraries";
    private static final String TAG = ItineraryService.class.getSimpleName();

    private static Popup popup;

    /**
     * Request to the API to add an itinerary.
     * If the request is successful, it goes back to the previous activity.
     *
     * @param context     Context of the application
     * @param listClients The list of clients to create an itinerary
     */
    public static void addItinerary(Context context, List<Client> listClients)
            throws JSONException {
        Log.d(TAG, "API URL: " + API_BASE_URL);

        AddItinerary addItineraryActivity = (AddItinerary) context;
        RequestQueue requestQueue = getRequestQueue(context);
        String jwtToken = addItineraryActivity.getJWTToken().getToken();
        JSONArray listIdClient = new JSONArray();
        JSONObject itinerariesInput = new JSONObject();

        for (Client client : listClients) {
            listIdClient.put(client.getId());
        }
        itinerariesInput.put("clients_schedule", listIdClient);

        Popup popup = new Popup(context);

        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.show();

        JsonObjectRequest request = NetworkUtils.createAuthenticatedRequest(Request.Method.POST,
                API_BASE_URL, itinerariesInput, jwtToken,
                response -> {
                    popup.dismissProgressDialog();
                    Log.d(TAG, "onResponse: " + response);
                    try {
                        JSONArray orderedClientsList = response.getJSONArray("clients_schedule");
                        for (int i = 0; i < orderedClientsList.length(); i++) {
                            namesClient.add(orderedClientsList.getJSONObject(i).getString("companyName"));
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    DialogButton btnSaveItinerary = new DialogButton(context.getString(R.string.confirm_creation_itinerary),
                            ((dialog, which) -> {
                        dialog.dismiss();
                        Intent returnIntent = new Intent(addItineraryActivity, Home.class);
                        addItineraryActivity.setResult(AddItinerary.RESULT_OK, returnIntent);
                        returnIntent.putExtra(AddItinerary.CLE_ITINERARY_ADDED, true);
                        addItineraryActivity.finish();
                    }));

                    StringBuilder orderedClientsListText = new StringBuilder();
                    try {
                        JSONArray orderedClientsList = response.getJSONArray("clients_schedule");
                        for (int i = 0; i < orderedClientsList.length(); i++) {
                            for (int j = 0; j < listClients.size(); j++) {
                                if (listClients.get(j).getId()
                                        == orderedClientsList.getJSONObject(i).getInt("id")){
                                    orderedClientsListText.append(listClients.get(j)
                                            .layoutClientItemList()).append("\n");
                                    listClientOrdered.add(listClients.get(i));
                                }
                            }
                        }
                        Itinerary itinerary = new Itinerary();
                        itinerary.setId(response.getString("id"));
                        itinerary.setClients((ArrayList)listClientOrdered);
                        Intent intent = new Intent(context,SaveItinerary.class);
                        intent.putExtra(FragmentItineraries.CLE_TOKEN,
                                addItineraryActivity.getJWTToken());
                        intent.putExtra(AddItinerary.KEY_ITINERARY_OBJECT,itinerary);
                        context.startActivity(intent);

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                },
                error -> {
                    popup.dismissProgressDialog();
                    Log.e(TAG, "onErrorResponse: ", error);
                    handleError(context, error);
                });
        requestQueue.add(request);
    }

    /**
     * Request to the API the itineraries.
     * If the request is successful, it add itineraries to the adapter and link them to the view
     * If not it displays the error encounter.
     *
     * @param context             Context of the application
     * @param listItinerariesView The view where the itineraries will be displayed
     */
    @Override
    public void getItineraries(Context context, ListView listItinerariesView) {
        Log.d(TAG, "API URL: " + API_BASE_URL);

        Home homeActivity = (Home) context;
        RequestQueue requestQueue = getRequestQueue(context);
        String jwtToken = homeActivity.getJWTToken().getToken();

        Popup popup = new Popup(context);
        popup.showProgressDialog(context.getString(R.string.progress_fetching_itineraries));

        JsonObjectRequest request = createAuthenticatedRequest(Request.Method.GET, API_BASE_URL,
                null, jwtToken,
                response -> {
                    popup.dismissProgressDialog();
                    Log.d(TAG, "onResponse: " + response);

                    ItineraryPage itineraryPage = Parser.getItinerariesPageable(response);
                    Log.d(TAG, "getItineraries: " + itineraryPage.itineraries());

                    ItineraryArrayAdapter adapter = new ItineraryArrayAdapter(homeActivity,
                            itineraryPage.itineraries());
                    listItinerariesView.post(() -> {
                        listItinerariesView.setAdapter(adapter);
                    });

                    // Save the client page to the activity
                    ((Home) context).setItineraryPage(itineraryPage);
                },
                error -> {
                    popup.dismissProgressDialog();
                    Log.e(TAG, "onErrorResponse: ", error);
                    handleError(context, error);
                }
        );

        requestQueue.add(request);
    }

    /**
     * Request to the API to fetch the next page of clients.
     * If the request is successful, it adds clients to the adapter and link it to the view
     * If not it displays the error encounter.
     *
     * @param context         Context of the application
     * @param listItinerariesView The view where the clients will be displayed
     * @param nextPageUrl     The URL of the next page
     * @param adapter         The adapter to add the clients to
     */
    public static void getNextPageItineraries(Context context, ListView listItinerariesView,
                                              String nextPageUrl, ItineraryArrayAdapter adapter) {
        Log.d(TAG, "Next Page URL: " + nextPageUrl);

        Home homeActivity = (Home) context;
        RequestQueue requestQueue = getRequestQueue(context);
        String jwtToken = homeActivity.getJWTToken().getToken();

        Popup popup = new Popup(context);
        popup.showProgressDialog(context.getString(R.string.progress_fetching_itineraries));

        JsonObjectRequest request = createAuthenticatedRequest(Request.Method.GET, nextPageUrl,
                null, jwtToken,
                response -> {
                    popup.dismissProgressDialog();
                    Log.d(TAG, "onResponse: " + response);

                    ItineraryPage itineraryPage = Parser.getItinerariesPageable(response);
                    Log.d(TAG, "getNextItineraryPageable: " + itineraryPage);

                    adapter.addAll(itineraryPage.itineraries());
                    adapter.notifyDataSetChanged();

                    // Save the client page to the activity
                    ((Home) context).setItineraryPage(itineraryPage);

                    // After fetching the next page, call the callback
                    callback.run();
                },
                error -> {
                    popup.dismissProgressDialog();
                    Log.e(TAG, "onErrorResponse: ", error);
                    handleError(context, error);

                    // After fetching the next page, call the callback
                    callback.run();
                }
        );

        requestQueue.add(request);
    }

    /**
     * Request to the API to delete an itinerary.
     * If the request is successful, it removed the itinerary from the adapter
     *
     * @param homeActivity    Context of the application
     * @param itinerarySelected  The itinerary to delete
     * @param listItinerariesView The view where the itineraries will be displayed
     */
    public static void deleteItinerary(Home homeActivity, Itinerary itinerarySelected,
                                       ListView listItinerariesView) {

        String apiURLDelete = API_BASE_URL + "/" + itinerarySelected.getId();

        RequestQueue requestQueue = getRequestQueue(homeActivity);
        String jwtToken = homeActivity.getJWTToken().getToken();

        Popup popup = new Popup(homeActivity);
        popup.showProgressDialog(homeActivity.getString(R.string.progress_deleting_itinerary));

        Log.d(TAG, "deleteItinerary: " + itinerarySelected.getId());

        JsonObjectRequest request = NetworkUtils.createAuthenticatedRequest(Request.Method.DELETE,
                apiURLDelete, null, jwtToken,
                response -> {
                    popup.dismissProgressDialog();
                    Log.d(TAG, "onResponse: " + response);
                    listItinerariesView.post(() -> {
                        ItineraryArrayAdapter itineraryArrayAdapter
                                = (ItineraryArrayAdapter) listItinerariesView.getAdapter();
                        itineraryArrayAdapter.remove(itinerarySelected);
                        itineraryArrayAdapter.notifyDataSetChanged();
                        Log.d(TAG, "deleteClient: Itinerary" + itinerarySelected.getId()
                                + "removed");
                    });
                },
                error -> {
                    popup.dismissProgressDialog();
                    Log.e(TAG, "onErrorResponse: ", error);
                    handleError(homeActivity, error);
                }
        );
        requestQueue.add(request);
    }

    /**
     * Delete the itinerary after the salesman saws the order of clients of the current itinerary
     * created and cancel the creation.
     * @param context context of the application
     * @param idItinerary id of the itinerary to delete
     */
    public static  void deleteItineraryToCancelTheCreation(Context context,String idItinerary) {
        String apiURLDelete = API_BASE_URL + "/" + idItinerary;

        AddItinerary addItineraryActivity = (AddItinerary) context;
        RequestQueue requestQueue = getRequestQueue(context);
        String jwtToken = addItineraryActivity.getJWTToken().getToken();

        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.show();

        Log.d(TAG, "deleteItinerary: " + idItinerary);

        JsonObjectRequest request = NetworkUtils.createAuthenticatedRequest(Request.Method.DELETE,
                apiURLDelete, null, jwtToken,
                response -> {
                    progressDialog.dismiss();
                    Log.d(TAG, "onResponse: " + response);
                },
                error -> {
                    progressDialog.dismiss();
                    Log.e(TAG, "onErrorResponse: ", error);
                    handleError(context, error);
                }
        );
        requestQueue.add(request);
    }
}
