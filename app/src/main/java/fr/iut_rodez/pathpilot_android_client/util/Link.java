package fr.iut_rodez.pathpilot_android_client.util;

import androidx.annotation.NonNull;

/**
 * Represents a link in a REST API response
 * <p>
 *     A link is composed of a relation and an href
 *     The relation is used to know what the link is used for
 *     The href is used to know where the link points
 * </p>
 * @param rel The relation of the link
 * @param href The href of the link
 */
public record Link(@NonNull String rel, @NonNull String href) {
}
