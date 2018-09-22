package com.diy.dj;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class PartyActivity extends AppCompatActivity {

    private static int hour;
    private static int minute;

    public static void start(Context context, int hour, int minute){
        Intent intent = new Intent(context, PartyActivity.class);
        PartyActivity.hour = hour;
        PartyActivity.minute = minute;
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party);
    }
}
