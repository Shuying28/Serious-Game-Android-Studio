package com.project.quiz_app.quiz;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.project.quiz_app.DialogObject;
import com.project.quiz_app.R;
import java.util.Calendar;
import java.util.Objects;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;


public class DailyQuizActivity extends AppCompatActivity implements View.OnClickListener {

    // Constants for level keys
    private static final String PREFS_NAME = "userPreferences";

    private SharedPreferences sharedPreferences;

    @Override
    protected void onPause() {
        super.onPause();
        countDownTimer.cancel();
        this.millisUntilFinished = Long.parseLong(countdownNumberTextView.getText().toString()) * 1000;
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    // onResume is called when the activity is created,
    // to prevent unexpected issues, I created a variable
    // to check if the activity was created
    boolean dailyQuizActivityCreated = false;

    @Override
    protected void onResume() {
        super.onResume();
        if (dailyQuizActivityCreated) {
            countdownNumberTextView.setText(String.valueOf((int) this.millisUntilFinished / 1000));
            createCountDownTimer(this.millisUntilFinished);
            countDownTimer.start();
            if (mediaPlayer != null) {
                mediaPlayer.start();
            }
        } else {
            createCountDownTimer(16000);
            dailyQuizActivityCreated = true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    interface Request {
        @GET("https://opentdb.com/api.php?type=multiple")
        Call<QuizObject> get(@Query("amount") String amount,
                             @Query("difficulty") String difficulty,
                             @Query("category") String category);
    }

    // Quiz variables
    QuizConfiguration quizConfiguration;

    // Quiz variable
    QuizObject quiz;

    // Visual variables
    TextView questionsTextView, questionsLeftTextView, correctAnswersTextView, userNameTextView;
    Button respA, respB, respC, respD;
    Button nextButton;
    LinearLayout resultCard;

    // Quiz running variables
    int questionIndex = 0;
    String selectedAnswer = "null";
    int score = 0;
    int totalQuestions = 5;
    int correctAnswer = 0;
    private MediaPlayer mediaPlayer;

    // Loading screen
    DialogObject dialogObject = new DialogObject(DailyQuizActivity.this);

    // Daily quiz compare variables
    Calendar dateAndTimeNow;
    Calendar dateAndTimeAfter24h;

    // CountDown variables
    TextView countdownTextTextView;
    TextView countdownNumberTextView, difficultyTextView, difficultyLevelTextView;
    CountDownTimer countDownTimer;
    long millisUntilFinished = 16000;
    ImageView userAvatar;
    // Flag to check if the quiz has ended
    boolean quizEnded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_quiz);

        mediaPlayer = MediaPlayer.create(this, R.raw.trivia_quiz_bg);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        questionsLeftTextView = findViewById(R.id.questions_left);
        correctAnswersTextView = findViewById(R.id.correct_answers);
        userNameTextView = findViewById(R.id.user_name);
        questionsLeftTextView.append(" " + totalQuestions);
        correctAnswersTextView.setText(correctAnswer + " / 5");

        countdownTextTextView = findViewById(R.id.countdown_text);
        countdownNumberTextView = findViewById(R.id.countdown_number);
        difficultyLevelTextView = findViewById(R.id.difficulty_level);
        difficultyTextView = findViewById(R.id.difficulty_text);
        questionsTextView = findViewById(R.id.question);
        respA = findViewById(R.id.A_response);
        respB = findViewById(R.id.B_response);
        respC = findViewById(R.id.C_response);
        respD = findViewById(R.id.D_response);
        nextButton = findViewById(R.id.next_button);
        resultCard = findViewById(R.id.result_card);
        userAvatar = findViewById(R.id.user_avatar);

        questionsLeftTextView.setVisibility(View.GONE);
        countdownTextTextView.setVisibility(View.GONE);
        countdownNumberTextView.setVisibility(View.GONE);
        difficultyLevelTextView.setVisibility(View.GONE);
        difficultyTextView.setVisibility(View.GONE);
        questionsTextView.setVisibility(View.GONE);
        respA.setVisibility(View.GONE);
        respB.setVisibility(View.GONE);
        respC.setVisibility(View.GONE);
        respD.setVisibility(View.GONE);
        nextButton.setVisibility(View.GONE);
        resultCard.setVisibility(View.GONE);
        userAvatar.setVisibility(View.GONE);

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        userNameTextView.setText(sharedPreferences.getString("userName", ""));
        int selectedAvatar = sharedPreferences.getInt("selectedAvatar", -1);

        if (selectedAvatar != -1) {
            userAvatar.setImageResource(selectedAvatar);
        }

        dialogObject.dailyQuizInfoDialog().thenAccept(okPressed -> {
            if (okPressed) {
                startDailyQuiz();
            }
        });

        respA.setOnClickListener(this);
        respB.setOnClickListener(this);
        respC.setOnClickListener(this);
        respD.setOnClickListener(this);
        nextButton.setOnClickListener(this);

        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                dialogObject.closeDailyQuizDialog();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
    }


    // CountDownTimer functions:
    private void createCountDownTimer(long millisUntilFinished) {
        this.countDownTimer = new CountDownTimer(millisUntilFinished, 1000) {
            String timeLeft;

            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = String.valueOf(millisUntilFinished / 1000);
                if (millisUntilFinished / 1000 == 10) {
                    countdownNumberTextView.setTextColor(Color.YELLOW);
                }

                if (millisUntilFinished / 1000 == 5) {
                    countdownNumberTextView.setTextColor(Color.RED);
                }
                countdownNumberTextView.setText(timeLeft);
            }

            @Override
            public void onFinish() {
                timeUp();
            }
        };
    }

    private void timeUp() {
        // if 15 second pass, the user will be
        // redirected to the next question with no score modifications
        if (questionIndex < 10) {
            respA.setBackgroundColor(Color.parseColor("#262956"));
            respB.setBackgroundColor(Color.parseColor("#262956"));
            respC.setBackgroundColor(Color.parseColor("#262956"));
            respD.setBackgroundColor(Color.parseColor("#262956"));
            countdownNumberTextView.setTextColor(Color.WHITE);

            questionIndex++;
            setQuestionsLeftTextView();
            Log.d("TAG", "timeUp: "+questionIndex);
            setValuesToQuiz(quiz, questionIndex);
            selectedAnswer = "null";

            countDownTimer.cancel();
            createCountDownTimer(16000);
            countDownTimer.start();
        }
    }


    private void startDailyQuiz() {
        dialogObject.startLoadingDialog();

        questionsLeftTextView.setVisibility(View.VISIBLE);
        countdownTextTextView.setVisibility(View.VISIBLE);
        countdownNumberTextView.setVisibility(View.VISIBLE);
        difficultyLevelTextView.setVisibility(View.VISIBLE);
        difficultyTextView.setVisibility(View.VISIBLE);
        questionsTextView.setVisibility(View.VISIBLE);
        respA.setVisibility(View.VISIBLE);
        respB.setVisibility(View.VISIBLE);
        respC.setVisibility(View.VISIBLE);
        respD.setVisibility(View.VISIBLE);
        nextButton.setVisibility(View.VISIBLE);
        resultCard.setVisibility(View.VISIBLE);
        userAvatar.setVisibility(View.VISIBLE);

        quizConfiguration = (QuizConfiguration) getIntent().getSerializableExtra("config");
        if (quizConfiguration != null) {
            String currentDifficulty = quizConfiguration.getDifficulty();
            Log.d("TAG", "startDailyQuiz: "+currentDifficulty);
            difficultyLevelTextView.setText(currentDifficulty);

            getQuestions();
        }
    }

    @Override
    public void onClick(View v) {
        Button clickedButton = (Button) v;

        if (clickedButton.getId() == R.id.next_button) {
            if (questionIndex < 10) {
                if (!Objects.equals(selectedAnswer, "null")) {
                    highlightAnswers();

                    if (Objects.equals(selectedAnswer, quiz.results.get(questionIndex).correct_answer)) {
                        score++;
                        correctAnswer++;
                        correctAnswersTextView.setText(correctAnswer + " / 5");
                    }

                    // Add a delay to allow users to see the correct and incorrect answers highlighted
                    new Handler().postDelayed(() -> {
                        questionIndex++;
                        setQuestionsLeftTextView();
                        setValuesToQuiz(quiz, questionIndex);
                        selectedAnswer = "null";

                        respA.setBackgroundColor(Color.parseColor("#262956"));
                        respB.setBackgroundColor(Color.parseColor("#262956"));
                        respC.setBackgroundColor(Color.parseColor("#262956"));
                        respD.setBackgroundColor(Color.parseColor("#262956"));

                        countdownNumberTextView.setTextColor(Color.WHITE);
                        countDownTimer.cancel();
                        createCountDownTimer(16000);
                        countDownTimer.start();
                    }, 500);
                } else {
                    Toast.makeText(DailyQuizActivity.this, "You have to select an answer!", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            selectedAnswer = clickedButton.getText().toString();
            respA.setBackgroundColor(Color.parseColor("#262956"));
            respB.setBackgroundColor(Color.parseColor("#262956"));
            respC.setBackgroundColor(Color.parseColor("#262956"));
            respD.setBackgroundColor(Color.parseColor("#262956"));
            clickedButton.setBackgroundColor(Color.parseColor("#F8D34D"));
        }
    }

    private void highlightAnswers() {
        if (respA.getText().toString().equals(quiz.results.get(questionIndex).correct_answer)) {
            respA.setBackgroundColor(Color.parseColor("#3FDBA3"));
        } else if (respA.getText().toString().equals(selectedAnswer)) {
            respA.setBackgroundColor(Color.parseColor("#FF5B5B"));
        }

        if (respB.getText().toString().equals(quiz.results.get(questionIndex).correct_answer)) {
            respB.setBackgroundColor(Color.parseColor("#3FDBA3"));
        } else if (respB.getText().toString().equals(selectedAnswer)) {
            respB.setBackgroundColor(Color.parseColor("#FF5B5B"));
        }

        if (respC.getText().toString().equals(quiz.results.get(questionIndex).correct_answer)) {
            respC.setBackgroundColor(Color.parseColor("#3FDBA3"));
        } else if (respC.getText().toString().equals(selectedAnswer)) {
            respC.setBackgroundColor(Color.parseColor("#FF5B5B"));
        }

        if (respD.getText().toString().equals(quiz.results.get(questionIndex).correct_answer)) {
            respD.setBackgroundColor(Color.parseColor("#3FDBA3"));
        } else if (respD.getText().toString().equals(selectedAnswer)) {
            respD.setBackgroundColor(Color.parseColor("#FF5B5B"));
        }
    }


    private void setQuestionsLeftTextView() {
        String helper = "Questions left: " + --totalQuestions;
        if (totalQuestions == 0) {
            quizEnded = true;
        }
        questionsLeftTextView.setText(helper);
    }

    private void getQuestions() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://opentdb.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Request request = retrofit.create(Request.class);
        request.get(
                quizConfiguration.getNumberOfQuestions(),
                quizConfiguration.getDifficulty(),
                quizConfiguration.getCategory()).enqueue(new Callback<QuizObject>() {

            @Override
            public void onResponse(@NonNull Call<QuizObject> call, @NonNull Response<QuizObject> response) {
                if (response.body() != null && response.body().results != null) {
                    QuizObject quiz = response.body();
                    setGlobalVariableQuiz(quiz);
                    setValuesToQuiz(quiz, 0);
                } else {
                    questionsTextView.setText(R.string.questions_were_not_generated);
                    startActivity(new Intent(getApplicationContext(), DailyQuizActivity.class));
                }
                dialogObject.dismissDialog();
                countDownTimer.start();
            }

            @Override
            public void onFailure(@NonNull Call<QuizObject> call, @NonNull Throwable t) {
                questionsTextView.setText(R.string.questions_were_not_generated);
                Intent intent = new Intent(getApplicationContext(), PracticeQuizActivity.class);
                startActivity(intent);
            }
        });
    }

    private void saveLevelProgress(String category, String difficulty) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Log.d("TAG", "saveLevelProgress: "+difficulty);
        editor.putString(category + "_difficulty", difficulty);
        editor.apply();
    }

    private String getLevelProgress(String category) {
        return sharedPreferences.getString(category + "_difficulty", "easy");
    }

    private void checkAndProceedToNextLevel(String category, int score) {
        if (quizEnded) {
            quizEnded = false;
            if (score >= 3) { // 60% of 5 questions
                String currentLevel = getLevelProgress(category);
                Log.d("TAG", "checkAndProceedToNextLevel: "+currentLevel);
                if (currentLevel.equals("easy")) {
                    saveLevelProgress(category, "medium");
                } else if (currentLevel.equals("medium")) {
                    saveLevelProgress(category, "hard");
                }
            }
        }
    }


    private void setGlobalVariableQuiz(QuizObject quiz) {
        this.quiz = quiz;
    }

    private void setValuesToQuiz(QuizObject quiz, int index) {
        if (index < 5) {
            String question = quiz.results.get(index).getQuestion();
            question = question.replace("&quot;", "'");
            question = question.replace("&#039;", "'");
            question = question.replace("&amp;", "&");
            question = question.replace("&eacute;", "e");
            questionsTextView.setText(question);
            int[] position = getRandomIndexVector();
            Random rand = new Random();
            int randomOrder = (rand.nextInt((4 - 1) + 1) + 1) - 1;

            switch (randomOrder) {
                case 0:
                    respA.setText(quiz.results.get(index).getCorrect_answer()
                            .replace("&quot;", "'")
                            .replace("&#039;", "'")
                            .replace("&amp;", "&"));
                    respB.setText(quiz.results.get(index).getIncorrect_answers().get(position[0])
                            .replace("&quot;", "'")
                            .replace("&#039;", "'")
                            .replace("&amp;", "&"));
                    respC.setText(quiz.results.get(index).getIncorrect_answers().get(position[1])
                            .replace("&quot;", "'")
                            .replace("&#039;", "'")
                            .replace("&amp;", "&"));
                    respD.setText(quiz.results.get(index).getIncorrect_answers().get(position[2])
                            .replace("&quot;", "'")
                            .replace("&#039;", "'")
                            .replace("&amp;", "&"));
                    break;

                case 1:
                    respA.setText(quiz.results.get(index).getIncorrect_answers().get(position[0])
                            .replace("&quot;", "'")
                            .replace("&#039;", "'")
                            .replace("&amp;", "&"));
                    respB.setText(quiz.results.get(index).getCorrect_answer()
                            .replace("&quot;", "'")
                            .replace("&#039;", "'")
                            .replace("&amp;", "&"));
                    respC.setText(quiz.results.get(index).getIncorrect_answers().get(position[1])
                            .replace("&quot;", "'")
                            .replace("&#039;", "'")
                            .replace("&amp;", "&"));
                    respD.setText(quiz.results.get(index).getIncorrect_answers().get(position[2])
                            .replace("&quot;", "'")
                            .replace("&#039;", "'")
                            .replace("&amp;", "&"));
                    break;

                case 2:
                    respA.setText(quiz.results.get(index).getIncorrect_answers().get(position[0])
                            .replace("&quot;", "'")
                            .replace("&#039;", "'")
                            .replace("&amp;", "&"));
                    respB.setText(quiz.results.get(index).getIncorrect_answers().get(position[1])
                            .replace("&quot;", "'")
                            .replace("&#039;", "'")
                            .replace("&amp;", "&"));
                    respC.setText(quiz.results.get(index).getCorrect_answer()
                            .replace("&quot;", "'")
                            .replace("&#039;", "'")
                            .replace("&amp;", "&"));
                    respD.setText(quiz.results.get(index).getIncorrect_answers().get(position[2])
                            .replace("&quot;", "'")
                            .replace("&#039;", "'")
                            .replace("&amp;", "&"));
                    break;

                default:
                    respA.setText(quiz.results.get(index).getIncorrect_answers().get(position[0])
                            .replace("&quot;", "'")
                            .replace("&#039;", "'")
                            .replace("&amp;", "&"));
                    respB.setText(quiz.results.get(index).getIncorrect_answers().get(position[1])
                            .replace("&quot;", "'")
                            .replace("&#039;", "'")
                            .replace("&amp;", "&"));
                    respC.setText(quiz.results.get(index).getIncorrect_answers().get(position[2])
                            .replace("&quot;", "'")
                            .replace("&#039;", "'")
                            .replace("&amp;", "&"));
                    respD.setText(quiz.results.get(index).getCorrect_answer()
                            .replace("&quot;", "'")
                            .replace("&#039;", "'")
                            .replace("&amp;", "&"));
                    break;
            }

        } else {
            countDownTimer.onFinish();
            String category = quizConfiguration.getCategory();
            checkAndProceedToNextLevel(category, score);
            // TODO change ltr
            dialogObject.seeDailyQuizResultsDialog(score, score, quizConfiguration.getCategory());

            questionsLeftTextView.setVisibility(View.GONE);
            countdownTextTextView.setVisibility(View.GONE);
            countdownNumberTextView.setVisibility(View.GONE);
            difficultyLevelTextView.setVisibility(View.GONE);
            difficultyTextView.setVisibility(View.GONE);
            questionsTextView.setVisibility(View.GONE);
            respA.setVisibility(View.GONE);
            respB.setVisibility(View.GONE);
            respC.setVisibility(View.GONE);
            respD.setVisibility(View.GONE);
            nextButton.setVisibility(View.GONE);
            resultCard.setVisibility(View.GONE);
        }
    }

    // Generate random order to questions
    private int[] getRandomIndexVector() {
        int[] indexVector = new int[]{5, 5, 5};
        Random rand = new Random();
        int randomValue;
        indexVector[0] = (rand.nextInt((3 - 1) + 1) + 1) - 1;

        for (int i = 1; i < 3; i++) {
            do {
                randomValue = (rand.nextInt((3 - 1) + 1) + 1) - 1;
                indexVector[i] = randomValue;
            }
            while (!checkDuplicate(i, randomValue, indexVector));
        }
        return indexVector;
    }

    private boolean checkDuplicate(int index, int value, int[] vector) {
        for (int i = index - 1; i >= 0; i--) {
            if (value == vector[i]) {
                return false;
            }
        }
        return true;
    }
}
