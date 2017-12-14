package com.example.harsh.dynamicmenu;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Map<String, String> optionsMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        setNavigationOptions(navigationView);
    }

    private void setNavigationOptions(NavigationView navigationView) {
        try {
            optionsMap = parseResponse();
            Menu menu = navigationView.getMenu();
            for (Iterator<String> it = optionsMap.keySet().iterator(); it.hasNext(); ) {
                String option = it.next();
                menu.add(R.id.group_main, option.hashCode(), Menu.NONE, option);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private Map<String, String> parseResponse() throws IOException {
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        Map<String, String> map = new HashMap<>();
        try {
            reader = new BufferedReader(
                    new InputStreamReader(getAssets().open("response.json"), "UTF-8"));
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                builder.append(mLine);
            }
            if (!TextUtils.isEmpty(builder.toString())) {
                JSONObject object = new JSONObject(builder.toString());
                for (Iterator<String> iterator = object.keys(); iterator.hasNext(); ) {
                    String key = iterator.next();
                    String value = object.getString(key);
                    map.put(key, value);
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return map;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        String url = optionsMap.get(item.getTitle());
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        if (getPackageManager().resolveActivity(intent, PackageManager.GET_RESOLVED_FILTER) != null) {
            startActivity(intent);
        } else {
            Snackbar.make(findViewById(R.id.fab), "No browser installed.", Snackbar.LENGTH_SHORT).show();
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
