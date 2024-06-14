package com.project.quiz_app;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.project.quiz_app.quiz.DailyQuiz;
import com.project.quiz_app.quiz.QuizConfiguration;
import com.project.quiz_app.quiz.QuizMenu;

import java.util.concurrent.atomic.AtomicReference;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView displayName, title, mathScoreTextView, scienceScoreTextView, historyScoreTextView;
    Button practiceQuizButton, mathematicsButton, scienceButton, historyButton;;
    DialogObject dialogObject = new DialogObject(MainActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Retrieve the score and category from the intent
        String category = getIntent().getStringExtra("category");
        int score = getIntent().getIntExtra("score", 0);
        int totalQuestions = getIntent().getIntExtra("totalQuestions", 0);

        Log.d("TAG", "onCreate: "+category);
        // Save the score and correct rate in SharedPreferences
        if (category != null) {
            saveScore(category, score, totalQuestions);
        }
        // Update the UI with the saved scores
        updateScoreUI();

        mathematicsButton = findViewById(R.id.mathematics_button);
        scienceButton = findViewById(R.id.science_button);
        historyButton = findViewById(R.id.history_button);
        practiceQuizButton = findViewById(R.id.practice_quiz_generate_button);
        displayName = findViewById(R.id.display_name);
        title = findViewById(R.id.title);

        mathScoreTextView = findViewById(R.id.math_score_text);
        scienceScoreTextView = findViewById(R.id.science_score_text);
        historyScoreTextView = findViewById(R.id.history_score_text);

        if(!isInternetConnection()) {
            mathematicsButton.setVisibility(View.GONE);
            practiceQuizButton.setVisibility(View.GONE);
            displayName.setVisibility(View.GONE);
            title.setVisibility(View.GONE);
            mathScoreTextView.setVisibility(View.GONE);
            scienceScoreTextView.setVisibility(View.GONE);
            historyScoreTextView.setVisibility(View.GONE);

            dialogObject.noInternetConnectionDialog();
        }

        // Get the name from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("userName", Context.MODE_PRIVATE);
        String userName = sharedPreferences.getString("userName", "Guest");

        // Display the name
        displayName.setText("Hello, "+userName);

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
        quizConfiguration.setNumberOfQuestions("5");

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

    private void saveScore(String category, int score, int totalQuestions) {
        SharedPreferences sharedPreferences = getSharedPreferences("QuizScores", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        int correctRate = (score * 100) / totalQuestions;
        switch (category){
            case "19":
                editor.putInt("Mathematics_score", score);
                editor.putInt("Mathematics_correct_rate", correctRate);
                break;
            case "17":
                editor.putInt("Science_score", score);
                editor.putInt("Science_correct_rate", correctRate);
                break;
            case "23":
                editor.putInt("History_score", score);
                editor.putInt("History_correct_rate", correctRate);
                break;
        }
        editor.apply();
    }

    private void updateScoreUI() {
        SharedPreferences sharedPreferences = getSharedPreferences("QuizScores", MODE_PRIVATE);

        int mathScore = sharedPreferences.getInt("Mathematics_score", 0);
        int mathCorrectRate = sharedPreferences.getInt("Mathematics_correct_rate", 0);

        int scienceScore = sharedPreferences.getInt("Science_score", 0);
        int scienceCorrectRate = sharedPreferences.getInt("Science_correct_rate", 0);

        int historyScore = sharedPreferences.getInt("History_score", 0);
        int historyCorrectRate = sharedPreferences.getInt("History_correct_rate", 0);

        TextView mathScoreText = findViewById(R.id.math_score_text);
        mathScoreText.setText("Math Score: " + mathScore + ", Correct Rate: " + mathCorrectRate + "%");

        TextView scienceScoreText = findViewById(R.id.science_score_text);
        scienceScoreText.setText("Science Score: " + scienceScore + ", Correct Rate: " + scienceCorrectRate + "%");

        TextView historyScoreText = findViewById(R.id.history_score_text);
        historyScoreText.setText("History Score: " + historyScore + ", Correct Rate: " + historyCorrectRate + "%");
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