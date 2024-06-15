package com.project.quiz_app;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;

public class JoinJourneyActivity extends AppCompatActivity {

    Button loginButton;
    Button selectAvatarButton;
    TextInputEditText nameTextInputEditText;
    ImageView selectedAvatarImageView;
    String name = "";
    int selectedAvatar = -1;
    private static final int REQUEST_SELECT_AVATAR = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_journey);

        // Start the background music service
        Intent musicIntent = new Intent(this, BackgroundMusicService.class);
        startService(musicIntent);

        loginButton = findViewById(R.id.join_journey_button);
        selectAvatarButton = findViewById(R.id.select_avatar_button);
        nameTextInputEditText = findViewById(R.id.name_input);
        selectedAvatarImageView = findViewById(R.id.selected_avatar);

        // Save the name and avatar in SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("userPreferences", Context.MODE_PRIVATE);
        String userName = sharedPreferences.getString("userName", "");
        selectedAvatar = sharedPreferences.getInt("selectedAvatar", -1);

        nameTextInputEditText.setText(userName);
        if (selectedAvatar != -1) {
            selectedAvatarImageView.setImageResource(selectedAvatar);
        }

        selectAvatarButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), SelectAvatarActivity.class);
            startActivityForResult(intent, REQUEST_SELECT_AVATAR);
        });

        loginButton.setOnClickListener(v -> {
            this.name = nameTextInputEditText.getText().toString().trim();
            if (!name.isEmpty() && selectedAvatar != -1) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("userName", name);
                editor.putInt("selectedAvatar", selectedAvatar);
                editor.apply();

                // Pass the name to the next activity
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "You have to enter a name and select an avatar!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELECT_AVATAR && resultCode == RESULT_OK) {
            selectedAvatar = data.getIntExtra("selectedAvatar", -1);
            if (selectedAvatar != -1) {
                selectedAvatarImageView.setImageResource(selectedAvatar);
                // Save the selected avatar in SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("userPreferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("selectedAvatar", selectedAvatar);
                editor.apply();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Optionally, stop the service when the activity is destroyed
//        Intent musicIntent = new Intent(this, BackgroundMusicService.class);
//        stopService(musicIntent);
    }
}
