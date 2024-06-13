package com.project.quiz_app;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.project.quiz_app.quiz.DailyQuiz;
import com.project.quiz_app.quiz.QuizMenu;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    TextView displayName, title;
    Button logoutButton, practiceQuizButton, dailyQuizButton, leaderboardButton, changeNameButton;
    FirebaseUser user;
    DialogObject dialogObject = new DialogObject(MainActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dailyQuizButton = findViewById(R.id.daily_quiz_button);
        practiceQuizButton = findViewById(R.id.practice_quiz_generate_button);
//        leaderboardButton = findViewById(R.id.leaderboard_button);
//        changeNameButton = findViewById(R.id.change_name_button);
//        logoutButton = findViewById(R.id.logout_button);
        displayName = findViewById(R.id.display_name);
        title = findViewById(R.id.title);

        if(!isInternetConnection()) {
            dailyQuizButton.setVisibility(View.GONE);
            practiceQuizButton.setVisibility(View.GONE);
//            leaderboardButton.setVisibility(View.GONE);
//            changeNameButton.setVisibility(View.GONE);
//            logoutButton.setVisibility(View.GONE);
            displayName.setVisibility(View.GONE);
            title.setVisibility(View.GONE);

            dialogObject.noInternetConnectionDialog();
        }

        displayName.setText("Hello, Guest");

        dailyQuizButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), DailyQuiz.class);
            startActivity(intent);
            finish();
        });

        practiceQuizButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), QuizMenu.class);
            startActivity(intent);
            finish();
        });

//        leaderboardButton.setOnClickListener(v -> {
//            Intent intent = new Intent(getApplicationContext(), Leaderboard.class);
//            startActivity(intent);
//            finish();
//        });
//
//        changeNameButton.setOnClickListener(v -> {
//            Intent intent = new Intent(getApplicationContext(), SetOrChangeName.class);
//            startActivity(intent);
//            finish();
//        });
//
//        logoutButton.setOnClickListener(v -> {
//            FirebaseAuth.getInstance().signOut();
//            Intent intent = new Intent(getApplicationContext(), Login.class);
//            startActivity(intent);
//            finish();
//        });

        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                dialogObject.exitAppDialog();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
    }

    public boolean isInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
    }
}