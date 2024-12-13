package fr.iut_rodez.pathpilot_android_client.home;

import static fr.iut_rodez.pathpilot_android_client.home.clients.AddClient.CLE_CLIENT_ADDED;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

import fr.iut_rodez.pathpilot_android_client.R;
import fr.iut_rodez.pathpilot_android_client.home.clients.FragmentClients;
import fr.iut_rodez.pathpilot_android_client.home.clients.FragmentClients.AddClient;
import fr.iut_rodez.pathpilot_android_client.login.JWTToken;
import fr.iut_rodez.pathpilot_android_client.login.LoginService;
import fr.iut_rodez.pathpilot_android_client.model.Client;

/**
 * Handle the different fragments of the application and the JWT token.
 * The JWT token is passed from the login activity to the home activity.
 */
public class Home extends AppCompatActivity implements AddClient {

    private static final String TAG = Home.class.getSimpleName();
    public static final int INDEX_FRAGMENT_CLIENT = 0;

    private ViewPager2 viewPager;
    private TabLayout tabManager;

    private JWTToken JWTToken;

    private ActivityResultLauncher<Intent> addClientLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_activity_home);

        viewPager = findViewById(R.id.view_pager);
        tabManager = findViewById(R.id.tab_layout);

        viewPager.setAdapter(new FragmentAdapter(this));

        int[] icons = {
                FragmentClients.ICON
        };

        new TabLayoutMediator(tabManager, viewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        // Get the custom view of the tab
                        View customView = LayoutInflater.from(Home.this).inflate(R.layout.tab_icon, null);
                        // Set the icon in the custom view
                        ImageView tabIcon = customView.findViewById(R.id.tab_icon);
                        tabIcon.setImageResource(icons[position]);
                        // Set the custom view of the tab
                        tab.setCustomView(customView);
                    }
                }
        ).attach();

        // Get the token from the intent
        Intent intent = getIntent();

        JWTToken = intent.getParcelableExtra(LoginService.CLE_TOKEN);

        addClientLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::returnFromAddClient);
    }

    public JWTToken getJWTToken() {
        return JWTToken;
    }

    private void returnFromAddClient(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK) {
            Log.d(TAG, "onCreate: Add client");
            Log.d(TAG, "onCreate: " + result.getData());

            // Goto the client fragment
            viewPager.setCurrentItem(0);

            // Load the clients
            if (result.getData() != null
                    && result.getData().hasExtra(CLE_CLIENT_ADDED)
                    && result.getData().getBooleanExtra(CLE_CLIENT_ADDED, false)) {

                FragmentClients fragmentClients = (FragmentClients) getSupportFragmentManager().getFragments().get(INDEX_FRAGMENT_CLIENT);
                fragmentClients.loadClients();
            }
        }
    }

    @Override
    public ActivityResultLauncher<Intent> getAddClientLauncher() {
        return addClientLauncher;
    }

    public ArrayList<Client> getClients() {
        return ((FragmentClients) getSupportFragmentManager().getFragments().get(INDEX_FRAGMENT_CLIENT)).getListClients();
    }
}
