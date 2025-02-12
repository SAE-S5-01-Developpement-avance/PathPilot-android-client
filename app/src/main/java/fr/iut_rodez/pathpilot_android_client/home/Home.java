package fr.iut_rodez.pathpilot_android_client.home;

import static fr.iut_rodez.pathpilot_android_client.home.clients.AddClient.ADDED_CLIENT_KEY;
import static fr.iut_rodez.pathpilot_android_client.home.itinerary.AddItinerary.ITINERARY_ADDED_KEY;
import static fr.iut_rodez.pathpilot_android_client.home.routes.AddRoute.ADDED_ROUTE_KEY;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

import fr.iut_rodez.pathpilot_android_client.R;
import fr.iut_rodez.pathpilot_android_client.home.clients.FragmentClients;
import fr.iut_rodez.pathpilot_android_client.home.clients.FragmentClients.FragmentClientsActions;
import fr.iut_rodez.pathpilot_android_client.home.clients.entity.Client;
import fr.iut_rodez.pathpilot_android_client.home.clients.entity.ClientPage;
import fr.iut_rodez.pathpilot_android_client.home.itinerary.FragmentItineraries;
import fr.iut_rodez.pathpilot_android_client.home.itinerary.FragmentItineraries.FragmentItineraryActions;
import fr.iut_rodez.pathpilot_android_client.home.itinerary.entity.Itinerary;
import fr.iut_rodez.pathpilot_android_client.home.itinerary.entity.ItineraryPage;
import fr.iut_rodez.pathpilot_android_client.home.routes.FragmentRoutes;
import fr.iut_rodez.pathpilot_android_client.home.routes.FragmentRoutes.FragmentRouteActions;
import fr.iut_rodez.pathpilot_android_client.home.routes.entity.RoutePage;
import fr.iut_rodez.pathpilot_android_client.login.JWTToken;
import fr.iut_rodez.pathpilot_android_client.login.LoginService;

/**
 * Handle the different fragments of the application and the JWT token.
 * The JWT token is passed from the login activity to the home activity.
 */
public class Home extends AppCompatActivity implements FragmentClientsActions, FragmentItineraryActions, FragmentRouteActions {

    private static final String TAG = Home.class.getSimpleName();
    public static final int INDEX_FRAGMENT_CLIENT = 0;
    public static final int INDEX_FRAGMENT_ITINERARY = 1;
    public static final int INDEX_FRAGMENT_ROUTE = 2;

    private ViewPager2 viewPager;
    private TabLayout tabManager;

    private JWTToken JWTToken;

    private ActivityResultLauncher<Intent> addClientLauncher;
    private ActivityResultLauncher<Intent> addItineraryLauncher;
    private ActivityResultLauncher<Intent> addRouteLauncher;

    private ClientPage clientPage;
    private ItineraryPage itineraryPage;
    private RoutePage routePage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_activity_home);

        viewPager = findViewById(R.id.view_pager);
        tabManager = findViewById(R.id.tab_layout);

        viewPager.setAdapter(new FragmentAdapter(this));

        int[] icons = {
                FragmentClients.ICON,
                FragmentItineraries.ICON,
                FragmentRoutes.ICON
        };

        new TabLayoutMediator(tabManager, viewPager, (tab, position) -> {
            // Get the custom view of the tab
            View customView = LayoutInflater.from(Home.this).inflate(R.layout.tab_icon, null);
            // Set the icon in the custom view
            ImageView tabIcon = customView.findViewById(R.id.tab_icon);
            tabIcon.setImageResource(icons[position]);
            // Set the custom view of the tab
            tab.setCustomView(customView);
        }).attach();

        // Get the token from the intent
        Intent intent = getIntent();

        JWTToken = intent.getParcelableExtra(LoginService.TOKEN_KEY);

        addClientLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::returnFromAddClient);
        addItineraryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::returnFromAddItinerary);
        addRouteLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::returnFromAddRoute);
    }

    public JWTToken getJWTToken() {
        return JWTToken;
    }

    private void returnFromAddClient(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK) {
            Log.d(TAG, "onCreate: Add client");
            Log.d(TAG, "onCreate: " + result.getData());

            // Goto the client fragment
            viewPager.setCurrentItem(INDEX_FRAGMENT_CLIENT);

            // Load the clients if the creation was successful
            if (result.getData() != null
                    && result.getData().hasExtra(ADDED_CLIENT_KEY)
                    && result.getData().getBooleanExtra(ADDED_CLIENT_KEY, false)) {

                FragmentClients fragmentClients = (FragmentClients) getSupportFragmentManager().getFragments().get(INDEX_FRAGMENT_CLIENT);
                fragmentClients.loadClients();
            }
        }
    }

    private void returnFromAddItinerary(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK) {
            Log.d(TAG, "onCreate: Return From Add Itinerary");
            Log.d(TAG, "onCreate: " + result.getData());

            // Goto the itinerary fragment
            viewPager.setCurrentItem(INDEX_FRAGMENT_ITINERARY);

            // Load the clients if the creation was successful
            if (result.getData() != null
                    && result.getData().hasExtra(ITINERARY_ADDED_KEY)
                    && result.getData().getBooleanExtra(ITINERARY_ADDED_KEY, false)) {

                FragmentItineraries fragmentItineraries = (FragmentItineraries) getSupportFragmentManager().getFragments().get(INDEX_FRAGMENT_ITINERARY);
                fragmentItineraries.loadItineraries();
            }
        }
    }

    private void returnFromAddRoute(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK) {
            Log.d(TAG, "onCreate: Return From Add Route");
            Log.d(TAG, "onCreate: " + result.getData());

            // Goto the route fragment
            viewPager.setCurrentItem(INDEX_FRAGMENT_ROUTE);

            // Load the clients if the creation was successful
            if (result.getData() != null
                    && result.getData().hasExtra(ADDED_ROUTE_KEY)
                    && result.getData().getBooleanExtra(ADDED_ROUTE_KEY, false)) {

                FragmentRoutes fragmentRoutes = (FragmentRoutes) getSupportFragmentManager().getFragments().get(INDEX_FRAGMENT_ROUTE);
                fragmentRoutes.loadRoutes();
            }
        }
    }

    public ActivityResultLauncher<Intent> getAddClientLauncher() {
        return addClientLauncher;
    }

    /**
     * Set the client page
     *
     * @param clientPage The client page
     */
    public void setClientPage(ClientPage clientPage) {
        this.clientPage = clientPage;
    }

    /**
     * Set the itineraries page
     *
     * @param itineraryPage The itinerary page
     */
    public void setItineraryPage(ItineraryPage itineraryPage) {
        this.itineraryPage = itineraryPage;
    }

    /**
     * Set the routes page
     *
     * @param routePage The route page
     */
    public void setRoutePage(RoutePage routePage) {
        this.routePage = routePage;
    }

    @Override
    public ClientPage getClientPage() {
        return clientPage;
    }

    @Override
    public ItineraryPage getItineraryPage() {
        return itineraryPage;
    }

    @Override
    public RoutePage getRoutePage() {
        return routePage;
    }

    @Override
    public ActivityResultLauncher<Intent> getAddRouteLauncher() {
        return addRouteLauncher;
    }

    public ActivityResultLauncher<Intent> getAddItineraryLauncher() {
        return addItineraryLauncher;
    }

    public ArrayList<Client> getClients() {
        return ((FragmentClients) getSupportFragmentManager().getFragments().get(INDEX_FRAGMENT_CLIENT)).getListClients();
    }

    public ArrayList<Itinerary> getItineraries() {
        return ((FragmentItineraries) getSupportFragmentManager().getFragments().get(INDEX_FRAGMENT_ITINERARY)).getListItineraries();
    }
}
