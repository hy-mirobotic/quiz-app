package com.example.kahoot.helper.quiz;

import android.content.Intent;
import android.view.View;

import com.example.kahoot.QuizActivity;
import com.example.kahoot.QuizResultActivity;
import com.example.kahoot.R;
import com.example.kahoot.helper.AsyncCallback;
import com.example.kahoot.ui.quiz.QuizFragment;

public class QuizFragmentQuizCloseSetup extends QuizFragmentSetup<Object> {
    public QuizFragmentQuizCloseSetup(QuizFragment quizFragment, View rootView) {
        super(quizFragment, rootView);
    }

    @Override
    public void setupQuizFragmentQuizData() {
        View quizCloseLayout = rootView.findViewById(R.id.quizCloseLayout);
        quizCloseLayout.setVisibility(View.VISIBLE);
        View quizCloseButton = quizCloseLayout.findViewById(R.id.quizCloseButton);
        quizCloseButton.setOnClickListener(v -> goToResultActivity());
    }

    @Override
    public void close() {

    }

    @Override
    protected AsyncCallback.Response<Object> getQuizStateData() throws Exception {
        return null;
    }

    @Override
    protected AsyncCallback.Response<Object> getQuizStateDataError(Exception e) {
        return null;
    }

    @Override
    protected void setupQuizFragmentQuizStateData(AsyncCallback.Response<Object> response) {

    }

    private void goToResultActivity() {
        QuizActivity quizActivity = quizFragment.getQuizActivity();
        Intent intent = new Intent(quizActivity, QuizResultActivity.class);
        intent.putExtra("loginName", quizActivity.getLoginName());
        intent.putExtra("loginQuizID", quizActivity.getLoginQuizID());
        intent.putExtra("quizClient", quizActivity.getQuizClient());
        intent.putExtra("quizName", quizActivity.getQuizName());
        intent.putExtra("quizLogo", quizActivity.getQuizLogo());
        intent.putExtra("quizTotalNumberOfQuestion", quizActivity.getQuizTotalNumberOfQuestion());
        intent.putExtra("isAnswerShownAfterQuestion", quizActivity.getIsAnswerShownAfterQuestion());
//        intent.putExtra("quizState", );
//        intent.putExtra("isQuizStateSynchronous", );
        quizActivity.startActivity(intent);
    }
}
