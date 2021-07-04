package com.example.kahoot.data.quiz;

public class QuizQuestionChoicesAndAnswerData {
    public enum MEDIA_TYPE {
        NONE,
        IMAGE,
        VIDEO;
    }

    public final int questionNumber;
    public final Integer questionCountdown;
    public final String questionText;
    public final MEDIA_TYPE questionMediaType;
    public final String questionMediaURL;
    public final String[] questionChoicesTextList;
    public final Integer questionChoicesSelectedIndex;
    public final boolean questionChoicesIsSubmitted;

    public QuizQuestionChoicesAndAnswerData(int questionNumber, Integer questionCountdown, String questionText, MEDIA_TYPE questionMediaType, String questionMediaURL, String[] questionChoicesTextList, Integer questionChoicesSelectedIndex, boolean questionChoicesIsSubmitted) {
        this.questionNumber = questionNumber;
        this.questionCountdown = questionCountdown;
        this.questionText = questionText;
        this.questionMediaType = questionMediaType;
        this.questionMediaURL = questionMediaURL;
        this.questionChoicesTextList = questionChoicesTextList;
        this.questionChoicesSelectedIndex = questionChoicesSelectedIndex;
        this.questionChoicesIsSubmitted = questionChoicesIsSubmitted;
    }
}
