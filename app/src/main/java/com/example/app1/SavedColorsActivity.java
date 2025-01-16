package com.example.app1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

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
        listView = findViewById(R.id.activity_saved_colors);
        getSupportActionBar().setTitle("Saved Colors");

        loadSavedColors();

        // Set up Bottom Navigation Bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_delete_color) {
                deleteColors();
                return true;
            } else if (id == R.id.nav_back_to_main) {
                Intent intent = new Intent(SavedColorsActivity.this, MainActivity.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_add_color) {
                Toast.makeText(this, "Denne funktion er endnu ikke implementeret", Toast.LENGTH_SHORT).show();
                return true;
            }

            return false;
        });
    }

    private void loadSavedColors() {
        // Hent gemte farver
        sharedPreferences = getSharedPreferences("ColorPickerApp", MODE_PRIVATE);
        gson = new Gson();

        String json = sharedPreferences.getString("savedColors", null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        List<String> savedColors = gson.fromJson(json, type);

        if (savedColors == null || savedColors.isEmpty()) {
            savedColors = new ArrayList<>();
            Toast.makeText(this, "Ingen gemte farver fundet!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Farvekoder hentet", Toast.LENGTH_SHORT).show();
        }

        // Vis de gemte farver i en liste
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, savedColors);
        listView.setAdapter(adapter);
    }

    private void deleteColors() {
        sharedPreferences.edit().remove("savedColors").apply();
        Toast.makeText(this, "Alle gemte farver er slettet", Toast.LENGTH_SHORT).show();
        loadSavedColors();
    }
}
