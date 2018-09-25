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

    private static final String CLIENT_ID = "89f69947e5df4167818e334f46abef71";
    private static final int REQUEST_CODE = 1337;
    private static final String REDIRECT_URI = "com.diy.dj://callback";


    public static void start(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        spotifyLogin(this);
    }

    public void spotifyLogin(Context context){
        Builder builder =
                new Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
        builder.setScopes(new String[]{"streaming"});
        AuthenticationRequest request = builder.build();
        openLoginActivity(this, REQUEST_CODE, request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = getResponse(resultCode, intent);

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
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
                    PartyActivity.start(this,-2, -2);
                    // Handle other cases
            }
        }
    }
}
