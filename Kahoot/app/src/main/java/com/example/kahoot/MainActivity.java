package com.example.kahoot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kahoot.data.quiz.QuizStateData;
import com.example.kahoot.helper.AsyncCallback;
import com.example.kahoot.helper.quiz.QuizClient;
import com.example.kahoot.data.quiz.QuizData;
import com.example.kahoot.helper.quiz.OverlayLayout;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainActivity extends AppCompatActivity {
    private OverlayLayout overlayLayout;

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        overlayLayout = new OverlayLayout(findViewById(R.id.overlayLayout));
    }

    @Override
    protected void onDestroy() {
        executorService.shutdownNow();
        super.onDestroy();
    }

    public void onLogin(View view) {
        String loginName = ((EditText) findViewById(R.id.loginNameTextView)).getText().toString();
        String loginQuizID = ((EditText) findViewById(R.id.loginQuizIDTextView)).getText().toString();
        if (loginName.isEmpty() || loginQuizID.isEmpty()) {
            overlayLayout.show(getString(R.string.loginNotFilledErrorMessage));
        } else {
            AsyncCallback.start(executorService, () -> QuizClient.submitLogin(loginName, loginQuizID), AsyncCallback.Response.Error::new, this::loginCallback);
        }
    }

    private void loginCallback(AsyncCallback.Response<QuizData> response) {
        if (response instanceof AsyncCallback.Response.Error) {
            overlayLayout.show(getString(R.string.loginFailedErrorMessage));
        } else {
            QuizData quizData = ((AsyncCallback.Response.Success<QuizData>) response).data;
            if (quizData.quizStateData.quizStateType == QuizStateData.TYPE.PRE_QUIZ) {
                final Future<?>[] asyncAwaitQuizStateChange = new Future<?>[1];
                overlayLayout.show(getString(R.string.loginPreQuizInfoMessage),
                        view -> {
                            asyncAwaitQuizStateChange[0] = quizData.quizClient.asyncAwaitQuizStateChange(executorService,
                                    response1 -> {
                                        overlayLayout.hide(
                                                view1 -> {
                                                    startQuiz(quizData);
                                                }
                                        );
                                    }
                            );
                        }
                        , view -> {
                            Future<?> future = asyncAwaitQuizStateChange[0];
                            if (future != null) {
                                future.cancel(true);
                            }
                        }
                );
            } else if (quizData.quizStateData.quizStateType == QuizStateData.TYPE.QUIZ_CLOSE) {
                overlayLayout.show(getString(R.string.loginPostQuizMessage));
            } else {
                startQuiz(quizData);
            }
        }
    }

    private void startQuiz(QuizData quizData) {
        Intent intent = new Intent(this, QuizActivity.class);
        intent.putExtra("loginName", quizData.loginName);
        intent.putExtra("loginQuizID", quizData.loginQuizID);
        intent.putExtra("quizClient", quizData.quizClient);
        intent.putExtra("quizName", quizData.quizName);
        intent.putExtra("quizLogo", quizData.quizLogo);
        intent.putExtra("quizTotalNumberOfQuestion", quizData.quizTotalNumberOfQuestion);
        intent.putExtra("isAnswerShownAfterQuestion", quizData.quizQuestionAnswerShownAfterQuestionChoices);
        intent.putExtra("quizState", quizData.quizStateData);
        intent.putExtra("isQuizStateSynchronous", quizData.quizStateIsSynched);
        startActivity(intent);
    }
}