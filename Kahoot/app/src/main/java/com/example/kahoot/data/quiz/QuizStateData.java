package com.example.kahoot.data.quiz;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;

interface ListItem {
    int getRepeat();

    int getLength();

    int getExtent();
}

public class QuizStateData implements Serializable {
    public enum TYPE implements ListItem {
        PRE_QUIZ,
        QUESTION_CHOICES,
        QUESTION_ANSWER,
        QUIZ_CLOSE,
        length;

        @Override
        public int getRepeat() {
            return 1;
        }

        @Override
        public int getLength() {
            return 1;
        }

        @Override
        public int getExtent() {
            return getRepeat() * getLength();
        }
    }

    public static final class List implements ListItem {
        private final java.util.List<ListItem> listOfListItem;
        private final int repeat;
        private final int length;

        private List(ListItem[] listOfListItem, int repeat) {
            this.listOfListItem = Arrays.asList(listOfListItem);
            assert new HashSet<>(this.listOfListItem).size() == listOfListItem.length;
            this.repeat = repeat;
            int length = 0;
            for (ListItem listItem : listOfListItem) {
                length += listItem.getLength();
            }
            this.length = length;
        }

        public static List create(ListItem[] listOfListItem, int repeat) throws Exception {
            if (listOfListItem.length < 1 || repeat < 1) {
                throw new Exception();
            }
            return new List(listOfListItem, repeat);
        }

        @Override
        public int getRepeat() {
            return repeat;
        }

        @Override
        public int getLength() {
            return length;
        }

        @Override
        public int getExtent() {
            return getRepeat() * getLength();
        }

        @NonNull
        public QuizStateData getQuizStateDataFromIndex(int index) throws Exception {
            int listItemIndex = 0;
            for (int i = 0; i < this.repeat; i++) {
                for (ListItem listItem : listOfListItem) {
                    if (listItemIndex <= index && index < listItemIndex + listItem.getExtent()) {
                        if (listItem instanceof QuizStateData.TYPE) {
                            return new QuizStateData((TYPE) listItem, i);
                        } else {
                            throw new Exception();
                        }
                    } else {
                        listItemIndex += listItem.getExtent();
                    }
                }
            }
            throw new Exception();
        }

        public int getIndexFromQuizStateData(QuizStateData quizStateData) throws Exception {
            int stateTypeInnerIndex = listOfListItem.indexOf(quizStateData.quizStateType);
            if (stateTypeInnerIndex == -1) {
                throw new Exception();
            }
            return quizStateData.quizStateIndex * this.length + stateTypeInnerIndex;
        }
    }

    public final TYPE quizStateType;
    public final int quizStateIndex;

    public QuizStateData(TYPE quizStateType, int quizStateIndex) {
        this.quizStateType = quizStateType;
        this.quizStateIndex = quizStateIndex;
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }

        if (object instanceof QuizStateData) {
            QuizStateData otherQuizStateData = (QuizStateData) object;
            return (quizStateType == otherQuizStateData.quizStateType && quizStateIndex == otherQuizStateData.quizStateIndex);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Integer.parseInt(String.format(Locale.ENGLISH, "%d0%d", quizStateType.ordinal(), quizStateIndex));
    }
}
