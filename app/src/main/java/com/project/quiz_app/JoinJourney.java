package com.project.quiz_app;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.project.quiz_app.MainActivity;
import com.project.quiz_app.R;

public class JoinJourney extends AppCompatActivity {

    Button loginButton;
    TextInputEditText nameTextInputEditText;
    String name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_journey);

        loginButton = findViewById(R.id.join_journey_button);
        nameTextInputEditText = findViewById(R.id.name_input);

        // Save the name in SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("userName", Context.MODE_PRIVATE);
        String userName = sharedPreferences.getString("userName", "");
        nameTextInputEditText.setText(userName);

        loginButton.setOnClickListener(v -> {
            this.name = nameTextInputEditText.getText().toString().trim();
            if (!name.isEmpty()) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("userName", name);
                editor.apply();
                nameTextInputEditText.setText(userName);

                // Pass the name to the next activity
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "You have to enter a name!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}