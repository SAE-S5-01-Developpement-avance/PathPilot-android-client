package fr.iut_rodez.pathpilot_android_client.home.routes;

import static fr.iut_rodez.pathpilot_android_client.home.routes.FragmentRoutes.JWT_TOKEN_KEY;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import fr.iut_rodez.pathpilot_android_client.R;
import fr.iut_rodez.pathpilot_android_client.home.clients.entity.ClientState;
import fr.iut_rodez.pathpilot_android_client.home.routes.entity.Route;
import fr.iut_rodez.pathpilot_android_client.home.routes.entity.RouteClient;
import fr.iut_rodez.pathpilot_android_client.login.JWTToken;
import fr.iut_rodez.pathpilot_android_client.player.PlayerItinerary;
import fr.iut_rodez.pathpilot_android_client.util.popup.DialogButton;
import fr.iut_rodez.pathpilot_android_client.util.popup.Popup;

public class InfoRoute extends AppCompatActivity {

    private static final String TAG = InfoRoute.class.getSimpleName();
    public static final String ROUTE_KEY = "route";

    private Route route;
    private Popup popup;
    private RecyclerView timelineRecyclerView;
    private JWTToken jwtToken;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_info_route);

        // Initialize views
        timelineRecyclerView = findViewById(R.id.timeline_recycler_view);
        findViewById(R.id.resume_button).setOnClickListener(v -> resumeRoute());
        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        popup = new Popup(this);

        // Set up RecyclerView
        timelineRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        setUpTimelineClients();
        setUpToken();
    }

    /**
     * Set up the timeline of clients for the route.
     * <p>
     * Retrieves the route from the intent and converts route stops to timeline items.
     * Sets up the RecyclerView with the timeline adapter.
     * </p>
     */
    private void setUpTimelineClients() {
        Intent intent = getIntent();
        route = intent.getParcelableExtra(ROUTE_KEY);

        if (route == null) {
            Log.e(TAG, "onCreate: No route found in the intent");
            popup.showAlertDialog(getString(R.string.error), getString(R.string.no_route_retrieved));
            finish();
            return;
        }

        // Set up clients
        ArrayList<RouteClient> clients = route.getClients().stream().peek(routeClient -> {
            routeClient.setState(getVisitStatus(routeClient));
            routeClient.getClient().setAddressDisplayName(this);
        }).collect(Collectors.toCollection(ArrayList::new));

        // Convert route stops to timeline items
        List<TimelineItem> timelineItems = clients.stream().map(TimelineItem::new).collect(Collectors.toList());
        // Set up adapter
        TimelineAdapter timelineAdapter = new TimelineAdapter(this, timelineItems);
        timelineRecyclerView.setAdapter(timelineAdapter);

        // Set header text to route ID
        ((TextView) findViewById(R.id.header_text)).setText(route.getId());

        // Update resume button text based on route status
        updateResumeButtonText();
    }

    /**
     * Get the visit status of a client.
     *
     * @param client The client to get the status of
     * @return The visit status of the client
     */
    private ClientState getVisitStatus(RouteClient client) {
        return switch (client.getState()) {
            case VISITED -> ClientState.VISITED;
            case EXPECTED -> ClientState.EXPECTED;
            default -> ClientState.SKIPPED;
        };
    }

    /**
     * Update the resume button text based on route status
     */
    private void updateResumeButtonText() {
        MaterialButton resumeButton = findViewById(R.id.resume_button);
        if (route.isCompleted()) {
            resumeButton.setText(R.string.route_completed);
            resumeButton.setEnabled(false);
        } else if (route.getIndexCurrentClient() == 0) {
            resumeButton.setText(R.string.button_start_route);
        } else {
            resumeButton.setText(R.string.button_resume_route);
        }
    }

    /**
     * Set up the token of the user.
     */
    private void setUpToken() {
        Intent intent = getIntent();
        if (intent.hasExtra(JWT_TOKEN_KEY)) {
            jwtToken = intent.getParcelableExtra(JWT_TOKEN_KEY);
        } else {
            Log.e(TAG, "setUpToken: No token found in the intent");
            popup.showAlertDialog(
                    getString(R.string.error),
                    getString(R.string.no_token_retrieve),
                    DialogButton.okFinish(this),
                    null,
                    null
            );
        }
    }

    /**
     * Resume the route from the current client.
     * <p>
     *     Redirects the user to the player activity with the current route.
     */
    private void resumeRoute() {
        Intent intent = new Intent(this, PlayerItinerary.class);
        intent.putExtra(ROUTE_KEY, route);
        intent.putExtra(JWT_TOKEN_KEY, jwtToken);
        startActivity(intent);
    }

    public JWTToken getJwtToken() {
        return jwtToken;
    }

    public Popup getPopup() {
        return popup;
    }
}