package com.diy.dj;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ChooseGenres extends AppCompatActivity {

    public static void start(Context context){
        Intent intent = new Intent(context, ChooseGenres.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_genres);
    }
}
