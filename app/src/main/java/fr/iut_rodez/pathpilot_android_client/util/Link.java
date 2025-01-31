package fr.iut_rodez.pathpilot_android_client.util;

import androidx.annotation.NonNull;

/**
 * Represents a link in a REST API response
 */
public record Link(@NonNull String rel, @NonNull String href) {
}
