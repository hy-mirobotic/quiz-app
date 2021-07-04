package com.example.kahoot.helper.quiz;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;

import androidx.core.os.HandlerCompat;

import com.example.kahoot.data.quiz.QuizData;
import com.example.kahoot.data.quiz.QuizQuestionAnswerData;
import com.example.kahoot.data.quiz.QuizQuestionChoicesAndAnswerData;
import com.example.kahoot.data.quiz.QuizResultData;
import com.example.kahoot.data.quiz.QuizStateData;
import com.example.kahoot.helper.AsyncCallback;

import java.io.Serializable;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class QuizClient implements Serializable {
    public static final Handler handler = HandlerCompat.createAsync(Looper.getMainLooper());

    private final String token;

    private QuizClient(String token) {
        this.token = token;
    }

    public static AsyncCallback.Response.Success<QuizData> submitLogin(String loginName, String loginQuizID) throws Exception {  // todo: backend return or throw
        QuizClient quizClient = new QuizClient("exampleToken");
        return new AsyncCallback.Response.Success<>(new QuizData(
                loginName,
                loginQuizID,
                quizClient,
                "Mi Robotic Quiz",
                Bitmap.createBitmap(64, 64, Bitmap.Config.ARGB_8888),
                4,
                true,
                true,
                quizClient.getQuizStateData()
        ));
    }

    private static void handleQuizStateChange(final AsyncCallback.Response<QuizStateData> response, final AsyncCallback.RunOnUIThread<QuizStateData> callback) {
        handler.post(() -> callback.runOnUIThread(response));
    }

    private AsyncCallback.Response.Success<QuizStateData> awaitQuizStateChange() throws Exception {  // todo: backend return or throw
        SystemClock.sleep(5 * 1000);
        return new AsyncCallback.Response.Success<>(getQuizStateData());
    }

    public Future<?> asyncAwaitQuizStateChange(ExecutorService executorService, final AsyncCallback.RunOnUIThread<QuizStateData> callback) {
        return executorService.submit(() -> {
            try {
                AsyncCallback.Response.Success<QuizStateData> response = awaitQuizStateChange();
                handleQuizStateChange(response, callback);
            } catch (Exception e) {
                AsyncCallback.Response.Error<QuizStateData> errorResponse = new AsyncCallback.Response.Error<>(e);
                handleQuizStateChange(errorResponse, callback);
            }
        });
    }

    public QuizQuestionChoicesAndAnswerData getQuestionChoicesData(QuizStateData quizStateData) throws Exception {  // todo: backend return or throw
        return new QuizQuestionChoicesAndAnswerData(
                1,
                5,
                "?",
                QuizQuestionChoicesAndAnswerData.MEDIA_TYPE.NONE,
                "",
                new String[]{"?", "?", "?", "?",},
                null,
                false
        );
    }

    public Boolean setQuestionChoicesSelectedIndex(QuizStateData quizStateData, int selectedIndex) throws Exception {  // todo: backend return or throw
        return QuizFakeServer.setQuestionChoicesSelectedIndex(quizStateData, selectedIndex);
    }

    public QuizQuestionAnswerData getQuestionAnswerData(QuizStateData quizStateData) throws Exception {  // todo: backend return or throw
        Random random = new Random();
        return new QuizQuestionAnswerData(QuizFakeServer.getQuestionChoicesSelectedIndex(quizStateData), random.nextInt(4));
    }

    public QuizResultData getQuizResultData(QuizStateData quizStateData) throws Exception {  // todo: backend return or throw
        return new QuizResultData(1, 100);
    }

    public Bitmap getImageFromURL(QuizStateData quizStateData, String url) throws Exception {  // todo: backend return or throw
        return Bitmap.createBitmap(64, 64, Bitmap.Config.ARGB_8888);
    }

    private QuizStateData getQuizStateData() throws Exception {  // todo: backend return or throw
//        return new QuizStateData(QuizStateData.TYPE.QUESTION_CHOICES, 0);
        return QuizFakeServer.getNewState();
    }
}