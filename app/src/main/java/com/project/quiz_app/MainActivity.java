package com.project.quiz_app;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.quiz_app.quiz.DailyQuizActivity;
import com.project.quiz_app.quiz.QuizConfiguration;
import com.project.quiz_app.quiz.QuizMenuActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView displayName, mathScoreTextView, scienceScoreTextView, historyScoreTextView, animalsScoreTextView, mathRateTextView, scienceRateTextView, historyRateTextView, animalsRateTextView;
    ImageView catTrivia, userAvatar;
    Button practiceQuizButton;

    CardView mathematicsButton, scienceButton, historyButton, animalsButton;
    DialogObject dialogObject = new DialogObject(MainActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Retrieve the score and category from the intent
        String category = getIntent().getStringExtra("category");
        int score = getIntent().getIntExtra("score", 0);
        int totalQuestions = getIntent().getIntExtra("totalQuestions", 0);

        // Save the score and correct rate in SharedPreferences
        if (category != null) {
            saveScore(category, score, totalQuestions);
        }
        // Update the UI with the saved scores
        updateScoreUI();

        mathematicsButton = findViewById(R.id.mathematics_card);
        scienceButton = findViewById(R.id.science_card);
        historyButton = findViewById(R.id.history_card);
        animalsButton = findViewById(R.id.animals_card);
        practiceQuizButton = findViewById(R.id.practice_quiz_generate_button);
        displayName = findViewById(R.id.display_name);
        catTrivia = findViewById(R.id.cat_trivia);
        userAvatar = findViewById(R.id.user_avatar);

        mathScoreTextView = findViewById(R.id.math_score_text);
        scienceScoreTextView = findViewById(R.id.science_score_text);
        historyScoreTextView = findViewById(R.id.history_score_text);
        animalsScoreTextView = findViewById(R.id.animals_score_text);
        mathRateTextView = findViewById(R.id.math_rate_text);
        scienceRateTextView = findViewById(R.id.science_rate_text);
        historyRateTextView = findViewById(R.id.history_rate_text);
        animalsRateTextView = findViewById(R.id.animals_rate_text);

        if(!isInternetConnection()) {
            mathematicsButton.setVisibility(View.GONE);
            scienceButton.setVisibility(View.GONE);
            historyButton.setVisibility(View.GONE);
            animalsButton.setVisibility(View.GONE);
            practiceQuizButton.setVisibility(View.GONE);
            displayName.setVisibility(View.GONE);
            catTrivia.setVisibility(View.GONE);
            userAvatar.setVisibility(View.GONE);
            mathScoreTextView.setVisibility(View.GONE);
            scienceScoreTextView.setVisibility(View.GONE);
            historyScoreTextView.setVisibility(View.GONE);
            animalsScoreTextView.setVisibility(View.GONE);
            mathRateTextView.setVisibility(View.GONE);
            scienceRateTextView.setVisibility(View.GONE);
            historyRateTextView.setVisibility(View.GONE);
            animalsRateTextView.setVisibility(View.GONE);

            dialogObject.noInternetConnectionDialog();
        }

        // Get the name from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("userPreferences", Context.MODE_PRIVATE);
        String userName = sharedPreferences.getString("userName", "Guest");
        int selectedAvatar = sharedPreferences.getInt("selectedAvatar", -1);

        if (selectedAvatar != -1) {
            userAvatar.setImageResource(selectedAvatar);
        }

        // Display the name
        displayName.setText("Hello, "+userName);

        mathematicsButton.setOnClickListener(this);
        scienceButton.setOnClickListener(this);
        historyButton.setOnClickListener(this);
        animalsButton.setOnClickListener(this);

        practiceQuizButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), QuizMenuActivity.class);
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
        Intent intent = new Intent(getApplicationContext(), DailyQuizActivity.class);
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
        SharedPreferences sharedPreferences = getSharedPreferences("userPreferences", MODE_PRIVATE);
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
            case "27":
                editor.putInt("Animals_score", score);
                editor.putInt("Animals_correct_rate", correctRate);
                break;
        }
        editor.apply();
    }

    private void updateScoreUI() {
        SharedPreferences sharedPreferences = getSharedPreferences("userPreferences", MODE_PRIVATE);

        int mathScore = sharedPreferences.getInt("Mathematics_score", 0);
        int mathCorrectRate = sharedPreferences.getInt("Mathematics_correct_rate", 0);

        int scienceScore = sharedPreferences.getInt("Science_score", 0);
        int scienceCorrectRate = sharedPreferences.getInt("Science_correct_rate", 0);

        int historyScore = sharedPreferences.getInt("History_score", 0);
        int historyCorrectRate = sharedPreferences.getInt("History_correct_rate", 0);

        int animalsScore = sharedPreferences.getInt("Animals_score", 0);
        int animalsCorrectRate = sharedPreferences.getInt("Animals_correct_rate", 0);

        mathScoreTextView = findViewById(R.id.math_score_text);
        scienceScoreTextView = findViewById(R.id.science_score_text);
        historyScoreTextView = findViewById(R.id.history_score_text);
        animalsScoreTextView = findViewById(R.id.animals_score_text);
        mathRateTextView = findViewById(R.id.math_rate_text);
        scienceRateTextView = findViewById(R.id.science_rate_text);
        historyRateTextView = findViewById(R.id.history_rate_text);
        animalsRateTextView = findViewById(R.id.animals_rate_text);

        mathScoreTextView.setText("Math Score: " + mathScore);
        scienceScoreTextView.setText("Science Score: " + scienceScore);
        historyScoreTextView.setText("History Score: " + historyScore);
        animalsScoreTextView.setText("Animals Score: " + animalsScore);
        mathRateTextView.setText("Correct Rate: " + mathCorrectRate + "%");
        scienceRateTextView.setText("Correct Rate: " + scienceCorrectRate + "%");
        historyRateTextView.setText("Correct Rate: " + historyCorrectRate + "%");
        animalsRateTextView.setText("Correct Rate: " + animalsCorrectRate + "%");
    }

    public enum Category {
        MATH(R.id.mathematics_card, "19"),
        SCIENCE(R.id.science_card, "17"),
        HISTORY(R.id.history_card, "23"),
        ANIMALS(R.id.animals_card, "27");

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