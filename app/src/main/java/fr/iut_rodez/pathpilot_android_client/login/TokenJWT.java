package fr.iut_rodez.pathpilot_android_client.login;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Date;

public class TokenJWT implements Parcelable {
    private final String token;
    /**
     *
     */
    private final long expiresIn;

    public TokenJWT(String token, long expiresIn) {
        this.token = token;
        this.expiresIn = expiresIn;
    }

    public String getToken() {
        return token;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    /**
     * Get the expiration date of the token.
     *
     * @return the expiration date of the token
     */
    public Date getExpirationDate() {
        return new Date(System.currentTimeMillis() + expiresIn);
    }

    protected TokenJWT(Parcel in) {
        token = in.readString();
        expiresIn = in.readLong();
    }

    public static final Creator<TokenJWT> CREATOR = new Creator<TokenJWT>() {
        @Override
        public TokenJWT createFromParcel(Parcel in) {
            return new TokenJWT(in);
        }

        @Override
        public TokenJWT[] newArray(int size) {
            return new TokenJWT[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(token);
        dest.writeLong(expiresIn);
    }
}