package com.example.kahoot.data.quiz;

import android.graphics.Bitmap;

import com.example.kahoot.helper.quiz.QuizClient;

public class QuizData {
    public final String loginName;
    public final String loginQuizID;
    public final QuizClient quizClient;
    public final String quizName;
    public final Bitmap quizLogo;
    public final int quizTotalNumberOfQuestion;
    public final boolean quizQuestionAnswerShownAfterQuestionChoices;
    public final boolean quizStateIsSynched;
    public final QuizStateData quizStateData;

    public QuizData(String loginName, String loginQuizID, QuizClient quizClient, String quizName, Bitmap quizLogo, int quizTotalNumberOfQuestion, boolean quizQuestionAnswerShownAfterQuestionChoices, boolean quizStateIsSynched, QuizStateData quizStateData) {
        this.loginName = loginName;
        this.loginQuizID = loginQuizID;
        this.quizClient = quizClient;
        this.quizName = quizName;
        this.quizLogo = quizLogo;
        this.quizTotalNumberOfQuestion = quizTotalNumberOfQuestion;
        this.quizQuestionAnswerShownAfterQuestionChoices = quizQuestionAnswerShownAfterQuestionChoices;
        this.quizStateIsSynched = quizStateIsSynched;
        this.quizStateData = quizStateData;
    }
}
