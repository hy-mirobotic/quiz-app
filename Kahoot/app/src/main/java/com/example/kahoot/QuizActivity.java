package com.example.kahoot;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.kahoot.data.quiz.QuizStateData;
import com.example.kahoot.helper.AsyncCallback;
import com.example.kahoot.helper.quiz.QuizClient;
import com.example.kahoot.ui.quiz.QuizFragment;
import com.example.kahoot.helper.quiz.OverlayLayout;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QuizActivity extends AppCompatActivity {
    private ViewPager2 quizPageViewManager;
    private OverlayLayout overlayLayout;

    private String loginName;
    private String loginQuizID;
    private QuizClient quizClient;
    private String quizName;
    private Bitmap quizLogo;
    private int quizTotalNumberOfQuestion;
    private boolean isAnswerShownAfterQuestion;
    private QuizStateData quizStateData;
    private boolean isQuizStateSynchronous;

    private QuizStateData.List quizStateList;

    public final ExecutorService executorService = Executors.newCachedThreadPool();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        Intent intent = getIntent();
        loginName = intent.getStringExtra("loginName");
        loginQuizID = intent.getStringExtra("loginQuizID");
        quizClient = (QuizClient) intent.getSerializableExtra("quizClient");
        quizName = intent.getStringExtra("quizName");
        quizLogo = intent.getParcelableExtra("quizLogo");
        quizTotalNumberOfQuestion = intent.getIntExtra("quizTotalNumberOfQuestion", 0);
        isAnswerShownAfterQuestion = intent.getBooleanExtra("isAnswerShownAfterQuestion", false);
        quizStateData = (QuizStateData) intent.getSerializableExtra("quizState");
        isQuizStateSynchronous = intent.getBooleanExtra("isQuizStateSynchronous", false);

        try {
            this.quizStateList = QuizStateData.List.create(isAnswerShownAfterQuestion ? new QuizStateData.TYPE[]{QuizStateData.TYPE.QUESTION_CHOICES, QuizStateData.TYPE.QUESTION_ANSWER} : new QuizStateData.TYPE[]{QuizStateData.TYPE.QUESTION_CHOICES}, quizTotalNumberOfQuestion);
        } catch (Exception exception) {
            exception.printStackTrace();
            onBackPressed();
            // todo: error handling
        }

        this.quizPageViewManager = findViewById(R.id.quizPageViewManager);
        QuizPageDataManager quizPageDataManager = new QuizPageDataManager(this);
        this.quizPageViewManager.setAdapter(quizPageDataManager);
        overlayLayout = new OverlayLayout(findViewById(R.id.overlayLayout));

        this.quizPageViewManager.setUserInputEnabled(!isQuizStateSynchronous);
        changePage(quizStateData, false);
    }

    @Override
    protected void onDestroy() {
        executorService.shutdownNow();
        super.onDestroy();
    }

    public OverlayLayout getQuizOverlayLayout() {
        return overlayLayout;
    }

    public String getLoginName() {
        return loginName;
    }

    public String getLoginQuizID() {
        return loginQuizID;
    }

    public QuizClient getQuizClient() {
        return quizClient;
    }

    public String getQuizName() {
        return quizName;
    }

    public Bitmap getQuizLogo() {
        return quizLogo;
    }

    public int getQuizTotalNumberOfQuestion() {
        return quizTotalNumberOfQuestion;
    }

    public boolean getIsAnswerShownAfterQuestion() {
        return isAnswerShownAfterQuestion;
    }

    public QuizStateData getQuizState() {
        return quizStateData;
    }

    public void setQuizState(QuizStateData quizStateData) {
        this.quizStateData = quizStateData;
    }

    public boolean getIsQuizStateSynchronous() {
        return isQuizStateSynchronous;
    }

    public void asyncChangeState() {
        quizClient.asyncAwaitQuizStateChange(executorService, this::changeState);
    }

    private void changeState(AsyncCallback.Response<QuizStateData> response) {
        if (response instanceof AsyncCallback.Response.Success) {
            QuizStateData newQuizStateData = ((AsyncCallback.Response.Success<QuizStateData>) response).data;
            changePage(((AsyncCallback.Response.Success<QuizStateData>) response).data, quizStateData.quizStateType != QuizStateData.TYPE.QUESTION_CHOICES || newQuizStateData.quizStateType != QuizStateData.TYPE.QUESTION_ANSWER || quizStateData.quizStateIndex != newQuizStateData.quizStateIndex);
        } else {
            new Exception("changeState error. ").printStackTrace();
            onBackPressed();
            // todo: error handling
        }
    }

    private void changePage(QuizStateData quizStateData, boolean smooth) {
        int index = 0;
        try {
            index = quizStateList.getIndexFromQuizStateData(quizStateData);
        } catch (Exception exception) {
            index = quizStateList.getExtent() + 1;
//            exception.printStackTrace();
//            onBackPressed();  // todo: error handling
        }
        quizPageViewManager.setCurrentItem(index, smooth);
    }

    private static final class QuizPageDataManager extends FragmentStateAdapter {
        private final QuizActivity quizActivity;

        public QuizPageDataManager(FragmentActivity fragmentActivity) {
            super(fragmentActivity);
            this.quizActivity = (QuizActivity) fragmentActivity;
        }

        @NotNull
        @Override
        public Fragment createFragment(int position) {
            try {
                return new QuizFragment(quizActivity.quizStateList.getQuizStateDataFromIndex(position));
            } catch (Exception exception) {
                return new QuizFragment(new QuizStateData(QuizStateData.TYPE.QUIZ_CLOSE, 0));  // todo error handling
            }
        }

        @Override
        public int getItemCount() {
            return quizActivity.quizStateList.getExtent() + 1;
        }
    }
}