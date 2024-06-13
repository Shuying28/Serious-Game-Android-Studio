package com.project.quiz_app;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.project.quiz_app.quiz.DailyQuiz;
import com.project.quiz_app.quiz.QuizConfiguration;
import com.project.quiz_app.quiz.QuizMenu;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView displayName, title;
    Button practiceQuizButton, mathematicsButton, scienceButton, historyButton;;
    DialogObject dialogObject = new DialogObject(MainActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mathematicsButton = findViewById(R.id.mathematics_button);
        scienceButton = findViewById(R.id.science_button);
        historyButton = findViewById(R.id.history_button);
        practiceQuizButton = findViewById(R.id.practice_quiz_generate_button);
        displayName = findViewById(R.id.display_name);
        title = findViewById(R.id.title);

        if(!isInternetConnection()) {
            mathematicsButton.setVisibility(View.GONE);
            practiceQuizButton.setVisibility(View.GONE);
            displayName.setVisibility(View.GONE);
            title.setVisibility(View.GONE);

            dialogObject.noInternetConnectionDialog();
        }

        displayName.setText("Hello, Guest");

        mathematicsButton.setOnClickListener(this);
        scienceButton.setOnClickListener(this);
        historyButton.setOnClickListener(this);


        practiceQuizButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), QuizMenu.class);
            startActivity(intent);
            finish();
        });

        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                dialogObject.exitAppDialog();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getApplicationContext(), DailyQuiz.class);
        QuizConfiguration quizConfiguration = new QuizConfiguration();
        quizConfiguration.setNumberOfQuestions("10");

        Category selectedCategory = Category.fromId(v.getId());
        if (selectedCategory != null) {
            quizConfiguration.setCategory(selectedCategory.getCategoryId());
        }

        quizConfiguration.setDifficulty("easy"); // Start with easy level
        intent.putExtra("config", quizConfiguration);
        startActivity(intent);
        finish();
    }

    public boolean isInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    public enum Category {
        MATH(R.id.mathematics_button, "19"),
        SCIENCE(R.id.science_button, "17"),
        HISTORY(R.id.history_button, "23");

        private final int id;
        private final String categoryId;

        Category(int id, String categoryId) {
            this.id = id;
            this.categoryId = categoryId;
        }

        public int getId() {
            return id;
        }

        public String getCategoryId() {
            return categoryId;
        }

        public static Category fromId(int id) {
            for (Category category : values()) {
                if (category.id == id) {
                    return category;
                }
            }
            return null;
        }
    }
}