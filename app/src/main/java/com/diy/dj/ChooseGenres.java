package com.diy.dj;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.spotify.sdk.android.authentication.AuthenticationResponse;

public class ChooseGenres extends AppCompatActivity {



    private static AuthenticationResponse recResponse;
    private SpotifyContacts spotifyContacts;


    public static void start(Context context, AuthenticationResponse response){
        recResponse = response;
        Intent intent = new Intent(context, ChooseGenres.class);
        context.startActivity(intent);

    }

    public  void openSettings(View view){
        Settings.start(this, spotifyContacts);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choosegenres);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        spotifyContacts = new SpotifyContacts();
        spotifyContacts.setTOKEN(recResponse.getAccessToken());
        spotifyContacts.connectToDevice(this);
    }
}
