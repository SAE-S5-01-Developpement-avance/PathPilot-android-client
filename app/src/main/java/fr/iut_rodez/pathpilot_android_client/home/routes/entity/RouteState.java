package fr.iut_rodez.pathpilot_android_client.home.routes.entity;

public enum RouteState {
    /**
     * The route is not started yet
     */
    NOT_STARTED,

    /**
     * The route is paused
     */
    PAUSED,

    /**
     * The route is stopped
     */
    STOPPED,

    /**
     * The route is finished
     */
    FINISHED
}
