package fr.iut_rodez.pathpilot_android_client.home.routes.entity;

public enum RouteState {
    /**
     * The route is not started yet
     */
    NOT_STARTED("NOT_STARTED"),

    /**
     * The route is paused
     */
    PAUSED("PAUSED"),

    /**
     * The route is stopped
     */
    STOPPED("STOPPED"),

    /**
     * The route is finished
     */
    FINISHED("FINISHED"),

    /**
     * The route is running
     */
    IN_PROGRESS("IN_PROGRESS");

    private final String value;

    RouteState(String value) {
        this.value = value;
    }
}
