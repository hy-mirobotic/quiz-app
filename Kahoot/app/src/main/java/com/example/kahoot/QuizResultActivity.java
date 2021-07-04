package com.example.kahoot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.kahoot.data.quiz.QuizResultData;
import com.example.kahoot.data.quiz.QuizStateData;
import com.example.kahoot.helper.AsyncCallback;
import com.example.kahoot.helper.quiz.QuizClient;
import com.example.kahoot.helper.quiz.OverlayLayout;

import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QuizResultActivity extends AppCompatActivity {
    private OverlayLayout overlayLayout;

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        overlayLayout = new OverlayLayout(findViewById(R.id.overlayLayout));
        View quizResultCloseButton = findViewById(R.id.quizResultCloseButton);
        quizResultCloseButton.setClickable(true);
        quizResultCloseButton.setFocusable(true);
        quizResultCloseButton.setOnClickListener(v -> goToMainActivity());
        Intent intent = getIntent();
        QuizClient quizClient = (QuizClient) intent.getSerializableExtra("quizClient");
        AsyncCallback.start(executorService, () -> new AsyncCallback.Response.Success<>(quizClient.getQuizResultData(new QuizStateData(QuizStateData.TYPE.QUIZ_CLOSE, 0))), AsyncCallback.Response.Error::new, (response) -> {
            if (response instanceof AsyncCallback.Response.Success) {
                QuizResultData quizResultData = ((AsyncCallback.Response.Success<QuizResultData>) response).data;
                TextView quizResultTextView = findViewById(R.id.quizResultTextView);
                quizResultTextView.setText(String.format(Locale.ENGLISH, "%d/%d", quizResultData.quizResultObtained, quizResultData.quizResultMaximum));
            } else {
                this.finishAffinity();  // todo: error handling
            }
        });
    }

    @Override
    protected void onDestroy() {
        executorService.shutdownNow();
        super.onDestroy();
    }

    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}