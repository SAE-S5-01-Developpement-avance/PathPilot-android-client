package fr.iut_rodez.pathpilot_android_client.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import fr.iut_rodez.pathpilot_android_client.R;

public class Client implements Parcelable {

    private int id;
    private String companyName;
    private double latHomeAddress;
    private double longHomeAddress;
    private String clientCategory;
    private String description;
    private String contactLastName;
    private String contactFirstName;
    private String phoneNumber;
    private String salesman;

    public Client(int id, String companyName, double latHomeAddress, double longHomeAddress, String clientCategory, String description, String contactLastName, String contactFirstName, String phoneNumber, String salesman) {
        this.id = id;
        this.companyName = companyName;
        this.latHomeAddress = latHomeAddress;
        this.longHomeAddress = longHomeAddress;
        this.clientCategory = clientCategory;
        this.description = description;
        this.contactLastName = contactLastName;
        this.contactFirstName = contactFirstName;
        this.phoneNumber = phoneNumber;
        this.salesman = salesman;
    }

    public Client(JSONObject clientJson) throws JSONException {
        this.id = clientJson.getInt("id");
        this.companyName = clientJson.getString("companyName");
        this.latHomeAddress = clientJson.getDouble("latHomeAddress");
        this.longHomeAddress = clientJson.getDouble("longHomeAddress");
        this.clientCategory = clientJson.getString("clientCategory");
        this.description = clientJson.getString("description");
        this.contactLastName = clientJson.getString("contactLastName");
        this.contactFirstName = clientJson.getString("contactFirstName");
        this.phoneNumber = clientJson.getString("phoneNumber");
        this.salesman = clientJson.getString("salesman");
    }

    protected Client(Parcel in) {
        id = in.readInt();
        companyName = in.readString();
        latHomeAddress = in.readDouble();
        longHomeAddress = in.readDouble();
        clientCategory = in.readString();
        description = in.readString();
        contactLastName = in.readString();
        contactFirstName = in.readString();
        phoneNumber = in.readString();
        salesman = in.readString();
    }

    public static final Creator<Client> CREATOR = new Creator<Client>() {
        @Override
        public Client createFromParcel(Parcel in) {
            return new Client(in);
        }

        @Override
        public Client[] newArray(int size) {
            return new Client[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public double getLatHomeAddress() {
        return latHomeAddress;
    }

    public void setLatHomeAddress(double latHomeAddress) {
        this.latHomeAddress = latHomeAddress;
    }

    public double getLongHomeAddress() {
        return longHomeAddress;
    }

    public void setLongHomeAddress(double longHomeAddress) {
        this.longHomeAddress = longHomeAddress;
    }

    public String getHomeAddress() {
        String nOrS = latHomeAddress > 0 ? "N" : "S";
        String eOrW = longHomeAddress > 0 ? "E" : "W";

        return String.format("%.2f°%s, %.2f°%s", latHomeAddress, nOrS, longHomeAddress, eOrW);
    }

    public String getClientCategory() {
        return clientCategory;
    }

    public void setClientCategory(String clientCategory) {
        this.clientCategory = clientCategory;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContactLastName() {
        return contactLastName;
    }

    public void setContactLastName(String contactLastName) {
        this.contactLastName = contactLastName;
    }

    public String getContactFirstName() {
        return contactFirstName;
    }

    public void setContactFirstName(String contactFirstName) {
        this.contactFirstName = contactFirstName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getSalesman() {
        return salesman;
    }

    public void setSalesman(String salesman) {
        this.salesman = salesman;
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", companyName='" + companyName + '\'' +
                ", latHomeAddress=" + latHomeAddress +
                ", longHomeAddress=" + longHomeAddress +
                ", clientCategory='" + clientCategory + '\'' +
                ", description='" + description + '\'' +
                ", contactLastName='" + contactLastName + '\'' +
                ", contactFirstName='" + contactFirstName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", salesman='" + salesman + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.companyName);
        dest.writeDouble(this.latHomeAddress);
        dest.writeDouble(this.longHomeAddress);
        dest.writeString(this.clientCategory);
        dest.writeString(this.description);
        dest.writeString(this.contactLastName);
        dest.writeString(this.contactFirstName);
        dest.writeString(this.phoneNumber);
        dest.writeString(this.salesman);
    }

    /**
     * Adapter for the list of clients.
     */
    public static class ClientArrayAdapter extends ArrayAdapter<Client> {

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
}
