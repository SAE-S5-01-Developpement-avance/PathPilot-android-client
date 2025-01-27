package fr.iut_rodez.pathpilot_android_client.util;

import static org.junit.jupiter.api.Assertions.*;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

import fr.iut_rodez.pathpilot_android_client.home.clients.Client;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class ParserTest {

    @Test
    public void testGetClientsPageable() throws JSONException {
        String clientsResponseJson = """
                {
                    "_embedded": {
                        "clientList": [
                            {
                                "id": 9,
                                "companyName": "IUT Rodez",
                                "latHomeAddress": 44.3602317,
                                "longHomeAddress": 2.575865,
                                "clientCategory": { "id": 1, "name": "CLIENT" },
                                "description": "Description",
                                "contactLastName": "Doe",
                                "contactFirstName": "John",
                                "phoneNumber": "0606060606"
                            }
                        ]
                    },
                    "_links": {
                        "first": { "href": "http://localhost:8080/api/clients?page=0&size=2" },
                        "prev": { "href": "http://localhost:8080/api/clients?page=1&size=2" },
                        "self": { "href": "http://localhost:8080/api/clients?page=2&size=2" },
                        "last": { "href": "http://localhost:8080/api/clients?page=2&size=2" }
                    },
                    "page": { "size": 2, "totalElements": 5, "totalPages": 3, "number": 2 }
                }
                """;

        JSONObject response = new JSONObject(clientsResponseJson);

        List<Client> clients = Parser.getClientsPageable(response);

        assertNotNull(clients);
        assertEquals(1, clients.size());

        Client firstClient = clients.get(0);
        assertEquals(9, firstClient.getId());
        assertEquals("IUT Rodez", firstClient.getCompanyName());
        assertEquals(44.3602317, firstClient.getLatHomeAddress());
        assertEquals(2.575865, firstClient.getLongHomeAddress());
        assertEquals("CLIENT", firstClient.getClientCategory());
        assertEquals("Description", firstClient.getDescription());
        assertEquals("Doe", firstClient.getContactLastName());
        assertEquals("John", firstClient.getContactFirstName());


    }

}