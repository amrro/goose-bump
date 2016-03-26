package com.amr.android.goosebumps;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;

public class LaunchActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        if (savedInstanceState == null)
            getSupportFragmentManager().beginTransaction()
            .replace(R.id.container, new LaunchFragment())
            .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        return super.onCreateOptionsMenu(menu);
    }


}
