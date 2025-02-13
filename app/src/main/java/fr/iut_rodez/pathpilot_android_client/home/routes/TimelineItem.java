package fr.iut_rodez.pathpilot_android_client.home.routes;

import fr.iut_rodez.pathpilot_android_client.home.clients.entity.ClientState;
import fr.iut_rodez.pathpilot_android_client.home.routes.entity.RouteClient;

/**
 * Record to represent a timeline item
 */
public record TimelineItem(String clientName, String clientType, String address, ClientState state) {
    /**
     * Constructor for a timeline item
     *
     * @param routeClient RouteClient to create the timeline item from
     */
    public TimelineItem(RouteClient routeClient) {
        this(routeClient.getClient().getCompanyName(),
                routeClient.getClient().getClientCategory(),
                routeClient.getClient().getAddressDisplayName(),
                routeClient.getState());
    }
}