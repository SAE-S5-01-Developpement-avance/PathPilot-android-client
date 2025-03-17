package fr.iut_rodez.pathpilot_android_client.home.routes.entity;

import androidx.annotation.NonNull;

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

    public String getValue() {
        return value;
    }

    RouteState(String value) {
        this.value = value;
    }

    public static RouteState fromString(@NonNull String value) {
        for (RouteState state : RouteState.values()) {
            if (state.value.equals(value)) {
                return state;
            }
        }
        // Default value
        return NOT_STARTED;
    }
}
