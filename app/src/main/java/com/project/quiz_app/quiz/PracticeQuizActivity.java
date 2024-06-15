package com.project.quiz_app.quiz;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.project.quiz_app.DialogObject;
import com.project.quiz_app.R;

import java.util.Objects;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class PracticeQuizActivity extends AppCompatActivity implements View.OnClickListener {

    interface Request {
        @GET("https://opentdb.com/api.php?type=multiple")
        Call<QuizObject> get(@Query("amount") String amount,
                             @Query("difficulty") String difficulty,
                             @Query("category") String category);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    // Quiz variables
    QuizConfiguration quizConfiguration;
    QuizObject quiz;

    // Visual variables
    TextView questionsTextView, userNameTextView, correctAnswersTextView;
    TextView questionsLeftTextView;
    Button respA, respB, respC, respD;
    Button nextButton;
    LinearLayout resultCard;
    ImageView userAvatar;

    // Quiz running variables
    int questionIndex = 0;
    String selectedAnswer = "null";
    int score = 0;
    int totalQuestions = 0;
    int fixedTotalQuestions = 0;
    int correctAnswer = 0;
    private MediaPlayer mediaPlayer;

    // Loading screen
    DialogObject dialogObject = new DialogObject(PracticeQuizActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_quiz);

        mediaPlayer = MediaPlayer.create(this, R.raw.trivia_quiz_bg);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        questionsLeftTextView = findViewById(R.id.questions_left);
        questionsTextView = findViewById(R.id.question);
        respA = findViewById(R.id.A_response);
        respB = findViewById(R.id.B_response);
        respC = findViewById(R.id.C_response);
        respD = findViewById(R.id.D_response);
        nextButton = findViewById(R.id.next_button);
        userAvatar = findViewById(R.id.user_avatar);
        resultCard = findViewById(R.id.result_card);
        userNameTextView = findViewById(R.id.user_name);
        correctAnswersTextView = findViewById(R.id.correct_answers);

        SharedPreferences sharedPreferences = getSharedPreferences("userPreferences", Context.MODE_PRIVATE);
        userNameTextView.setText(sharedPreferences.getString("userName", ""));
        int selectedAvatar = sharedPreferences.getInt("selectedAvatar", -1);

        if (selectedAvatar != -1) {
            userAvatar.setImageResource(selectedAvatar);
        }

        quizConfiguration = (QuizConfiguration) getIntent().getSerializableExtra("config");
        if (quizConfiguration != null) {
            getQuestions();
        } else {
            Toast.makeText(getApplicationContext(), "Something went wrong...", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), QuizMenuActivity.class);
            ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(),
                    R.anim.slide_in_left, android.R.anim.slide_out_right);
            startActivity(intent, options.toBundle());
            finish();
        }

        respA.setOnClickListener(this);
        respB.setOnClickListener(this);
        respC.setOnClickListener(this);
        respD.setOnClickListener(this);
        nextButton.setOnClickListener(this);

        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                dialogObject.closeQuizDialog();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
    }

    @Override
    public void onClick(View v) {
        respA.setBackgroundColor(Color.parseColor("#262956"));
        respB.setBackgroundColor(Color.parseColor("#262956"));
        respC.setBackgroundColor(Color.parseColor("#262956"));
        respD.setBackgroundColor(Color.parseColor("#262956"));

        Button clickedButton = (Button) v;

        if (clickedButton.getId() == R.id.next_button) {
            if (questionIndex <= Integer.parseInt(quizConfiguration.getNumberOfQuestions())) {
                if (!Objects.equals(selectedAnswer, "null")) {
                    highlightAnswers();

                    if (Objects.equals(selectedAnswer, quiz.results.get(questionIndex).correct_answer)) {
                        score++;
                        correctAnswer++;
                        correctAnswersTextView.setText(correctAnswer + " / " + fixedTotalQuestions);
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
                    }, 500);
                } else {
                    Toast.makeText(PracticeQuizActivity.this, "You have to select an answer!", Toast.LENGTH_SHORT).show();
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
        questionsLeftTextView.setText(helper);
    }

    private void getQuestions() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://opentdb.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        dialogObject.startLoadingDialog();
        Request request = retrofit.create(Request.class);
        request.get(
                quizConfiguration.getNumberOfQuestions(),
                quizConfiguration.getDifficulty(),
                quizConfiguration.getCategory()).enqueue(new Callback<QuizObject>() {

            @Override
            public void onResponse(@NonNull Call<QuizObject> call, @NonNull Response<QuizObject> response) {
                if (response.body() != null && response.body().results != null) {
                    QuizObject quiz = response.body();

                    // append question left to textview
                    totalQuestions = Integer.parseInt(quizConfiguration.getNumberOfQuestions());
                    questionsLeftTextView.append(" " + totalQuestions);
                    fixedTotalQuestions = totalQuestions;
                    correctAnswersTextView.setText(correctAnswer + " / " + fixedTotalQuestions);

                    setGlobalVariableQuiz(quiz);
                    setValuesToQuiz(quiz, 0);
                } else {
                    questionsTextView.setText(R.string.questions_were_not_generated);
                    Intent intent = new Intent(getApplicationContext(), PracticeQuizActivity.class);
                    startActivity(intent);
                }
                dialogObject.dismissDialog();
            }

            @Override
            public void onFailure(@NonNull Call<QuizObject> call, @NonNull Throwable t) {
                questionsTextView.setText(R.string.questions_were_not_generated);
                Intent intent = new Intent(getApplicationContext(), PracticeQuizActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setGlobalVariableQuiz(QuizObject quiz) {
        this.quiz = quiz;
    }

    private void setValuesToQuiz(QuizObject quiz, int index) {
        if (index < Integer.parseInt(quizConfiguration.getNumberOfQuestions())) {
            String question = quiz.results.get(index).getQuestion();
            question = question.replace("&quot;", "'");
            question = question.replace("&#039;", "'");
            question = question.replace("&amp;", "&");
            question = question.replace("&eacute;", "e");
            questionsTextView.setText(question);
            int[] position = getRandomIndexVector();
            Random rand = new Random();
            int randomOrder = (rand.nextInt(4));

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
            dialogObject.seeQuizResultsDialog(score, score); // Simplified: only displaying the current score
            questionsLeftTextView.setVisibility(View.GONE);
            questionsTextView.setVisibility(View.GONE);
            respA.setVisibility(View.GONE);
            respB.setVisibility(View.GONE);
            respC.setVisibility(View.GONE);
            respD.setVisibility(View.GONE);
            nextButton.setVisibility(View.GONE);
            userAvatar.setVisibility(View.GONE);
            resultCard.setVisibility(View.GONE);
        }
    }

    // Generate random order to questions
    private int[] getRandomIndexVector() {
        int[] indexVector = new int[]{5, 5, 5};
        Random rand = new Random();
        int randomValue;
        indexVector[0] = (rand.nextInt(3));

        for (int i = 1; i < 3; i++) {
            do {
                randomValue = (rand.nextInt(3));
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
