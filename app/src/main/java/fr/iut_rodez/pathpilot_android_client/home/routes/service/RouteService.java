package fr.iut_rodez.pathpilot_android_client.home.routes.service;

import static fr.iut_rodez.pathpilot_android_client.util.VolleyErrorHandler.handleError;
import static fr.iut_rodez.pathpilot_android_client.util.network.NetworkUtils.createAuthenticatedRequest;
import static fr.iut_rodez.pathpilot_android_client.util.network.NetworkUtils.getRequestQueue;

import android.content.Context;
import android.util.Log;
import android.widget.ListView;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

import fr.iut_rodez.pathpilot_android_client.R;
import fr.iut_rodez.pathpilot_android_client.home.Home;
import fr.iut_rodez.pathpilot_android_client.home.itinerary.entity.Itinerary;
import fr.iut_rodez.pathpilot_android_client.home.routes.entity.Route;
import fr.iut_rodez.pathpilot_android_client.home.routes.entity.Route.RouteArrayAdapter;
import fr.iut_rodez.pathpilot_android_client.home.routes.entity.RoutePage;
import fr.iut_rodez.pathpilot_android_client.login.JWTToken;
import fr.iut_rodez.pathpilot_android_client.player.PlayerItinerary;
import fr.iut_rodez.pathpilot_android_client.util.Parser;
import fr.iut_rodez.pathpilot_android_client.util.network.NetworkUtils;
import fr.iut_rodez.pathpilot_android_client.util.popup.Popup;

public class RouteService implements IRouteService {

    public void getRoutes(Context context, ListView listRoutesView) {
        Log.d(TAG, "API URL: " + ROUTES_API_ENDPOINT);

        Home homeActivity = (Home) context;
        RequestQueue requestQueue = getRequestQueue(context);
        String jwtToken = homeActivity.getJWTToken().getToken();

        Popup popup = new Popup(context);
        popup.showProgressDialog();

        JsonObjectRequest request = createAuthenticatedRequest(Request.Method.GET, ROUTES_API_ENDPOINT, null, jwtToken,
                response -> {
                    popup.dismissProgressDialog();
                    Log.d(TAG, "onResponse: " + response);

                    RoutePage routePage = Parser.getRoutesPageable(response);
                    Log.d(TAG, "getRoutes: " + routePage.routes());

                    routePage.routes().forEach(route -> route.setDateDisplayName(context.getResources().getConfiguration().getLocales().get(0)));
                    RouteArrayAdapter adapter = new RouteArrayAdapter(homeActivity, routePage.routes());
                    listRoutesView.post(() -> {
                        listRoutesView.setAdapter(adapter);
                    });

                    // Save the client page to the activity
                    ((Home) context).setRoutePage(routePage);
                },
                error -> {
                    popup.dismissProgressDialog();
                    Log.e(TAG, "onErrorResponse: ", error);
                    handleError(context, error);
                }
        );

        requestQueue.add(request);
    }

    public void getNextPageRoutes(Context context, ListView listRoutesView, String nextPageUrl, RouteArrayAdapter adapter, Runnable callback) {
        Log.d(TAG, "Next Page URL: " + nextPageUrl);

        Home homeActivity = (Home) context;
        RequestQueue requestQueue = getRequestQueue(context);
        String jwtToken = homeActivity.getJWTToken().getToken();

        Popup popup = new Popup(context);
        popup.showProgressDialog();

        JsonObjectRequest request = createAuthenticatedRequest(Request.Method.GET, nextPageUrl, null, jwtToken,
                response -> {
                    popup.dismissProgressDialog();
                    Log.d(TAG, "onResponse: " + response);

                    RoutePage routePage = Parser.getRoutesPageable(response);
                    Log.d(TAG, "getNextRoutePageable: " + routePage);

                    adapter.addAll(routePage.routes());
                    adapter.notifyDataSetChanged();

                    // Save the client page to the activity
                    ((Home) context).setRoutePage(routePage);

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
     * Stop the route.
     *
     * @param context The context of the application
     * @param route the route we have to stop
     */
    public void stopRoute(Context context, Route route, JWTToken jwtToken) {
        Log.d(TAG, "API URL: " + ROUTES_API_ENDPOINT + "/" + route.getId() + "/stop");

        PlayerItinerary playerItinerary = (PlayerItinerary) context;
        RequestQueue requestQueue = getRequestQueue(context);

        Popup popup = new Popup(context);
        popup.showProgressDialog();

        JsonObjectRequest request = createAuthenticatedRequest(Request.Method.PATCH,
                ROUTES_API_ENDPOINT + "/" + route.getId() + "/stop", null, jwtToken.getToken(),
                response -> {
                    popup.dismissProgressDialog();
                    Log.d(TAG, "onResponse: " + response);
                    // Return to the right activity
                    playerItinerary.finish();
                },
                error -> {
                    popup.dismissProgressDialog();
                    Log.e(TAG, "onErrorResponse: ", error);
                    handleError(context, error);
                }
        );
        requestQueue.add(request);
    }

    @Override
    public void startRoute(Context context, Route route, GeoPoint currentPosition, JWTToken jwtToken, Response.Listener<JSONObject> onResponse, Response.ErrorListener onErrorResponse) {
        String url = ROUTES_API_ENDPOINT + "/" + route.getId() + "/start";
        Log.d(TAG, "API URL: " + url);

        RequestQueue requestQueue = getRequestQueue(context);
        PositionRequestModel positionRequestModel = new PositionRequestModel(currentPosition);

        JsonObjectRequest request = createAuthenticatedRequest(
            Request.Method.PATCH,
            url,
            positionRequestModel.toJson(),
            jwtToken.getToken(),
            onResponse,
            onErrorResponse
        );
        requestQueue.add(request);
    }

    @Override
    public void pauseRoute(Context context, Route route, JWTToken jwtToken, Response.Listener<JSONObject> onResponse, Response.ErrorListener onErrorResponse) {
        String url = ROUTES_API_ENDPOINT + "/" + route.getId() + "/pause";
        Log.d(TAG, "API URL: " + url);

        RequestQueue requestQueue = getRequestQueue(context);

        JsonObjectRequest request = createAuthenticatedRequest(
                Request.Method.PATCH,
                url,
                null,
                jwtToken.getToken(),
                onResponse,
                onErrorResponse
        );
        requestQueue.add(request);
    }

    @Override
    public void resumeRoute(Context context, Route route, GeoPoint currentPosition, JWTToken jwtToken, Response.Listener<JSONObject> onResponse, Response.ErrorListener onErrorResponse) {
        String url = ROUTES_API_ENDPOINT + "/" + route.getId() + "/resume";
        Log.d(TAG, "API URL: " + url);

        RequestQueue requestQueue = getRequestQueue(context);
        PositionRequestModel positionRequestModel = new PositionRequestModel(currentPosition);

        JsonObjectRequest request = createAuthenticatedRequest(
                Request.Method.PATCH,
                url,
                positionRequestModel.toJson(),
                jwtToken.getToken(),
                onResponse,
                onErrorResponse
        );
        requestQueue.add(request);
    }


    /**
     * Create a route from an itinerary.
     * <p>
     * Send a request to the server to create a route from an itinerary.<br>
     * If the request is successful, it redirects the user to the player activity with the new route.
     * If the request fails, it shows an error message.
     * </p>
     *
     * @param context The context of the application
     * @param jwtToken The JWT token of the user
     * @param itinerary The itinerary to create the route from
     * @param onResponse The response listener
     * @param onErrorResponse The error listener
     */
    @Override
    public void createRoute(Context context, JWTToken jwtToken, Itinerary itinerary, Response.Listener<JSONObject> onResponse, Response.ErrorListener onErrorResponse) {
        RequestQueue requestQueue = NetworkUtils.getRequestQueue(context);
        RouteRequestModel routeRequestModel = new RouteRequestModel(itinerary.getId());

        JsonObjectRequest request = NetworkUtils.createAuthenticatedRequest(
                Request.Method.POST,
                ROUTES_API_ENDPOINT,
                routeRequestModel.toJson(),
                jwtToken.getToken(),
                onResponse,
                onErrorResponse
        );
        requestQueue.add(request);
    }

    /**
     * Delete the route.
     *
     * @param homeActivity   the activity of the application
     * @param selectedRoute          the route at delete
     * @param listRoutesView list of the routes in the view
     */
    @Override
    public void deleteRoute(Home homeActivity, JWTToken jwtToken, Route selectedRoute, ListView listRoutesView) {
        String apiURLDelete = ROUTES_API_ENDPOINT + "/" + selectedRoute.getId();

        RequestQueue requestQueue = getRequestQueue(homeActivity);
        Popup popup = new Popup(homeActivity);
        popup.showProgressDialog(homeActivity.getString(R.string.message_deleting_route));

        JsonObjectRequest request = NetworkUtils.createAuthenticatedRequest(Request.Method.DELETE,
                apiURLDelete, null, jwtToken.getToken(),
                response -> {
                    popup.dismissProgressDialog();
                    Log.d(TAG, "onResponse: " + response);
                    listRoutesView.post(() -> {
                        Route.RouteArrayAdapter routeArrayAdapter
                                = (Route.RouteArrayAdapter) listRoutesView.getAdapter();
                        routeArrayAdapter.remove(selectedRoute);
                        routeArrayAdapter.notifyDataSetChanged();
                        Log.d(TAG, "deleteRoute: Route " + selectedRoute.getId()
                                + " removed");
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

    record RouteRequestModel(@NonNull String itineraryId) {
        public JSONObject toJson() {
            JSONObject json = new JSONObject();
            try {
                json.put("itineraryId", itineraryId);
            } catch (Exception ignored) {
                // This should never happen, has the value isn't a Number
            }
            return json;
        }
    }

    /**
     * Update salesman position
     *
     * @param context
     * @param jwtToken
     * @param currentPosition
     * @param route
     * @param onResponse
     * @param onErrorResponse
     */
    @Override
    public void updateSalesmanPosition(Context context, JWTToken jwtToken, GeoPoint currentPosition, Route route, Response.Listener<JSONObject> onResponse, Response.ErrorListener onErrorResponse) {
        RequestQueue requestQueue = NetworkUtils.getRequestQueue(context);
        PositionRequestModel positionRequestModel = new PositionRequestModel(currentPosition);

        JsonObjectRequest request = NetworkUtils.createAuthenticatedRequest(
                Request.Method.PUT,
                ROUTES_API_ENDPOINT + "/" + route.getId() + "/updateSalesmanPosition",
                positionRequestModel.toJson(),
                jwtToken.getToken(),
                onResponse,
                onErrorResponse
        );
        requestQueue.add(request);
    }

    record PositionRequestModel(@NonNull GeoPoint currentPosition) {
        public JSONObject toJson() {
            JSONObject json = new JSONObject();
            try {
                json.put("latitude", currentPosition.getLatitude());
                json.put("longitude", currentPosition.getLongitude());
            } catch (Exception ignored) {
                // Ignored
            }
            return json;
        }
    }
}
