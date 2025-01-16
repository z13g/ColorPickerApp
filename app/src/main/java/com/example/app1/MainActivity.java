package com.example.app1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    private static final int TAKE_PHOTO = 2;

    private ImageView imageView;
    private View colorPreview;
    private TextView colorValue;
    private Button saveColorCodeButton;

    private SharedPreferences sharedPreferences;
    private Gson gson;
    private List<String> savedColors;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        getSupportActionBar().setTitle("Color Picker App");

        // Initialize UI components
        imageView = findViewById(R.id.image_view);
        colorPreview = findViewById(R.id.color_preview);
        colorValue = findViewById(R.id.color_value);
        saveColorCodeButton = findViewById(R.id.save_colorcode_button);

        // Bottom Navigation
        // Initialize Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.inflateMenu(R.menu.bottom_nav_main);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_gallery) {
                openGallery();
                return true;
            } else if (id == R.id.nav_saved_colors) {
                Intent intent = new Intent(MainActivity.this, SavedColorsActivity.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_camera) {
                openCamera();
                return true;
            }

            return false;
        });



        // Set touch listener on the image
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
                    Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                    int x = (int) event.getX();
                    int y = (int) event.getY();

                    int scaledX = (int) (x * ((float) bitmap.getWidth() / imageView.getWidth()));
                    int scaledY = (int) (y * ((float) bitmap.getHeight() / imageView.getHeight()));

                    if (scaledX >= 0 && scaledY >= 0 && scaledX < bitmap.getWidth() && scaledY < bitmap.getHeight()) {
                        int pixel = bitmap.getPixel(scaledX, scaledY);
                        colorPreview.setBackgroundColor(pixel);
                        String hexColor = String.format("#%06X", (0xFFFFFF & pixel));
                        colorValue.setText(hexColor);
                    }
                }
                return true;
            }
        });

        // Set click listeners for buttons
        saveColorCodeButton.setOnClickListener(v -> {
            String hexColor = colorValue.getText().toString();
            if (!hexColor.isEmpty()) {
                saveColor(hexColor);
            } else {
                Toast.makeText(MainActivity.this, "Vælg en farve først!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, TAKE_PHOTO);
    }

    private void saveColor(String hexColor) {
        sharedPreferences = getSharedPreferences("ColorPickerApp", MODE_PRIVATE);
        Gson gson = new Gson();

        // Hent eksisterende farvekoder
        String json = sharedPreferences.getString("savedColors", null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        List<String> savedColors = gson.fromJson(json, type);

        // Hvis listen er tom, opret en ny
        if (savedColors == null) {
            savedColors = new ArrayList<>();
        }

        // Tilføj farvekoden, hvis den ikke allerede findes
        if (!savedColors.contains(hexColor)) {
            savedColors.add(hexColor);
        } else {
            Toast.makeText(this, "Farvekoden er allerede gemt: " + hexColor, Toast.LENGTH_SHORT).show();
            return;
        }

        // Gem den opdaterede liste i SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("savedColors", gson.toJson(savedColors));
        editor.apply();

        Toast.makeText(this, "Farvekode gemt: " + hexColor, Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE && data != null) {
                Uri imageUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    imageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == TAKE_PHOTO && data != null) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                imageView.setImageBitmap(photo);
            }
        }
    }
}
