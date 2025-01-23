package fr.iut_rodez.pathpilot_android_client.home.clients;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import fr.iut_rodez.pathpilot_android_client.R;
import fr.iut_rodez.pathpilot_android_client.home.clients.Client;

/**
 * Adapter for the list of clients.
 */
public class ClientArrayAdapter extends ArrayAdapter<Client> {

    private Context context;
    private List<Client> clients;

    public ClientArrayAdapter(@NonNull Context context, List<Client> clients) {
        super(context, -1, clients);
        this.context = context;
        this.clients = clients;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Inflate le layout personnalisé
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.client_list_item, parent, false);

        // Récupérer les TextView du layout
        TextView clientName = rowView.findViewById(R.id.client_name);
        TextView clientAddress = rowView.findViewById(R.id.client_address);
        TextView clientCategory = rowView.findViewById(R.id.client_category);
        TextView clientDescription = rowView.findViewById(R.id.client_description);
        TextView clientContactFirstName = rowView.findViewById(R.id.client_contact_first_name);
        TextView clientContactLastName = rowView.findViewById(R.id.client_contact_last_name);
        TextView clientPhoneNumber = rowView.findViewById(R.id.client_contact_phone);

        // Récupérer le client à cette position
        Client client = clients.get(position);

        // Définir les valeurs des TextView
        clientName.setText(client.getCompanyName());
        clientAddress.setText(client.getHomeAddress());
        clientCategory.setText(client.getClientCategory());
        clientDescription.setText(client.getDescription());
        clientContactFirstName.setText(client.getContactFirstName());
        clientContactLastName.setText(client.getContactLastName());
        clientPhoneNumber.setText(client.getPhoneNumber());

        return rowView;
    }
}
