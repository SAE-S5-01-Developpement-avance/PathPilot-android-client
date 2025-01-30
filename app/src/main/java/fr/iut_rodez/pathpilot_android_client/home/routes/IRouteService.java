package fr.iut_rodez.pathpilot_android_client.home.routes;

import android.widget.ListView;

public interface IRouteService {
    String BASE_API_URL = "/api/routes";

    /**
     * Retrieve the list of routes from the server.
     * <p>
     *     Send a request to the server to retrieve the list of routes.<br>
     *     If the request is successful, it displays the list of routes in the view.
     * </p>
     * @param listRoutesView The view where the routes will be displayed
     */
    void createRoute();

    /**
     * Tell the server that the client has been visited.
     * <p>
     *     Send a request to the server to tell that the client has been visited.<br>
     *     If the request is successful, it updates the route with the new client visited.
     * </p>
     * @param route The route to update
     */
    void clientVisited(Route route);
}
