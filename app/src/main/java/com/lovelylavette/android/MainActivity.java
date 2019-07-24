package com.lovelylavette.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.google.android.libraries.places.api.Places;
import com.lovelylavette.android.fragment.DestinationFragment;
import com.lovelylavette.android.fragment.ProfileFragment;
import com.lovelylavette.android.fragment.SearchFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    FragmentManager fragmentManager;

    @BindView(R.id.nav_view)
    BottomNavigationView navView;

    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentTransaction ft = fragmentManager.beginTransaction();

            switch (item.getItemId()) {
                case R.id.menu_dashboard:
                    ft.replace(R.id.frag_container, new DestinationFragment())
                            .commit();
                    return true;
                case R.id.menu_search:
                    ft.replace(R.id.frag_container, new SearchFragment())
                            .commit();
                    return true;
                case R.id.menu_trips:
                    return true;
                case R.id.menu_profile:
                    ft.replace(R.id.frag_container, new ProfileFragment())
                            .commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        fragmentManager = getSupportFragmentManager();
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
        resetNav();
        Places.initialize(getApplicationContext(), BuildConfig.GoogleApiKey);
    }

    private void resetNav() {
        Fragment fragment = fragmentManager.findFragmentById(R.id.frag_container);
        if (fragment == null) {
            navView.setSelectedItemId(R.id.menu_search);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
