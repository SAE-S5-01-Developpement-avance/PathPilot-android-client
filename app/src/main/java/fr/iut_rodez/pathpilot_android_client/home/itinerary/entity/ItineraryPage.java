package fr.iut_rodez.pathpilot_android_client.home.itinerary.entity;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import fr.iut_rodez.pathpilot_android_client.util.Link;

/**
 * Represents a page of itineraries with links to other pages
 */
public record ItineraryPage(@NonNull ArrayList<Itinerary> itineraries, @NonNull ArrayList<Link> links) {
    /**
     * Get the link to the next page of itineraries
     *
     * @return The link to the next page of itineraries, or null if there is no next page
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