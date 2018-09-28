package com.diy.dj;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TimePicker;

public class Settings extends AppCompatActivity {
    private static SpotifyContacts spotifyContacts;

    public static void start(Context context, SpotifyContacts contcats){
        Intent intent = new Intent(context, Settings.class);
        context.startActivity(intent);
        spotifyContacts = contcats;
    }

    public void pushChilluntil(View view){
        TimePicker timePicker = findViewById(R.id.picktime);
        timePicker.setVisibility(View.VISIBLE);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener(){

            @Override
            public void onTimeChanged (TimePicker v, int hour, int minute){
                PartyActivity.start(Settings.this, hour, minute, spotifyContacts);
                //todo
            }

        });
    }

    public void pushChill(View view){
        PartyActivity.start(this, 1 , 1, spotifyContacts);
    }

    public void pushParty(View view){
        PartyActivity.start(this, -1 , -1, spotifyContacts);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }
}
