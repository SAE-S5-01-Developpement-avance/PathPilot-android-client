package fr.iut_rodez.pathpilot_android_client.home.clients;

import android.graphics.drawable.Drawable;

import androidx.fragment.app.Fragment;

import fr.iut_rodez.pathpilot_android_client.R;

public class FragmentClients extends Fragment {

    public static final int ICON = R.drawable.icone_peoples;

    public static FragmentClients newInstance() {
        return new FragmentClients();
    }

    private FragmentClients() {
    }
}
