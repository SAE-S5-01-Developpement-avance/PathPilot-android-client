package fr.iut_rodez.pathpilot_android_client.home.routes.entity;

import android.os.Parcel;
import android.os.Parcelable;

import fr.iut_rodez.pathpilot_android_client.home.clients.entity.Client;
import fr.iut_rodez.pathpilot_android_client.home.clients.entity.ClientState;

public class RouteClient implements Parcelable {
    private Client client;
    private ClientState state;

    public RouteClient(Client client, ClientState state) {
        this.client = client;
        this.state = state;
    }

    protected RouteClient(Parcel in) {
        client = in.readParcelable(Client.class.getClassLoader());
        state = ClientState.valueOf(in.readString());
    }

    public static final Parcelable.Creator<RouteClient> CREATOR = new Parcelable.Creator<RouteClient>() {
        @Override
        public RouteClient createFromParcel(Parcel in) {
            return new RouteClient(in);
        }

        @Override
        public RouteClient[] newArray(int size) {
            return new RouteClient[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(client, flags);
        dest.writeString(state.name());
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public ClientState getState() {
        return state;
    }

    public void setState(ClientState state) {
        this.state = state;
    }


}