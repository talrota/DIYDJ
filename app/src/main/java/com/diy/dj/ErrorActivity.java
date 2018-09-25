package com.diy.dj;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class ErrorActivity extends AppCompatActivity {

    public String getSerror() {
        return Serror;
    }

    public static void setSerror(String serror) {
        Serror = serror;
    }

    static String Serror;

    public static void  start(Context context, String error) {
        setSerror(error);
        Intent intent = new Intent(context, ErrorActivity.class);
        intent.putExtra("error", error);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);

        TextView s = (TextView) findViewById(R.id.error);
        Intent i = getIntent();
        final String val = i.getStringExtra("error");
        s.setText("Error: " + val);
    }
}