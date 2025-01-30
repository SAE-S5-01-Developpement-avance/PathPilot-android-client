package fr.iut_rodez.pathpilot_android_client.home.routes;

import android.widget.ListView;

import fr.iut_rodez.pathpilot_android_client.home.clients.Client;

public class RouteService implements IRouteService {

    /**
     * Retrieve the list of routes from the server.
     * <p>
     * Send a request to the server to retrieve the list of routes.<br>
     * If the request is successful, it displays the list of routes in the view.
     * </p>
     *
     */
    @Override
    public void createRoute() {

    }

    @Override
    public void clientVisited(Route route) {
        Client clientVisited = route.getCurrentClient();
        if (clientVisited != null) {
            final String url = BASE_API_URL + "/" + clientVisited.getId();

            //TODO Send Request to the server
            //Update the route with the new client visited
            route.clientHasBeenVisited();
        }
    }
}
