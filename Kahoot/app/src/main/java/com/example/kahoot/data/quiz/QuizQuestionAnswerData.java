package com.example.kahoot.data.quiz;

public class QuizQuestionAnswerData {
    public final Integer questionAnswerSubmittedIndex;
    public final Integer questionAnswerCorrectIndex;

    public QuizQuestionAnswerData(Integer questionAnswerSubmittedIndex, Integer questionAnswerCorrectIndex) {
        this.questionAnswerSubmittedIndex = questionAnswerSubmittedIndex;
        this.questionAnswerCorrectIndex = questionAnswerCorrectIndex;
    }
}
