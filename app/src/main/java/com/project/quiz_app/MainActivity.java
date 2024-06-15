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

    TextView displayName, mathScoreTextView, scienceScoreTextView, videoScoreTextView, animalsScoreTextView, mathRateTextView, scienceRateTextView, videoRateTextView, animalsRateTextView;
    TextView videoDifficultyTextView, scienceDifficultyTextView, mathDifficultyTextView, animalsDifficultyTextView;
    TextView musicScoreTextView, computersScoreTextView, musicRateTextView, computersRateTextView, musicDifficultyTextView, computersDifficultyTextView;
    ImageView catTrivia, userAvatar;
    Button practiceQuizButton;

    CardView mathematicsButton, scienceButton, videoButton, animalsButton, musicButton, computersButton;
    DialogObject dialogObject = new DialogObject(MainActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Start the background music service
        Intent musicIntent = new Intent(this, BackgroundMusicService.class);
        startService(musicIntent);

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
        videoButton = findViewById(R.id.video_card);
        animalsButton = findViewById(R.id.animals_card);
        musicButton = findViewById(R.id.music_card);
        computersButton = findViewById(R.id.computers_card);
        practiceQuizButton = findViewById(R.id.practice_quiz_generate_button);
        displayName = findViewById(R.id.display_name);
        catTrivia = findViewById(R.id.cat_trivia);
        userAvatar = findViewById(R.id.user_avatar);

        mathDifficultyTextView = findViewById(R.id.math_difficulty_text);
        scienceDifficultyTextView = findViewById(R.id.science_difficulty_text);
        videoDifficultyTextView = findViewById(R.id.video_difficulty_text);
        animalsDifficultyTextView = findViewById(R.id.animals_difficulty_text);
        musicDifficultyTextView = findViewById(R.id.music_difficulty_text);
        computersDifficultyTextView = findViewById(R.id.computers_difficulty_text);

        mathScoreTextView = findViewById(R.id.math_score_text);
        scienceScoreTextView = findViewById(R.id.science_score_text);
        videoScoreTextView = findViewById(R.id.video_score_text);
        animalsScoreTextView = findViewById(R.id.animals_score_text);
        musicScoreTextView = findViewById(R.id.music_score_text);
        computersScoreTextView = findViewById(R.id.computers_score_text);

        mathRateTextView = findViewById(R.id.math_rate_text);
        scienceRateTextView = findViewById(R.id.science_rate_text);
        videoRateTextView = findViewById(R.id.video_rate_text);
        animalsRateTextView = findViewById(R.id.animals_rate_text);
        musicRateTextView = findViewById(R.id.music_rate_text);
        computersRateTextView = findViewById(R.id.computers_rate_text);

        if(!isInternetConnection()) {
            mathematicsButton.setVisibility(View.GONE);
            scienceButton.setVisibility(View.GONE);
            videoButton.setVisibility(View.GONE);
            animalsButton.setVisibility(View.GONE);
            musicButton.setVisibility(View.GONE);
            computersButton.setVisibility(View.GONE);
            practiceQuizButton.setVisibility(View.GONE);
            displayName.setVisibility(View.GONE);
            catTrivia.setVisibility(View.GONE);
            userAvatar.setVisibility(View.GONE);
            mathScoreTextView.setVisibility(View.GONE);
            scienceScoreTextView.setVisibility(View.GONE);
            videoScoreTextView.setVisibility(View.GONE);
            animalsScoreTextView.setVisibility(View.GONE);
            musicScoreTextView.setVisibility(View.GONE);
            computersScoreTextView.setVisibility(View.GONE);
            animalsDifficultyTextView.setVisibility(View.GONE);
            scienceDifficultyTextView.setVisibility(View.GONE);
            videoDifficultyTextView.setVisibility(View.GONE);
            animalsDifficultyTextView.setVisibility(View.GONE);
            musicDifficultyTextView.setVisibility(View.GONE);
            computersDifficultyTextView.setVisibility(View.GONE);
            mathRateTextView.setVisibility(View.GONE);
            scienceRateTextView.setVisibility(View.GONE);
            videoRateTextView.setVisibility(View.GONE);
            animalsRateTextView.setVisibility(View.GONE);
            musicRateTextView.setVisibility(View.GONE);
            computersRateTextView.setVisibility(View.GONE);

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
        videoButton.setOnClickListener(this);
        animalsButton.setOnClickListener(this);
        musicButton .setOnClickListener(this);
        computersButton .setOnClickListener(this);

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

        SharedPreferences sharedPreferences = getSharedPreferences("userPreferences", Context.MODE_PRIVATE);
        String difficulty_level = sharedPreferences.getString(quizConfiguration.getCategory() + "_difficulty", "");
        if (difficulty_level.equals("")){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(quizConfiguration.getCategory() + "_difficulty", "easy");
            quizConfiguration.setDifficulty("easy");
            editor.apply();
        } else {
            quizConfiguration.setDifficulty(difficulty_level);
        }
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
            case "15":
                editor.putInt("Video_score", score);
                editor.putInt("Video_correct_rate", correctRate);
                break;
            case "27":
                editor.putInt("Animals_score", score);
                editor.putInt("Animals_correct_rate", correctRate);
                break;
            case "12":
                editor.putInt("Music_score", score);
                editor.putInt("Music_correct_rate", correctRate);
                break;
            case "18":
                editor.putInt("Computers_score", score);
                editor.putInt("Computers_correct_rate", correctRate);
                break;
        }
        editor.apply();
    }

    private void updateScoreUI() {
        SharedPreferences sharedPreferences = getSharedPreferences("userPreferences", MODE_PRIVATE);

        String mathDifficulty = sharedPreferences.getString( "19_difficulty", "easy");
        int mathScore = sharedPreferences.getInt("Mathematics_score", 0);
        int mathCorrectRate = sharedPreferences.getInt("Mathematics_correct_rate", 0);

        String scienceDifficulty = sharedPreferences.getString("17_difficulty", "easy");
        int scienceScore = sharedPreferences.getInt("Science_score", 0);
        int scienceCorrectRate = sharedPreferences.getInt("Science_correct_rate", 0);

        String videoDifficulty = sharedPreferences.getString("15_difficulty", "easy");
        int videoScore = sharedPreferences.getInt("Video_score", 0);
        int videoCorrectRate = sharedPreferences.getInt("Video_correct_rate", 0);

        String animalsDifficulty = sharedPreferences.getString("27_difficulty", "easy");
        int animalsScore = sharedPreferences.getInt("Animals_score", 0);
        int animalsCorrectRate = sharedPreferences.getInt("Animals_correct_rate", 0);

        String musicDifficulty = sharedPreferences.getString("12_difficulty", "easy");
        int musicScore = sharedPreferences.getInt("Music_score", 0);
        int musicCorrectRate = sharedPreferences.getInt("Music_correct_rate", 0);

        String computersDifficulty = sharedPreferences.getString("18_difficulty", "easy");
        int computersScore = sharedPreferences.getInt("Computers_score", 0);
        int computersCorrectRate = sharedPreferences.getInt("Computers_correct_rate", 0);

        mathDifficultyTextView = findViewById(R.id.math_difficulty_text);
        scienceDifficultyTextView = findViewById(R.id.science_difficulty_text);
        videoDifficultyTextView = findViewById(R.id.video_difficulty_text);
        animalsDifficultyTextView = findViewById(R.id.animals_difficulty_text);
        musicDifficultyTextView = findViewById(R.id.music_difficulty_text);
        computersDifficultyTextView = findViewById(R.id.computers_difficulty_text);

        mathScoreTextView = findViewById(R.id.math_score_text);
        scienceScoreTextView = findViewById(R.id.science_score_text);
        videoScoreTextView = findViewById(R.id.video_score_text);
        animalsScoreTextView = findViewById(R.id.animals_score_text);
        musicScoreTextView = findViewById(R.id.music_score_text);
        computersScoreTextView = findViewById(R.id.computers_score_text);

        mathRateTextView = findViewById(R.id.math_rate_text);
        scienceRateTextView = findViewById(R.id.science_rate_text);
        videoRateTextView = findViewById(R.id.video_rate_text);
        animalsRateTextView = findViewById(R.id.animals_rate_text);
        musicRateTextView = findViewById(R.id.music_rate_text);
        computersRateTextView = findViewById(R.id.computers_rate_text);

        mathDifficultyTextView.setText("Difficulty: " + mathDifficulty);
        scienceDifficultyTextView.setText("Difficulty: " + scienceDifficulty);
        videoDifficultyTextView.setText("Difficulty: " + videoDifficulty);
        animalsDifficultyTextView.setText("Difficulty: " + animalsDifficulty);
        musicDifficultyTextView.setText("Difficulty: " + musicDifficulty);
        computersDifficultyTextView.setText("Difficulty: " + computersDifficulty);

        mathScoreTextView.setText("Math Score: " + mathScore);
        scienceScoreTextView.setText("Science Score: " + scienceScore);
        videoScoreTextView.setText("Video Score: " + videoScore);
        animalsScoreTextView.setText("Animals Score: " + animalsScore);
        musicScoreTextView.setText("Music Score: " + musicScore);
        computersScoreTextView.setText("Computers Score: " + computersScore);

        mathRateTextView.setText("Correct Rate: " + mathCorrectRate + "%");
        scienceRateTextView.setText("Correct Rate: " + scienceCorrectRate + "%");
        videoRateTextView.setText("Correct Rate: " + videoCorrectRate + "%");
        animalsRateTextView.setText("Correct Rate: " + animalsCorrectRate + "%");
        musicRateTextView.setText("Correct Rate: " + musicCorrectRate + "%");
        computersRateTextView.setText("Correct Rate: " + computersCorrectRate + "%");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Optionally, stop the service when the activity is destroyed
        Intent musicIntent = new Intent(this, BackgroundMusicService.class);
        stopService(musicIntent);
    }

    public enum Category {
        MATH(R.id.mathematics_card, "19"),
        SCIENCE(R.id.science_card, "17"),
        VIDEO(R.id.video_card, "15"),
        ANIMALS(R.id.animals_card, "27"),
        MUSIC(R.id.music_card, "12"),
        COMPUTERS(R.id.computers_card, "18");

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