package fr.iut_rodez.pathpilot_android_client.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import fr.iut_rodez.pathpilot_android_client.R;
import fr.iut_rodez.pathpilot_android_client.home.clients.FragmentClients;

public class Home extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TabLayout tabManager;

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
    }
}
