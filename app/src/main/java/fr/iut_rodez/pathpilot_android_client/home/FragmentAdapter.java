package fr.iut_rodez.pathpilot_android_client.home;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import fr.iut_rodez.pathpilot_android_client.home.clients.FragmentClients;
import fr.iut_rodez.pathpilot_android_client.itinerary.FragmentItineraries;

public class FragmentAdapter extends FragmentStateAdapter {

    private static final int NUMBER_OF_FRAGMENT = 2;

    public FragmentAdapter(FragmentActivity activity) {
        super(activity);
    }

    @Override
    public Fragment createFragment(int position) {
        return switch (position) {
            case 0 -> FragmentClients.newInstance();
            case 1 -> FragmentItineraries.newInstance();
            default -> null;
        };
    }

    @Override
    public int getItemCount() {
        return NUMBER_OF_FRAGMENT;
    }
}
