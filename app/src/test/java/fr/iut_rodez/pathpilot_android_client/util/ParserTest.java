package fr.iut_rodez.pathpilot_android_client.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

import fr.iut_rodez.pathpilot_android_client.home.clients.Client;
import fr.iut_rodez.pathpilot_android_client.home.itinerary.Itinerary;

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

    @Test
    public void testGetItinerariesPageable() throws JSONException {
        String ItineraryResponseJson = """
                {
                    "_embedded": {
                        "routeList": [
                            {
                                "_id": -1735026622,
                                "salesman": 1,
                                "salesmanHome": {
                                    "latitude": 44.36017116455328,
                                    "longitude": 2.5767227655364024
                                },
                                "clients_schedule": [
                                    {
                                        "client": 8,
                                        "companyLocation": {
                                            "latitude": 44.36076301822145,
                                            "longitude": 2.555959091843846
                                        },
                                        "companyName": "Spar"
                                    },
                                    {
                                        "client": 9,
                                        "companyLocation": {
                                            "latitude": 44.3602317,
                                            "longitude": 2.575865
                                        },
                                        "companyName": "IUT Rodez"
                                    }
                                ],
                                "startDate": null,
                                "clients_visited": [],
                                "salesManCurrentPosition": {
                                    "latitude": 44.36017116455328,
                                    "longitude": 2.5767227655364024
                                }
                            }
                        ]
                    },
                    "_links": {
                        "self": { "href": "http://localhost:8080/api/routes?page=0&size=20" }
                    },
                    "page": { "size": 20, "totalElements": 1, "totalPages": 1, "number": 0 }
                }
                """;

        JSONObject response = new JSONObject(ItineraryResponseJson);

        List<Itinerary> itineraries = Parser.getItinerariesPageable(response);

        assertNotNull(itineraries);
        assertEquals(1, itineraries.size());

        Itinerary firstItinerary = itineraries.get(0);
        assertEquals(-1735026622, firstItinerary.getId());
        assertEquals(44.36017116455328, firstItinerary.getSalesmanLatitude());
        assertEquals(2.5767227655364024, firstItinerary.getSalesmanLongitude());
        assertEquals(2, firstItinerary.getClients().size());

        Client firstClient = firstItinerary.getClients().get(0);
        assertEquals(8, firstClient.getId());
        assertEquals("Spar", firstClient.getCompanyName());
        assertEquals(44.36076301822145, firstClient.getLatHomeAddress());
        assertEquals(2.555959091843846, firstClient.getLongHomeAddress());
    }

}