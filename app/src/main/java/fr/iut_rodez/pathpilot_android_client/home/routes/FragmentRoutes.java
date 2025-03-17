package fr.iut_rodez.pathpilot_android_client.home.routes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
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
    public static final String ROUTE_KEY = "route";

    private ListView listRoutesView;
    private boolean isLoading = false;
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
        view.findViewById(R.id.refresh_routes_btn).setOnClickListener(v -> {
            refreshRoutes();
        });

        // Set OnScrollListener to load more routes when reaching the bottom
        listRoutesView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // No action needed here
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount >= totalItemCount && totalItemCount > 0 && !isLoading) {
                    // Check if there is a next page link
                    Link nextLink = homeActivity.getRoutePage().getNext();
                    if (nextLink != null) {
                        isLoading = true;
                        routeService.getNextPageRoutes(homeActivity, listRoutesView, nextLink.href(), (RouteArrayAdapter) listRoutesView.getAdapter(), () -> isLoading = false);
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
            intent.putExtra(InfoRoute.ROUTE_KEY, route);
            intent.putExtra(InfoRoute.JWT_TOKEN_KEY, homeActivity.getJWTToken());

            homeActivity.getInfoRouteLauncher().launch(intent);
        });

        return view;
    }

    private void refreshRoutes() {
        ((RouteArrayAdapter) listRoutesView.getAdapter()).clear();
        routeService.getRoutes(homeActivity, listRoutesView);
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
