package fr.iut_rodez.pathpilot_android_client.home.routes.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import fr.iut_rodez.pathpilot_android_client.home.clients.entity.Client;
import fr.iut_rodez.pathpilot_android_client.home.clients.entity.ClientState;

public class RouteTest {

    @Test
    public void getNextClient() {
        Route route = createRoute();
        var client1 = createRouteClient(1, "Company 1", ClientState.EXPECTED);
        var client2 = createRouteClient(2, "Company 2", ClientState.EXPECTED);
        var client3 = createRouteClient(3, "Company 3", ClientState.EXPECTED);
        route.setClients(new ArrayList<>(List.of(client1, client2, client3)));
        route.setIndexCurrentClient(0);
        route.setState(RouteState.IN_PROGRESS);

        assertEquals(client1, route.getCurrentClient());
        assertEquals(client2, route.getNextClient());

        route.clientHasBeenVisited();

        assertEquals(client2, route.getCurrentClient());
        assertEquals(client3, route.getNextClient());

        route.clientHasBeenVisited();

        assertEquals(client3, route.getCurrentClient());
        assertNull(route.getNextClient());
    }

    private Route createRoute() {
        return new Route();
    }

    private RouteClient createRouteClient(int idClient, String companyName, ClientState clientState) {
        return new RouteClient(
                new Client(idClient, companyName, 0, 0),
                clientState
        );
    }
}