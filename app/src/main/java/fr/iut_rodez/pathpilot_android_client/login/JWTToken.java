package fr.iut_rodez.pathpilot_android_client.login;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Date;

/**
 * Class representing a JWT token.
 */
public class JWTToken implements Parcelable {

    private final String token;
    private final long expiresIn;

    public JWTToken(String token, long expiresIn) {
        this.token = token;
        this.expiresIn = expiresIn;
    }

    /**
     * Get the token JWT.
     *
     * @return the token JWT
     * @throws IllegalStateException if the token JWT is expired
     */
    public String getToken() {
        if (getExpirationDate().before(new java.util.Date())) {
            throw new IllegalStateException("TokenJWT is expired");
        }

        return token;
    }

    /**
     * @return the expiration time of the token in milliseconds
     */
    public long getExpiresIn() {
        return expiresIn;
    }

    /**
     * Get the expiration date of the token.
     *
     * @return the expiration date of the token
     */
    public Date getExpirationDate() {
        return new Date(System.currentTimeMillis() + getExpiresIn());
    }

    protected JWTToken(Parcel in) {
        token = in.readString();
        expiresIn = in.readLong();
    }

    public static final Creator<JWTToken> CREATOR = new Creator<JWTToken>() {
        @Override
        public JWTToken createFromParcel(Parcel in) {
            return new JWTToken(in);
        }

        @Override
        public JWTToken[] newArray(int size) {
            return new JWTToken[size];
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