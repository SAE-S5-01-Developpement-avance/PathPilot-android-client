package fr.iut_rodez.pathpilot_android_client.home.routes;

import fr.iut_rodez.pathpilot_android_client.home.clients.entity.ClientState;
import fr.iut_rodez.pathpilot_android_client.home.routes.entity.RouteClient;

/**
 * Class to represent a timeline item
 */
public class TimelineItem {
    private String clientName;
    private String clientType;
    private String address;
    private ClientState state;

    /**
     * Constructor for a timeline item
     *
     * @param routeClient RouteClient to create the timeline item from
     */
    public TimelineItem(RouteClient routeClient) {
        this.clientName = routeClient.getClient().getCompanyName();
        this.clientType = routeClient.getClient().getClientCategory();
        this.address = routeClient.getClient().getAddressDisplayName();
        this.state = routeClient.getState();
    }

    public String getClientName() {
        return clientName;
    }

    public String getClientType() {
        return clientType;
    }

    public String getAddress() {
        return address;
    }

    public ClientState getState() {
        return state;
    }
}