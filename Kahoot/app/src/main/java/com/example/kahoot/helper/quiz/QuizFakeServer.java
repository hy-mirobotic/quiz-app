package com.example.kahoot.helper.quiz;

import com.example.kahoot.data.quiz.QuizStateData;

import java.util.HashMap;

public class QuizFakeServer {
    private static int count = 0;
    private static HashMap<Integer, Integer> memory = new HashMap<>();

    public static QuizStateData getNewState() {
        if (count < 7) {
            QuizStateData.TYPE type = QuizStateData.TYPE.values()[(count % (QuizStateData.TYPE.length.ordinal() - 2)) + 1];
            int index = count / 2;
            count++;
            return new QuizStateData(type, index);
        } else {
            return new QuizStateData(QuizStateData.TYPE.QUIZ_CLOSE, 0);
        }
    }

    public static Boolean setQuestionChoicesSelectedIndex(QuizStateData quizStateData, int selectedIndex) throws Exception {
        memory.put(new QuizStateData(QuizStateData.TYPE.QUESTION_ANSWER, quizStateData.quizStateIndex).hashCode(), selectedIndex);
        return true;
    }

    public static Integer getQuestionChoicesSelectedIndex(QuizStateData quizStateData) throws Exception {
        Integer i = memory.get(quizStateData.hashCode());
        return i;
    }
}
