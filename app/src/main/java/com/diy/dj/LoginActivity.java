package com.diy.dj;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import static com.spotify.sdk.android.authentication.AuthenticationClient.getResponse;
import static com.spotify.sdk.android.authentication.AuthenticationClient.openLoginActivity;
import static com.spotify.sdk.android.authentication.AuthenticationRequest.Builder;

public class LoginActivity extends AppCompatActivity {

    private SpotifyContacts spotifyContacts;

    public static void start(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        spotifyContacts = new SpotifyContacts();
        spotifyLogin(this);
    }

    public void spotifyLogin(Context context){
        String clientID = spotifyContacts.getCLIENT_ID();
        String redirect_uri = spotifyContacts.getREDIRECT_URI();
        int request_code = spotifyContacts.getREQUEST_CODE();

        Builder builder =
                new Builder(clientID, AuthenticationResponse.Type.TOKEN, redirect_uri);
        builder.setScopes(new String[]{"streaming","playlist-modify-private", "playlist-modify-public"});
        AuthenticationRequest request = builder.build();
        openLoginActivity(this, request_code, request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == spotifyContacts.getREQUEST_CODE()) {
            AuthenticationResponse response = getResponse(resultCode, intent);

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    spotifyContacts = new SpotifyContacts();
                    spotifyContacts.setTOKEN(response.getAccessToken());
                    spotifyContacts.connectToDevice(this);
                    ChooseGenres.start(this, response);
                    // Handle successful response
                    return;

                // Auth flow returned an error
                case ERROR:
                    Log.e("error", response.getError());
                    ErrorActivity.start(this, response.getError());
                    // Handle error response
                    return;

                // Most likely auth flow was cancelled
                default:
                    PartyActivity.start(this,-2, -2, spotifyContacts);
                    // Handle other cases
            }
        }
    }
}
