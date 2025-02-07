package fr.iut_rodez.pathpilot_android_client.home.clients.entity;

public enum ClientState {
    /**
     * The client has been visited (blue dot)
     */
    VISITED,

    /**
     * The client has not been visited (white dot with blue border)
     */
    NOT_VISITED,

    /**
     * The client has been skipped (blue dot with white border)
     */
    SKIPPED
}