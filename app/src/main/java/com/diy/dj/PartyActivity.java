package com.diy.dj;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

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
        Toolbar toolbar = findViewById(R.id.menuToolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logoutViaMenu:
                FirebaseAuth.getInstance().signOut();
                finish();
                PreLoginActivity.start(this);
                break;
        }
        return true;
    }
}
