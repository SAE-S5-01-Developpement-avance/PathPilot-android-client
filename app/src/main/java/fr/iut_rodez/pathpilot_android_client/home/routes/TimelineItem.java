package fr.iut_rodez.pathpilot_android_client.home.routes;

import fr.iut_rodez.pathpilot_android_client.home.clients.entity.Client;

/**
 * Class to represent a timeline item
 */
public class TimelineItem {
    private String clientName;
    private String clientType;
    private String address;

    /**
     * Constructor for a timeline item
     *
     * @param clientName Name of the client
     * @param clientType Type of the client
     * @param address    Address of the client
     */
    public TimelineItem(String clientName, String clientType, String address) {
        this.clientName = clientName;
        this.clientType = clientType;
        this.address = address;
    }

    public TimelineItem(Client client) {
        this.clientName = client.getCompanyName();
        this.clientType = client.getClientCategory();
        this.address = client.getAddressDisplayName();
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
}