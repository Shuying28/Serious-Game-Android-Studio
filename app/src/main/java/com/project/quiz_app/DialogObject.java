package com.project.quiz_app;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.quiz_app.quiz.DailyQuizActivity;
import com.project.quiz_app.quiz.QuizMenuActivity;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class DialogObject {

    Activity activity;
    AlertDialog dialog;

    public DialogObject(Activity activity) {
        this.activity = activity;
    }

    public void startLoadingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        activity.getLayoutInflater();
        builder.setView(R.layout.dialog_loading_screen);
        builder.setCancelable(false);

        this.dialog = builder.create();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public void seeQuizResultsDialog(int score) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_quiz_results, null);
        builder.setView(view);
        builder.setCancelable(false);

        this.dialog = builder.create();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        Button okButton = view.findViewById(R.id.ok_quiz_results);
        TextView totalScoreTextView = view.findViewById(R.id.textview_show_total_score);

        String stringToSetToTextView = totalScoreTextView.getText().toString();
        stringToSetToTextView += " " + score;
        totalScoreTextView.setText(stringToSetToTextView);

        okButton.setOnClickListener(v -> {
            dismissDialog();
            Intent intent = new Intent(activity, QuizMenuActivity.class);
            activity.startActivity(intent);
            activity.finish();
        });
    }

    public void closeQuizDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_close_quiz, null);
        builder.setView(view);
        builder.setCancelable(false);

        this.dialog = builder.create();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        Button yesButton = view.findViewById(R.id.yes_close_quiz_button);
        Button noButton = view.findViewById(R.id.no_close_quiz_button);

        yesButton.setOnClickListener(v -> {
            dismissDialog();
            Intent intent = new Intent(activity, MainActivity.class);
            ActivityOptions options = ActivityOptions.makeCustomAnimation(activity,
                    R.anim.slide_in_left, android.R.anim.slide_out_right);
            activity.startActivity(intent, options.toBundle());
            activity.finish();
        });

        noButton.setOnClickListener(v -> dismissDialog());
    }


    public void closeDailyQuizDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_exit_daily_quiz, null);
        builder.setView(view);
        builder.setCancelable(false);

        this.dialog = builder.create();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        Button yesButton = view.findViewById(R.id.yes_close_daily_quiz_button);
        Button noButton = view.findViewById(R.id.no_close_daily_quiz_button);

        yesButton.setOnClickListener(v -> {
            dismissDialog();

            Intent intent = new Intent(activity, MainActivity.class);
            ActivityOptions options = ActivityOptions.makeCustomAnimation(activity,
                    R.anim.slide_in_left, android.R.anim.slide_out_right);
            activity.startActivity(intent, options.toBundle());
            activity.finish();
        });

        noButton.setOnClickListener(v -> dismissDialog());
    }


    // dailyQuizDialog will be displayed when the user enters the QuizMenu and daily quiz is available
    // The user can select Proceed to start the daily quiz, or Later
    public void dailyQuizDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_daily_quiz_available, null);
        builder.setView(view);
        builder.setCancelable(false);

        this.dialog = builder.create();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        Button proceedButton = view.findViewById(R.id.proceed_with_daily_quiz);
        Button laterButton = view.findViewById(R.id.later_daily_quiz);

        proceedButton.setOnClickListener(v -> {
            dismissDialog();
            Intent intent = new Intent(activity, DailyQuizActivity.class);
            activity.startActivity(intent);
            activity.finish();
        });

        laterButton.setOnClickListener(v -> dismissDialog());
    }

    public void seeDailyQuizResultsDialog(int score, String category) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_quiz_results, null);
        builder.setView(view);
        builder.setCancelable(false);

        this.dialog = builder.create();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        Button okButton = view.findViewById(R.id.ok_quiz_results);
        TextView totalScoreTextView = view.findViewById(R.id.textview_show_total_score);

        String stringToSetToTextView = "Total score: " + score;
        totalScoreTextView.setText(stringToSetToTextView);

        okButton.setOnClickListener(v -> {
            dismissDialog();
            Intent intent = new Intent(activity, MainActivity.class);
            intent.putExtra("category", category);
            intent.putExtra("score", score);
            intent.putExtra("totalQuestions", 5);
            activity.startActivity(intent);
            activity.finish();
        });
    }

    public void dailyQuizNotAvailableDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_daily_quiz_not_available, null);
        builder.setView(view);
        builder.setCancelable(false);

        this.dialog = builder.create();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        TextView availableDateTextView = view.findViewById(R.id.daily_quiz_available_date);

        // Fetch the available date for daily quiz
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        assert user != null;

        DatabaseReference date = database.getReference()
                .child("Users").child(user.getUid()).child("dailyQuizAvailableDate");

        date.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               String availableDate = "Available date: ";
                availableDate += dataSnapshot.getValue(String.class);
                availableDateTextView.setText(availableDate);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Error
            }
        });

        Button okButton = view.findViewById(R.id.ok_quiz_results);
        okButton.setOnClickListener(v -> {
            Intent intent = new Intent(activity, MainActivity.class);
            ActivityOptions options = ActivityOptions.makeCustomAnimation(activity,
                    R.anim.slide_in_left, android.R.anim.slide_out_right);
            activity.startActivity(intent, options.toBundle());
            activity.finish();
        });
    }



    public CompletableFuture<Boolean> dailyQuizInfoDialog() {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_info_daily_quiz, null);
        builder.setView(view);
        builder.setCancelable(false);

        this.dialog = builder.create();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        Button startDailyQuizButton = view.findViewById(R.id.start_daily_quiz_button);
        startDailyQuizButton.setOnClickListener(v -> {
            dismissDialog();
            future.complete(true);
        });

        Button tryItLaterButton = view.findViewById(R.id.try_it_later_button);
        tryItLaterButton.setOnClickListener(v -> {
            dismissDialog();
            Intent intent = new Intent(activity, MainActivity.class);
            ActivityOptions options = ActivityOptions.makeCustomAnimation(activity,
                    R.anim.slide_in_left, android.R.anim.slide_out_right);
            activity.startActivity(intent, options.toBundle());
            activity.finish();
        });
        return future;
    }
    public void exitAppDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_exit_quiz_app, null);
        builder.setView(view);
        builder.setCancelable(false);

        this.dialog = builder.create();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        Button yesButton = view.findViewById(R.id.yes_close_app_button);
        Button noButton = view.findViewById(R.id.no_close_app_button);

        yesButton.setOnClickListener(v -> {
            dismissDialog();
            activity.finish();
        });
        noButton.setOnClickListener(v -> dismissDialog());
    }


    public void noInternetConnectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_no_internet_connection, null);
        builder.setView(view);
        builder.setCancelable(false);

        this.dialog = builder.create();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        Button okButton = view.findViewById(R.id.ok_no_internet_button);

        okButton.setOnClickListener(v -> {
            dismissDialog();
            activity.finish();
        });
    }

    public void dismissDialog() {
        dialog.dismiss();
    }

}
