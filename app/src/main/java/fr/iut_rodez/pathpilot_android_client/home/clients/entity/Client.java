package fr.iut_rodez.pathpilot_android_client.home.clients.entity;

import static fr.iut_rodez.pathpilot_android_client.home.clients.entity.Client.ClientConstant.COMPANY_NAME_JSON_KEY;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

import java.text.MessageFormat;
import java.util.List;

import fr.iut_rodez.pathpilot_android_client.util.Parser;
import fr.iut_rodez.pathpilot_android_client.util.map.LocationNameProvider;

/**
 * Class representing a client.
 */
public class Client implements Parcelable {

    private int id;
    private String companyName;
    private double latHomeAddress;
    private double longHomeAddress;
    private String addressDisplayName;
    private ClientCategory clientCategory = ClientCategory.CLIENT;
    private String description;
    private String contactLastName;
    private String contactFirstName;
    private String phoneNumber;
    private String salesman;

    public Client(int id, String companyName, double latHomeAddress, double longHomeAddress, ClientCategory clientCategory, String description, String contactLastName, String contactFirstName, String phoneNumber, String salesman) {
        this.id = id;
        this.companyName = companyName;
        this.latHomeAddress = latHomeAddress;
        this.longHomeAddress = longHomeAddress;
        this.clientCategory = clientCategory;
        this.description = description;
        this.contactLastName = contactLastName;
        this.contactFirstName = contactFirstName;
        this.phoneNumber = phoneNumber;
    }

    public Client(JSONObject clientJson) throws JSONException {
        this.id = clientJson.getInt("id");
        this.companyName = clientJson.getString(COMPANY_NAME_JSON_KEY);
        this.latHomeAddress = clientJson.getDouble("latHomeAddress");
        this.longHomeAddress = clientJson.getDouble("longHomeAddress");
        this.clientCategory = ClientCategory.fromJSON(clientJson.getJSONObject("clientCategory"));
        this.description = clientJson.getString("description");
        this.contactLastName = clientJson.getString("contactLastName");
        this.contactFirstName = clientJson.getString("contactFirstName");
        this.phoneNumber = clientJson.getString("phoneNumber");
    }

    protected Client(Parcel in) {
        id = in.readInt();
        companyName = in.readString();
        latHomeAddress = in.readDouble();
        longHomeAddress = in.readDouble();
        addressDisplayName = in.readString();
        clientCategory = ClientCategory.fromString(in.readString());
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

    public Client(String companyName, double latitudeValue, double longitude, String descriptionText, Boolean isClient, String firstNameText, String lastNameText, String phoneNumber) {
        this.companyName = companyName;
        this.latHomeAddress = latitudeValue;
        this.longHomeAddress = longitude;
        this.clientCategory = isClient == null || isClient ? ClientCategory.CLIENT : ClientCategory.PROSPECT;
        this.description = descriptionText;
        this.contactLastName = lastNameText;
        this.contactFirstName = firstNameText;
        this.phoneNumber = phoneNumber;
    }

    /**
     * Constructor for Itineraries clients
     *
     * @param idClient        id of the client
     * @param companyName     the client's companyName
     * @param latHomeAddress  the latitude of the client's home address
     * @param longHomeAddress the longitude of the client's home address
     */
    public Client(int idClient, String companyName, double latHomeAddress, double longHomeAddress) {
        this.id = idClient;
        this.companyName = companyName;
        this.latHomeAddress = latHomeAddress;
        this.longHomeAddress = longHomeAddress;
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

    public String getClientCategory() {
        return clientCategory.category();
    }

    public void setClientCategory(String clientCategory) {
        this.clientCategory = ClientCategory.fromString(clientCategory);
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

    @NonNull
    public String getAddressDisplayName() {
        return addressDisplayName != null ? addressDisplayName : "";
    }

    /**
     * Set the address name of the client.
     *
     * @param context context of the Geocoder.
     */
    public void setAddressDisplayName(Context context) {
        this.addressDisplayName = LocationNameProvider.getAddressName(context, getGeoPoint());
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
                ", addressDisplayName='" + addressDisplayName + '\'' +
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

    public JSONObject toJson() throws JSONException {
        JSONObject clientJson = new JSONObject();

        clientJson.put(COMPANY_NAME_JSON_KEY, companyName);
        clientJson.put("latHomeAddress", latHomeAddress);
        clientJson.put("longHomeAddress", longHomeAddress);
        clientJson.put("clientCategory", clientCategory.category());
        clientJson.put("description", description);
        clientJson.put("contactLastName", contactLastName);
        clientJson.put("contactFirstName", contactFirstName);
        clientJson.put("phoneNumber", phoneNumber);
        clientJson.put("salesman", salesman);

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
        dest.writeString(this.addressDisplayName);
        dest.writeString(this.clientCategory.category());
        dest.writeString(this.description);
        dest.writeString(this.contactLastName);
        dest.writeString(this.contactFirstName);
        dest.writeString(this.phoneNumber);
        dest.writeString(this.salesman);
    }

    public GeoPoint getGeoPoint() {
        return new GeoPoint(latHomeAddress, longHomeAddress);
    }

    /**
     * Create a client from a short JSON object
     * <p>
     * When we retrieve an itinerary, the list of clients is also given.
     * But we dont need and dont have all the information of the client.<br>
     * So, this method is used to create a client from a short JSON object. With only the:
     *     <ul>
     *         <li>id</li>
     *         <li>companyName</li>
     *         <li>companyLocation</li>
     *         <li>clientState</li>
 *         </ul>
     * </p>
     *
     * @param clientJson the short JSON object
     * @return the client created from the short JSON object
     */
    public static Client createClientFromShortJson(JSONObject clientJson) throws JSONException {
        GeoPoint companyLocation = Parser.getGeoPointFromGeoJSONPoint(clientJson.getJSONObject("companyLocation"));
        return new Client(
                clientJson.getInt("id"),
                clientJson.getString(COMPANY_NAME_JSON_KEY),
                companyLocation.getLatitude(),
                companyLocation.getLongitude()
        );
    }

    /**
     * Convert the list of clients to a string list used in route / itinerary.
     *
     * @param clients The list of clients
     * @return The string representation of the list of clients
     */
    public static String getClientsDisplay(List<Client> clients) {
        StringBuilder clientNames = new StringBuilder();
        for (int i = 0; i < clients.size(); i++) {
            clientNames.append(MessageFormat.format("{0}. {1}", i + 1, clients.get(i).toShortString()));
            if (i < clients.size() - 1) {
                clientNames.append("\n");
            }
        }
        return clientNames.toString();
    }

    static class ClientConstant {
        //private constructor to prevent instantiation
        private ClientConstant() {
        }

        public static final String COMPANY_NAME_JSON_KEY = "companyName";
    }

    public String layoutClientItemList() {
        return getCompanyName() + " - " + getAddressDisplayName();
    }
}
