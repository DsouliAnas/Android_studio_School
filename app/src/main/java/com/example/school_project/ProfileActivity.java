package com.example.school_project;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import java.io.ByteArrayOutputStream;

public class ProfileActivity extends AppCompatActivity {

    private static final int IMAGE_PICKER_REQUEST = 1;

    private EditText usernameText, addressText;
    private TextView emailText;
    private ImageView profileImage;
    private Button editProfileButton, saveProfileButton;
    private String email; // User's email to identify the record

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Enable the back button in the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Handle the back button click event
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Initialize views
        usernameText = findViewById(R.id.usernameText);
        emailText = findViewById(R.id.emailText);
        addressText = findViewById(R.id.addressText);
        profileImage = findViewById(R.id.profileImage);
        editProfileButton = findViewById(R.id.editProfileButton);
        saveProfileButton = findViewById(R.id.saveProfileButton);

        // Initialize DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Get the user's email from intent and check if it is valid
        Intent intent = getIntent();
        email = intent.getStringExtra("email");

        if (email != null) {
            // Load the profile using the email
            loadUserProfile();
        } else {
            Toast.makeText(this, "Email is missing", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Edit Profile Button Clicked
        editProfileButton.setOnClickListener(v -> enableProfileEditing());

        // Save Profile Button Clicked
        saveProfileButton.setOnClickListener(v -> saveUserProfile());
    }

    // Show dialog to request permission and open gallery if allowed
    public void chooseProfileImage(View view) {
        new AlertDialog.Builder(this)
                .setMessage("This app needs access to your gallery to change the profile image. Do you allow this?")
                .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // If the user clicks Allow, open the gallery
                        openGallery();
                    }
                })
                .setNegativeButton("Don't Allow", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // If the user clicks Don't Allow, show a message
                        Toast.makeText(ProfileActivity.this, "Permission denied, unable to change image", Toast.LENGTH_SHORT).show();
                    }
                })
                .create()
                .show();
    }

    // Open the gallery to pick an image
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICKER_REQUEST);
    }

    private void loadUserProfile() {
        // Fetch user profile data from the database
        Cursor cursor = dbHelper.getUserProfile(email);
        if (cursor != null && cursor.moveToFirst()) {
            String username = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_USERNAME));
            String address = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ADDRESS));
            String imageString = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PROFILE_IMAGE));

            // Set the UI elements with the fetched data
            usernameText.setText(username);
            emailText.setText(email);
            addressText.setText(address);

            // Set Profile Image if it exists
            if (imageString != null) {
                byte[] decodedString = Base64.decode(imageString, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                profileImage.setImageBitmap(decodedByte);
            }

            cursor.close();
        } else {
            Toast.makeText(this, "Failed to load profile", Toast.LENGTH_SHORT).show();
        }
    }

    private void enableProfileEditing() {
        // Enable the editing of the profile fields
        usernameText.setEnabled(true);
        addressText.setEnabled(true);

        // Show the save button and hide the edit button
        saveProfileButton.setVisibility(View.VISIBLE);
        editProfileButton.setVisibility(View.GONE);
    }

    private void saveUserProfile() {
        String username = usernameText.getText().toString();
        String address = addressText.getText().toString();

        if (username.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the bitmap from the ImageView
        Bitmap bitmap = ((BitmapDrawable) profileImage.getDrawable()).getBitmap();

        // Convert Bitmap to Base64 String for storage
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        // Update user profile in the database
        boolean updated = dbHelper.updateUserProfile(email, username, address, encodedImage);

        // Display success or failure message
        if (updated) {
            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
        }
    }

    // Handle the result of image picking
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICKER_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                    profileImage.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
