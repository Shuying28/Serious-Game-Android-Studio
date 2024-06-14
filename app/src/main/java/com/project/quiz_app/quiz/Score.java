package com.project.quiz_app.quiz;

import java.io.Serializable;

public class Score implements Serializable {
    private int totalScoreMath;
    private int correctAnswersMath;
    private int totalQuestionsMath;

    private int totalScoreScience;
    private int correctAnswersScience;
    private int totalQuestionsScience;

    private int totalScoreHistory;
    private int correctAnswersHistory;
    private int totalQuestionsHistory;

    // Math
    public int getTotalScoreMath() {
        return totalScoreMath;
    }

    public void setTotalScoreMath(int totalScoreMath) {
        this.totalScoreMath = totalScoreMath;
    }

    public int getCorrectAnswersMath() {
        return correctAnswersMath;
    }

    public void setCorrectAnswersMath(int correctAnswersMath) {
        this.correctAnswersMath = correctAnswersMath;
    }

    public int getTotalQuestionsMath() {
        return totalQuestionsMath;
    }

    public void setTotalQuestionsMath(int totalQuestionsMath) {
        this.totalQuestionsMath = totalQuestionsMath;
    }

    public double getCorrectRateMath() {
        return totalQuestionsMath == 0 ? 0 : (double) correctAnswersMath / totalQuestionsMath * 100;
    }

    // Science
    public int getTotalScoreScience() {
        return totalScoreScience;
    }

    public void setTotalScoreScience(int totalScoreScience) {
        this.totalScoreScience = totalScoreScience;
    }

    public int getCorrectAnswersScience() {
        return correctAnswersScience;
    }

    public void setCorrectAnswersScience(int correctAnswersScience) {
        this.correctAnswersScience = correctAnswersScience;
    }

    public int getTotalQuestionsScience() {
        return totalQuestionsScience;
    }

    public void setTotalQuestionsScience(int totalQuestionsScience) {
        this.totalQuestionsScience = totalQuestionsScience;
    }

    public double getCorrectRateScience() {
        return totalQuestionsScience == 0 ? 0 : (double) correctAnswersScience / totalQuestionsScience * 100;
    }

    // History
    public int getTotalScoreHistory() {
        return totalScoreHistory;
    }

    public void setTotalScoreHistory(int totalScoreHistory) {
        this.totalScoreHistory = totalScoreHistory;
    }

    public int getCorrectAnswersHistory() {
        return correctAnswersHistory;
    }

    public void setCorrectAnswersHistory(int correctAnswersHistory) {
        this.correctAnswersHistory = correctAnswersHistory;
    }

    public int getTotalQuestionsHistory() {
        return totalQuestionsHistory;
    }

    public void setTotalQuestionsHistory(int totalQuestionsHistory) {
        this.totalQuestionsHistory = totalQuestionsHistory;
    }

    public double getCorrectRateHistory() {
        return totalQuestionsHistory == 0 ? 0 : (double) correctAnswersHistory / totalQuestionsHistory * 100;
    }
}
