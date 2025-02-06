package fr.iut_rodez.pathpilot_android_client.home.routes.entity;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import fr.iut_rodez.pathpilot_android_client.util.Link;

/**
 * Represents a page of routes with links to other pages
 */
public record RoutePage(@NonNull ArrayList<Route> routes, @NonNull ArrayList<Link> links) {
    /**
     * Get the link to the next page of routes
     *
     * @return The link to the next page of routes, or null if there is no next page
     */
    public Link getNext() {
        for (Link link : links) {
            if (link.rel().equals("next")) {
                return link;
            }
        }
        return null;
    }
}