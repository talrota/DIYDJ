package com.diy.dj;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TimePicker;

public class Settings extends AppCompatActivity {

    public static void start(Context context){
        Intent intent = new Intent(context, Settings.class);
        context.startActivity(intent);
    }

    public void pushChilluntil(View view){
        TimePicker timePicker = findViewById(R.id.picktime);
        timePicker.setVisibility(View.VISIBLE);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener(){

            @Override
            public void onTimeChanged (TimePicker v, int hour, int minute){
                PartyActivity.start(Settings.this, hour, minute);
                //todo
            }

        });
    }

    public void pushChill(View view){
        PartyActivity.start(this, 1 , 1);
    }

    public void pushParty(View view){
        PartyActivity.start(this, -1 , -1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }
}
