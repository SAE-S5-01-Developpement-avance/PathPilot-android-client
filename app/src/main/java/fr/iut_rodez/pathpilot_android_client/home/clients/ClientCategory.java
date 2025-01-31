package fr.iut_rodez.pathpilot_android_client.home.clients;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

public enum ClientCategory {
    CLIENT("CLIENT"),
    PROSPECT("PROSPECT");

    private final String category;

    ClientCategory(String category) {
        this.category = category;
    }

    public String category() {
        return category;
    }

    /**
     * Parse the category from a JSON object
     * <p>
     * If the category is not found, the default value is CLIENT
     * The category is searched in the "name" field
     * </p>
     *
     * @param category
     * @return
     */
    @NonNull
    public static ClientCategory fromJSON(JSONObject category) {
        ClientCategory categoryParsed = CLIENT;
        try {
            String categoryStringFound = category.getString("name");
            categoryParsed = fromString(categoryStringFound);
        } catch (JSONException ignored) {
            // If the category is not found, the default value is CLIENT
        }
        return categoryParsed;
    }

    /**
     * Parse the category from a string
     * <p>
     * If the string is not recognized, the default value is CLIENT
     * </p>
     *
     * @param category the string to parse
     * @return the parsed category
     */
    @NonNull
    public static ClientCategory fromString(String category) {
        ClientCategory categoryParsed = CLIENT;
        ClientCategory[] values = ClientCategory.values();
        boolean found = false;

        for (int i = 0, valuesLength = values.length; i < valuesLength && !found; i++) {
            ClientCategory clientCategory = values[i];
            if (clientCategory.category.equalsIgnoreCase(category)) {
                categoryParsed = clientCategory;
                found = true;
            }
        }
        return categoryParsed;
    }
}
