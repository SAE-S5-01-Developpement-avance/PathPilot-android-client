package fr.iut_rodez.pathpilot_android_client.home;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import fr.iut_rodez.pathpilot_android_client.home.clients.FragmentClients;
import fr.iut_rodez.pathpilot_android_client.home.itinerary.FragmentItineraries;
import fr.iut_rodez.pathpilot_android_client.home.routes.FragmentRoutes;

public class FragmentAdapter extends FragmentStateAdapter {

    private static final int NUMBER_OF_FRAGMENT = 3;

    public FragmentAdapter(FragmentActivity activity) {
        super(activity);
    }

    @Override
    public Fragment createFragment(int position) {
        return switch (position) {
            case 0 -> FragmentClients.newInstance();
            case 1 -> FragmentItineraries.newInstance();
            case 2 -> FragmentRoutes.newInstance();
            default -> null;
        };
    }

    @Override
    public int getItemCount() {
        return NUMBER_OF_FRAGMENT;
    }
}
