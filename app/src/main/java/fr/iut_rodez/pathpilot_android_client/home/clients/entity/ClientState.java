package fr.iut_rodez.pathpilot_android_client.home.clients.entity;

public enum ClientState {
    /**
     * The client has been visited (blue dot)
     */
    VISITED("VISITED"),

    /**
     * The client has not been visited (white dot with blue border)
     */
    EXPECTED("EXPECTED"),

    /**
     * The client has been skipped (blue dot with white border)
     */
    SKIPPED("SKIPPED");

    public final String value;

    ClientState(String value) {
        this.value = value;
    }
}