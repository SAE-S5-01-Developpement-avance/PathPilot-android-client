package fr.iut_rodez.pathpilot_android_client.home.clients.entity;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import fr.iut_rodez.pathpilot_android_client.util.Link;

/**
 * Represents a page of clients with links to other pages
 */
public record ClientPage(@NonNull ArrayList<Client> clients, @NonNull ArrayList<Link> links) {
    /**
     * Get the link to the next page of clients
     *
     * @return The link to the next page of clients, or null if there is no next page
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
