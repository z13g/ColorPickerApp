package com.example.app1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SavedColorsActivity extends AppCompatActivity {

    private ListView listView;
    private SharedPreferences sharedPreferences;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_colors);

        // Initialize UI components
        listView = findViewById(R.id.list_view);
        getSupportActionBar().setTitle("Saved Colors");

        loadSavedColors();

        // Set up Bottom Navigation Bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.inflateMenu(R.menu.bottom_nav_saved_colors);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_delete_color) {
                //deleteColor();
                return true;
            } else if (id == R.id.nav_back_to_main) {
                Intent intent = new Intent(SavedColorsActivity.this, MainActivity.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_add_color) {
                //addColorManually();
                return true;
            }

            return false;
        });

        // Mark the current tab as selected
        //bottomNavigationView.setSelectedItemId(R.id.nav_back_to_main);
    }

    private void loadSavedColors() {
        sharedPreferences = getSharedPreferences("ColorPickerApp", MODE_PRIVATE);
        gson = new Gson();

        // Hent gemte farvekoder fra SharedPreferences
        String json = sharedPreferences.getString("savedColors", null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        List<String> savedColors = gson.fromJson(json, type);

        // Hvis listen er tom, opret en ny
        if (savedColors == null) {
            savedColors = new ArrayList<>();
        }

        // Vis farverne i en ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, savedColors);
        listView.setAdapter(adapter);
    }


}
