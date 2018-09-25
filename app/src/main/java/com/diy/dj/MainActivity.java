package com.diy.dj;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    public  void openJoin(View view){ JoinActivity.start(this);}

    public  void openLogin(View view){ PreLoginActivity.start(this);}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
