package com.example.kahoot.helper.quiz;

import android.view.View;

import com.example.kahoot.helper.AsyncCallback;
import com.example.kahoot.ui.quiz.QuizFragment;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public abstract class QuizFragmentSetup<T> {
    public final AsyncCallback.RunOffUIThread<T> getQuizStateData;
    public final AsyncCallback.RunOffUIThreadError<T> getQuizStateDataError;

    public final AsyncCallback.RunOnUIThread<T> setupQuizFragmentQuizStateData;

    public final QuizFragment quizFragment;
    public final View rootView;

    public QuizFragmentSetup(QuizFragment quizFragment, View rootView) {
        this.getQuizStateData = this::getQuizStateData;
        this.getQuizStateDataError = this::getQuizStateDataError;

        this.setupQuizFragmentQuizStateData = this::setupQuizFragmentQuizStateData;

        this.quizFragment = quizFragment;
        this.rootView = rootView;
    }

    public abstract void setupQuizFragmentQuizData();

    public Future<?> setupQuizFragmentQuizStateData(ExecutorService executorService) {
        return AsyncCallback.start(executorService, getQuizStateData, getQuizStateDataError, setupQuizFragmentQuizStateData);
    }

    public abstract void close();

    protected abstract AsyncCallback.Response<T> getQuizStateData() throws Exception;

    protected abstract AsyncCallback.Response<T> getQuizStateDataError(Exception e);

    protected abstract void setupQuizFragmentQuizStateData(AsyncCallback.Response<T> response);
}
