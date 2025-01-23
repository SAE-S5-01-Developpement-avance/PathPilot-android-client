package fr.iut_rodez.pathpilot_android_client.home.clients;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class representing a client.
 */
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
        this.clientCategory = "Type: " + clientJson.getJSONObject("clientCategory").getString("name");
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

    public Client(String companyName, double latitudeValue, double longitude, String descriptionText, boolean isClient, String firstNameText, String lastNameText, String phoneNumber) {
        this.companyName = companyName;
        this.latHomeAddress = latitudeValue;
        this.longHomeAddress = longitude;
        this.clientCategory = isClient ? "Client" : "Prospect";
        this.description = descriptionText;
        this.contactLastName = lastNameText;
        this.contactFirstName = firstNameText;
        this.phoneNumber = phoneNumber;
    }

    /**
     * Constructor for Itineraries clients
     *
     * @param id           id of the client
     * @param companyName  the client's companyName
     */
    public Client(int id, String companyName) {
        this.id = id;
        this.companyName = companyName;
    }

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

    /**
     * Return a short string representation of the client using the companyName
     *
     * @return the short string representation of the client
     */
    public String toShortString() {
        return companyName;
    }

    public JSONObject toJson() {
        JSONObject clientJson = new JSONObject();
        try {
            clientJson.put("companyName", companyName);
            clientJson.put("latHomeAddress", latHomeAddress);
            clientJson.put("longHomeAddress", longHomeAddress);
            clientJson.put("clientCategory", clientCategory);
            clientJson.put("description", description);
            clientJson.put("contactLastName", contactLastName);
            clientJson.put("contactFirstName", contactFirstName);
            clientJson.put("phoneNumber", phoneNumber);
            clientJson.put("salesman", salesman);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return clientJson;
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
}
