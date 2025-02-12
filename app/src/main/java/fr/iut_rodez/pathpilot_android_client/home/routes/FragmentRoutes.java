package fr.iut_rodez.pathpilot_android_client.home.routes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import fr.iut_rodez.pathpilot_android_client.R;
import fr.iut_rodez.pathpilot_android_client.ServiceFactory;
import fr.iut_rodez.pathpilot_android_client.home.Home;
import fr.iut_rodez.pathpilot_android_client.home.routes.entity.Route;
import fr.iut_rodez.pathpilot_android_client.home.routes.entity.Route.RouteArrayAdapter;
import fr.iut_rodez.pathpilot_android_client.home.routes.entity.RoutePage;
import fr.iut_rodez.pathpilot_android_client.home.routes.service.IRouteService;
import fr.iut_rodez.pathpilot_android_client.util.Link;

/**
 * Display all routes
 * Handle all the routes related actions
 */
public class FragmentRoutes extends Fragment {

    /**
     * The tab's icon.
     *
     * @see Home
     */
    public static final int ICON = R.drawable.icon_car;
    private static final String TAG = FragmentRoutes.class.getSimpleName();
    public static final String JWT_TOKEN_KEY = "token";
    public static final String LIST_ITINERARIES_KEY = "listItineraries";
    public static final String ITINERARY_KEY = "route";

    private ListView listRoutesView;
    private Home homeActivity;
    private IRouteService routeService = ServiceFactory.getRouteService();

    public static FragmentRoutes newInstance() {
        return new FragmentRoutes();
    }

    private FragmentRoutes() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_routes, container, false);
        homeActivity = (Home) getActivity();


        //Set header text to routes
        ((TextView) view.findViewById(R.id.header_text)).setText(R.string.header_routes_list);

        listRoutesView = view.findViewById(R.id.routes_list);

        // Set OnScrollListener to load more routes when reaching the bottom
        listRoutesView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // No action needed here
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount >= totalItemCount && totalItemCount > 0) {
                    // Check if there is a next page link
                    Link nextLink = homeActivity.getRoutePage().getNext();
                    if (nextLink != null) {
                        routeService.getNextPageRoutes(homeActivity, listRoutesView, nextLink.href(), (RouteArrayAdapter) listRoutesView.getAdapter());
                    }
                }
            }
        });

        // Get all routes from the API
        loadRoutes();

        registerForContextMenu(listRoutesView);
        view.findViewById(R.id.button_add).setOnClickListener(v -> gotoCreateRoute());

        listRoutesView.setOnItemClickListener((parent, view1, position, id) -> {
            Route route = (Route) parent.getItemAtPosition(position);
            Log.d(TAG, "onItemClick: Route: " + route);
            Intent intent = new Intent(getActivity(), InfoRoute.class);
            intent.putExtra(ITINERARY_KEY, route);
            intent.putExtra(JWT_TOKEN_KEY, homeActivity.getJWTToken());
            startActivity(intent);
        });

        return view;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        new MenuInflater(getActivity()).inflate(R.menu.itineray_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Route routeSelected = (Route) listRoutesView.getItemAtPosition(info.position);
        int optionSelected = item.getItemId();

//        if (optionSelected == R.id.delete_route) {
//            Log.d(TAG, "onContextItemSelected: Delete route");
//            RouteService.deleteRoute(homeActivity, routeSelected, listRoutesView);
//        } else {
//            Log.e(TAG, "onContextItemSelected: Unknown option selected");
//        }
        return (super.onContextItemSelected(item));
    }

    /**
     * Call the service to load all the routes from the API.
     */
    public void loadRoutes() {
        routeService.getRoutes(homeActivity, listRoutesView);
    }

    /**
     * Go to the {@link AddRoute} activity.
     * <p>
     * Give the JWT token and the list of itineraries to the activity.
     */
    private void gotoCreateRoute() {
        Log.d(TAG, "gotoCreateRoute: Goto create route");

        Intent intent = new Intent(getActivity(), AddRoute.class);
        intent.putExtra(JWT_TOKEN_KEY, homeActivity.getJWTToken());
        intent.putExtra(LIST_ITINERARIES_KEY, homeActivity.getItineraries());
        homeActivity.getAddRouteLauncher().launch(intent);
    }

    public interface FragmentRouteActions {
        RoutePage getRoutePage();
        ActivityResultLauncher<Intent> getAddRouteLauncher();
    }
}
