package fr.iut_rodez.pathpilot_android_client.home.routes.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import fr.iut_rodez.pathpilot_android_client.home.clients.entity.Client;
import fr.iut_rodez.pathpilot_android_client.home.clients.entity.ClientState;

public class RouteTest {

    @Test
    public void visitAllClient() {
        var client1 = createRouteClient(1, "Company 1", ClientState.EXPECTED);
        var client2 = createRouteClient(2, "Company 2", ClientState.EXPECTED);
        var client3 = createRouteClient(3, "Company 3", ClientState.EXPECTED);
        Route route = createRoute(List.of(client1, client2, client3));
        route.setState(RouteState.IN_PROGRESS);

        assertEquals(client1, route.getNextClient());

        route.clientHasBeenVisited();
        assertEquals(client2, route.getNextClient());

        route.clientHasBeenVisited();
        assertEquals(client3, route.getNextClient());

        route.clientHasBeenVisited();
        assertNull(route.getNextClient());
    }

    @Test
    public void skipOneClient() {
        var client1 = createRouteClient(1, "Company 1", ClientState.EXPECTED);
        var client2 = createRouteClient(2, "Company 2", ClientState.EXPECTED);
        var client3 = createRouteClient(3, "Company 3", ClientState.EXPECTED);
        Route route = createRoute(List.of(client1, client2, client3));
        route.setState(RouteState.IN_PROGRESS);

        assertEquals(client1, route.getNextClient());

        assertNotEquals(route.getNextClient(), route.getClients().get(1));
        route.skippedClient(route.getClients().get(1));
        assertEquals(ClientState.SKIPPED, route.getClients().get(1).getState());
        assertEquals(client1, route.getNextClient());

        route.clientHasBeenVisited();
        assertEquals(client3, route.getNextClient());

        route.clientHasBeenVisited();
        assertNull(route.getNextClient());
    }

    @Test
    public void skipAllClient() {
        var client1 = createRouteClient(1, "Company 1", ClientState.EXPECTED);
        var client2 = createRouteClient(2, "Company 2", ClientState.EXPECTED);
        var client3 = createRouteClient(3, "Company 3", ClientState.EXPECTED);
        Route route = createRoute(List.of(client1, client2, client3));
        route.setState(RouteState.IN_PROGRESS);

        assertEquals(client1, route.getNextClient());

        route.skippedClient(route.getNextClient());
        assertEquals(ClientState.SKIPPED, route.getClients().get(0).getState());
        assertEquals(client2, route.getNextClient());

        route.skippedClient(route.getNextClient());
        assertEquals(ClientState.SKIPPED, route.getClients().get(1).getState());
        assertEquals(client3, route.getNextClient());

        route.skippedClient(route.getNextClient());
        assertEquals(ClientState.SKIPPED, route.getClients().get(2).getState());
        assertNull(route.getNextClient());
    }

    @Test
    public void skipFirstClient() {
        var client1 = createRouteClient(1, "Company 1", ClientState.EXPECTED);
        var client2 = createRouteClient(2, "Company 2", ClientState.EXPECTED);
        var client3 = createRouteClient(3, "Company 3", ClientState.EXPECTED);
        Route route = createRoute(List.of(client1, client2, client3));
        route.setState(RouteState.IN_PROGRESS);

        assertEquals(client1, route.getNextClient());

        route.skippedClient(route.getNextClient());
        assertEquals(ClientState.SKIPPED, route.getClients().get(0).getState());
        assertEquals(client2, route.getNextClient());

        route.clientHasBeenVisited();
        assertEquals(client3, route.getNextClient());

        route.clientHasBeenVisited();
        assertNull(route.getNextClient());
    }

    @Test
    public void testCounterWhenAllClientVisited() {
        var client1 = createRouteClient(1, "Company 1", ClientState.EXPECTED);
        var client2 = createRouteClient(2, "Company 2", ClientState.EXPECTED);
        var client3 = createRouteClient(3, "Company 3", ClientState.EXPECTED);
        Route route = createRoute(List.of(client1, client2, client3));
        route.setState(RouteState.IN_PROGRESS);

        assertEquals(3, route.countClientsExpected());
        assertEquals(0, route.countClientsVisited());

        route.clientHasBeenVisited();
        assertEquals(3, route.countClientsExpected());
        assertEquals(1, route.countClientsVisited());

        route.clientHasBeenVisited();
        assertEquals(3, route.countClientsExpected());
        assertEquals(2, route.countClientsVisited());

        route.clientHasBeenVisited();
        assertEquals(3, route.countClientsExpected());
        assertEquals(3, route.countClientsVisited());
    }

    @Test
    public void testCounterWhenOneClientIsSkiped() {
        var client1 = createRouteClient(1, "Company 1", ClientState.EXPECTED);
        var client2 = createRouteClient(2, "Company 2", ClientState.EXPECTED);
        var client3 = createRouteClient(3, "Company 3", ClientState.EXPECTED);
        Route route = createRoute(List.of(client1, client2, client3));
        route.setState(RouteState.IN_PROGRESS);

        assertEquals(3, route.countClientsExpected());
        assertEquals(0, route.countClientsVisited());

        route.clientHasBeenVisited();
        assertEquals(3, route.countClientsExpected());
        assertEquals(1, route.countClientsVisited());

        route.skippedClient(route.getNextClient());
        assertEquals(2, route.countClientsExpected());
        assertEquals(1, route.countClientsVisited());

        route.clientHasBeenVisited();
        assertEquals(2, route.countClientsExpected());
        assertEquals(2, route.countClientsVisited());
    }

    @Test
    public void testCounterWhenAllClientIsSkiped() {
        var client1 = createRouteClient(1, "Company 1", ClientState.EXPECTED);
        var client2 = createRouteClient(2, "Company 2", ClientState.EXPECTED);
        var client3 = createRouteClient(3, "Company 3", ClientState.EXPECTED);
        Route route = createRoute(List.of(client1, client2, client3));
        route.setState(RouteState.IN_PROGRESS);

        assertEquals(3, route.countClientsExpected());
        assertEquals(0, route.countClientsVisited());

        route.skippedClient(route.getNextClient());
        assertEquals(2, route.countClientsExpected());
        assertEquals(0, route.countClientsVisited());

        route.skippedClient(route.getNextClient());
        assertEquals(1, route.countClientsExpected());
        assertEquals(0, route.countClientsVisited());

        route.skippedClient(route.getNextClient());
        assertEquals(0, route.countClientsExpected());
        assertEquals(0, route.countClientsVisited());
    }

    private Route createRoute(List<RouteClient> clients) {
        Route route = new Route();
        route.setClients(new ArrayList<>(clients));
        route.setNextClientIndex(0);
        route.setState(RouteState.NOT_STARTED);
        return route;
    }

    private RouteClient createRouteClient(int idClient, String companyName, ClientState clientState) {
        return new RouteClient(
                new Client(idClient, companyName, 0, 0),
                clientState
        );
    }
}