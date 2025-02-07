package fr.iut_rodez.pathpilot_android_client.home.routes;

import fr.iut_rodez.pathpilot_android_client.home.clients.entity.Client;
import fr.iut_rodez.pathpilot_android_client.home.clients.entity.ClientState;

/**
 * Class to represent a timeline item
 */
public class TimelineItem {
    private String clientName;
    private String clientType;
    private String address;
    //TODO add state after refactor
    //private ClientState state;

    /**
     * Constructor for a timeline item
     *
     * @param clientName Name of the client
     * @param clientType Type of the client
     * @param address    Address of the client
     * @param state      State of the client
     */
    public TimelineItem(String clientName, String clientType, String address, ClientState state) {
        this.clientName = clientName;
        this.clientType = clientType;
        this.address = address;
        //this.state = state;
    }

    /**
     * Constructor for a timeline item
     *
     * @param client Client to create the timeline item from
     */
    public TimelineItem(Client client) {
        this.clientName = client.getCompanyName();
        this.clientType = client.getClientCategory();
        this.address = client.getAddressDisplayName();
        //this.state = client.getState();
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

//    public ClientState getState() {
//        return state;
//    }
}