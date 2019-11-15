package com.depa.flagmaker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class What extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_what);
    }

    public void finish(View view) {
        finish();
    }
}
