package com.diy.dj;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import static com.spotify.sdk.android.authentication.AuthenticationClient.*;
import static com.spotify.sdk.android.authentication.AuthenticationRequest.*;

public class MainActivity extends AppCompatActivity {
    private static final String CLIENT_ID = "89f69947e5df4167818e334f46abef71";
    private static final int REQUEST_CODE = 1337;
    private static final String REDIRECT_URI = "com.diy.dj://callback";

    public  void openJoin(View view){ JoinActivity.start(this);}

    public  void openLogin(View view){

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
