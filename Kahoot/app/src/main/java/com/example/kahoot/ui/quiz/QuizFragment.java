package com.example.kahoot.ui.quiz;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.kahoot.QuizActivity;
import com.example.kahoot.R;
import com.example.kahoot.data.quiz.QuizStateData;
import com.example.kahoot.databinding.FragmentQuizBinding;
import com.example.kahoot.helper.quiz.QuizFragmentQuestionAnswerSetup;
import com.example.kahoot.helper.quiz.QuizFragmentQuestionChoicesSetup;
import com.example.kahoot.helper.quiz.QuizFragmentQuizCloseSetup;
import com.example.kahoot.helper.quiz.QuizFragmentSetup;

import java.util.concurrent.Future;

public class QuizFragment extends Fragment {
    public final QuizStateData quizStateData;

    private QuizActivity quizActivity;
    private FragmentQuizBinding fragmentQuizBinding;
    private QuizFragmentSetup<?> quizFragmentSetup;

    private Future<?> quizFragmentSetupFuture;

    public QuizFragment(QuizStateData quizStateData) {
        super();
        this.quizStateData = quizStateData;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        quizActivity = (QuizActivity) requireActivity();

        fragmentQuizBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_quiz, container, false);
        View view = fragmentQuizBinding.getRoot();

        setupPageQuizData(view);
        if (!quizActivity.getIsQuizStateSynchronous()) {
            setupPageQuizStateData(view);
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (quizActivity.getIsQuizStateSynchronous()) {
            setupPageQuizStateData(requireView());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        quizActivity.getQuizOverlayLayout().hide();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        quizFragmentSetupFuture.cancel(true);
        quizFragmentSetup.close();
    }

    public QuizActivity getQuizActivity() {
        return quizActivity;
    }

    public FragmentQuizBinding getFragmentQuizBinding() {
        return fragmentQuizBinding;
    }

    private void setupPageQuizData(View rootView) {
        switch (quizStateData.quizStateType) {
            case QUESTION_CHOICES:
                quizFragmentSetup = new QuizFragmentQuestionChoicesSetup(this, rootView);
                break;
            case QUESTION_ANSWER:
                quizFragmentSetup = new QuizFragmentQuestionAnswerSetup(this, rootView);
                break;
            case QUIZ_CLOSE:
                quizFragmentSetup = new QuizFragmentQuizCloseSetup(this, rootView);
                break;
            default:
                new Exception("setupPageQuizData error. ").printStackTrace();
                quizActivity.onBackPressed();  // todo: error handling
                break;
        }
        quizFragmentSetup.setupQuizFragmentQuizData();
    }

    private void setupPageQuizStateData(View rootView) {
        quizFragmentSetupFuture = quizFragmentSetup.setupQuizFragmentQuizStateData(quizActivity.executorService);
        switch (quizStateData.quizStateType) {
            case QUESTION_CHOICES:
            case QUESTION_ANSWER:
                if (quizActivity.getIsQuizStateSynchronous()) {
                    quizActivity.asyncChangeState();
                }
                break;
            case QUIZ_CLOSE:
                break;
            default:
                new Exception("setupPageQuizStateData error. ").printStackTrace();
                quizActivity.onBackPressed();  // todo: error handling
                break;
        }
    }
}