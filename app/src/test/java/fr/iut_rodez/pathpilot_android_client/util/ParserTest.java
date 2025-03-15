package fr.iut_rodez.pathpilot_android_client.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import fr.iut_rodez.pathpilot_android_client.home.clients.entity.Client;
import fr.iut_rodez.pathpilot_android_client.home.clients.entity.ClientCategory;
import fr.iut_rodez.pathpilot_android_client.home.clients.entity.ClientPage;
import fr.iut_rodez.pathpilot_android_client.home.itinerary.entity.Itinerary;
import fr.iut_rodez.pathpilot_android_client.home.itinerary.entity.ItineraryPage;
import fr.iut_rodez.pathpilot_android_client.home.routes.entity.Route;
import fr.iut_rodez.pathpilot_android_client.home.routes.entity.RouteClient;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class ParserTest {

    @Test
    public void testGetClientsPageable() throws JSONException {
        String clientsResponseJson = """
                {
                     "_embedded": {
                         "clientResponseModelList": [
                             {
                                 "id": 10,
                                 "companyName": "Intermarche",
                                 "latHomeAddress": 48.8566,
                                 "longHomeAddress": 2.3522,
                                 "clientCategory": { "id": 1, "name": "CLIENT" },
                                 "description": "Description A",
                                 "contactLastName": "Doe",
                                 "contactFirstName": "John",
                                 "phoneNumber": "0123456789",
                                 "_links": {
                                     "self": { "href": "http://localhost:8080/clients/10" },
                                     "delete": { "href": "http://localhost:8080/clients/10" }
                                 }
                             },
                             {
                                 "id": 16,
                                 "companyName": "Nike",
                                 "latHomeAddress": 48.8566,
                                 "longHomeAddress": 2.3522,
                                 "clientCategory": { "id": 2, "name": "PROSPECT" },
                                 "description": "Description de Nike",
                                 "contactLastName": "Doe",
                                 "contactFirstName": "John",
                                 "phoneNumber": "0123456789",
                                 "_links": {
                                     "self": { "href": "http://localhost:8080/clients/16" },
                                     "delete": { "href": "http://localhost:8080/clients/16" }
                                 }
                             }
                         ]
                     },
                     "_links": {
                         "self": { "href": "http://localhost:8080/clients?page=1&size=2" },
                         "next": { "href": "http://localhost:8080/clients?page=2&size=2" },
                         "previous": { "href": "http://localhost:8080/clients?page=0&size=2" }
                     },
                     "page": { "size": 2, "totalElements": 6, "totalPages": 3, "number": 1 }
                 }
                """;

        JSONObject response = new JSONObject(clientsResponseJson);

        ClientPage clientPage = Parser.getClientsPageable(response);
        ArrayList<Client> clients = clientPage.clients();

        assertNotNull(clients);
        assertEquals(2, clients.size());

        Client firstClient = clients.get(0);
        assertEquals(10, firstClient.getId());
        assertEquals("Intermarche", firstClient.getCompanyName());
        assertEquals(48.8566, firstClient.getLatHomeAddress());
        assertEquals(2.3522, firstClient.getLongHomeAddress());
        assertEquals(ClientCategory.CLIENT.category(), firstClient.getClientCategory());
        assertEquals("Description A", firstClient.getDescription());
        assertEquals("Doe", firstClient.getContactLastName());
        assertEquals("John", firstClient.getContactFirstName());
        assertEquals("0123456789", firstClient.getPhoneNumber());
    }

    @Test
    public void testGetItinerariesPageable() throws JSONException {
        String itineraryResponseJson = """
                {
                    "_embedded": {
                        "itineraryResponseModelList": [
                            {
                                "id": "679c7e2687ba45366b4b39e8",
                                "salesman_home": {
                                    "x": 44.36017116455328,
                                    "y": 2.5767227655364024,
                                    "type": "Point",
                                    "coordinates": [44.36017116455328, 2.5767227655364024]
                                },
                                "clients_schedule": [
                                    {
                                        "id": 7,
                                        "companyLocation": {
                                            "x": 2.5680542079520023,
                                            "y": 44.36224183353758,
                                            "type": "Point",
                                            "coordinates": [
                                                2.5680542079520023,
                                                44.36224183353758
                                            ]
                                        },
                                        "companyName": "Liddle"
                                    },
                                    {
                                        "id": 8,
                                        "companyLocation": {
                                            "x": 2.555959091843846,
                                            "y": 44.36076301822145,
                                            "type": "Point",
                                            "coordinates": [
                                                2.555959091843846,
                                                44.36076301822145
                                            ]
                                        },
                                        "companyName": "Spar"
                                    },
                                    {
                                        "id": 10,
                                        "companyLocation": {
                                            "x": 2.3522,
                                            "y": 48.8566,
                                            "type": "Point",
                                            "coordinates": [2.3522, 48.8566]
                                        },
                                        "companyName": "Intermarché"
                                    },
                                    {
                                        "id": 17,
                                        "companyLocation": {
                                            "x": 2.673797607421875,
                                            "y": 49.135002605812176,
                                            "type": "Point",
                                            "coordinates": [
                                                2.673797607421875,
                                                49.135002605812176
                                            ]
                                        },
                                        "companyName": "Super U"
                                    }
                                ],
                                "_links": {
                                    "self": [
                                        {
                                            "href": "http://localhost:8080/itineraries/679c7e2687ba45366b4b39e8"
                                        },
                                        {
                                            "href": "http://localhost:8080/itineraries/679c7e2687ba45366b4b39e8"
                                        }
                                    ]
                                }
                            }
                        ]
                    },
                    "_links": {
                        "self": { "href": "http://localhost:8080/itineraries?page=0&size=20" }
                    },
                    "page": { "size": 20, "totalElements": 1, "totalPages": 1, "number": 0 }
                }
                """;

        JSONObject response = new JSONObject(itineraryResponseJson);

        ItineraryPage itineraryPage = Parser.getItinerariesPageable(response);
        List<Itinerary> itineraries = itineraryPage.itineraries();

        assertNotNull(itineraries);
        assertEquals(1, itineraries.size());

        Itinerary firstItinerary = itineraries.get(0);
        assertEquals("679c7e2687ba45366b4b39e8", firstItinerary.getId());
        assertEquals(2.5767227655364024, firstItinerary.getSalesmanLatitude());
        assertEquals(44.36017116455328, firstItinerary.getSalesmanLongitude());
        assertEquals(4, firstItinerary.getClients().size());

        Client firstClient = firstItinerary.getClients().get(0);
        assertEquals(7, firstClient.getId());
        assertEquals("Liddle", firstClient.getCompanyName());
        assertEquals(44.36224183353758, firstClient.getLatHomeAddress());
        assertEquals(2.5680542079520023, firstClient.getLongHomeAddress());
    }

    @Test
    public void testGetRoute() throws JSONException {
        String routeResponseJson = """
                {
                     "id": "67ab0e87ec091a7d076a63f0",
                     "salesman_home": {
                         "x": 0.0,
                         "y": 0.0,
                         "type": "Point",
                         "coordinates": [
                             0.0,
                             0.0
                         ]
                     },
                     "clients": [
                         {
                             "client": {
                                 "id": 1,
                                 "companyLocation": {
                                     "x": 2.3522,
                                     "y": 48.8566,
                                     "type": "Point",
                                     "coordinates": [
                                         2.3522,
                                         48.8566
                                     ]
                                 },
                                 "companyName": "hp"
                             },
                             "state": "EXPECTED"
                         },
                         {
                             "client": {
                                 "id": 2,
                                 "companyLocation": {
                                     "x": 2.3522,
                                     "y": 48.8566,
                                     "type": "Point",
                                     "coordinates": [
                                         2.3522,
                                         48.8566
                                     ]
                                 },
                                 "companyName": "hp"
                             },
                             "state": "EXPECTED"
                         },
                         {
                             "client": {
                                 "id": 3,
                                 "companyLocation": {
                                     "x": 2.3522,
                                     "y": 48.8566,
                                     "type": "Point",
                                     "coordinates": [
                                         2.3522,
                                         48.8566
                                     ]
                                 },
                                 "companyName": "hp"
                             },
                             "state": "EXPECTED"
                         }
                     ],
                     "startDate": "2025-02-11T08:47:03.298+00:00",
                     "salesman_current_position": {
                         "x": 0.0,
                         "y": 0.0,
                         "type": "Point",
                         "coordinates": [
                             0.0,
                             0.0
                         ]
                     },
                     "_links": {
                         "self": {
                             "href": "http://localhost:8080/routes/67ab0e87ec091a7d076a63f0"
                         }
                     }
                 }
                """;

        JSONObject response = new JSONObject(routeResponseJson);

        Route route = Parser.getRoute(response);

        assertNotNull(route);
        assertEquals("67ab0e87ec091a7d076a63f0", route.getId());
        assertEquals(0.0, route.getSalesmanHome().getLatitude());
        assertEquals(0.0, route.getSalesmanHome().getLongitude());
        LocalDateTime expectedStartDate = LocalDateTime.of(2025, 2, 11, 8, 47, 03, 298 * 1_000_000);
        assertEquals(expectedStartDate, route.getStartDate());
        assertEquals(3, route.getClients().size());
        assertEquals(0.0, route.getCurrentSalesmanPosition().getLatitude());
        assertEquals(0.0, route.getCurrentSalesmanPosition().getLongitude());

        RouteClient firstClient = route.getClients().get(0);
        assertEquals(1, firstClient.getClient().getId());
        assertEquals("hp", firstClient.getClient().getCompanyName());
        assertEquals(48.8566, firstClient.getClient().getLatHomeAddress());
        assertEquals(2.3522, firstClient.getClient().getLongHomeAddress());
    }

    @Test
    @DisplayName("Parse route when current position is null")
    public void testGetRouteCurrentPositionNull() throws JSONException {
        String routeResponseJson = """
                {
                "id": "67d5ac5a88283415862bfa10",
                "salesman_home": {
                    "x": 1.0,
                    "y": 0.0,
                    "type": "Point",
                    "coordinates": [1.0, 0.0]
                },
                "clients": [
                    {
                        "client": {
                            "id": 1,
                            "companyLocation": {
                                "x": 2.3522,
                                "y": 48.8566,
                                "type": "Point",
                                "coordinates": [2.3522, 48.8566]
                            },
                            "companyName": "Client"
                        },
                        "state": "EXPECTED"
                    }
                ],
                "startDate": "2025-03-15T16:35:38.221+00:00",
                "salesman_current_position": null,
                "state": "NOT_STARTED",
                "_links": {
                    "self": {
                        "href": "http://localhost:8080/routes/67d5ac5a88283415862bfa10"
                    }
                }
                }
                """;

        JSONObject response = new JSONObject(routeResponseJson);

        Route route = Parser.getRoute(response);

        assertNotNull(route);
        assertEquals("67d5ac5a88283415862bfa10", route.getId());
        assertEquals(0.0, route.getSalesmanHome().getLatitude());
        assertEquals(1.0, route.getSalesmanHome().getLongitude());
        LocalDateTime expectedStartDate = LocalDateTime.of(2025, 3, 15, 16, 35, 38, 221 * 1_000_000);
        assertEquals(expectedStartDate, route.getStartDate());
        assertEquals(1, route.getClients().size());
        assertNull(route.getCurrentSalesmanPosition());

        RouteClient firstClient = route.getClients().get(0);
        assertEquals(1, firstClient.getClient().getId());
        assertEquals("Client", firstClient.getClient().getCompanyName());
        assertEquals(48.8566, firstClient.getClient().getLatHomeAddress());
        assertEquals(2.3522, firstClient.getClient().getLongHomeAddress());
    }

    @Test
    public void testGetRouteButEmptyArray() throws JSONException {
        String routeResponseJson = """
                {
                "nothing": []
                }
                """;

        JSONObject response = new JSONObject(routeResponseJson);

        Route route = Parser.getRoute(response);

        assertNull(route);
    }

}