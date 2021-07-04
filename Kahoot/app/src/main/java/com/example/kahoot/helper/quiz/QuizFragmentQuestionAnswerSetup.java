package com.example.kahoot.helper.quiz;

import android.animation.LayoutTransition;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.kahoot.R;
import com.example.kahoot.data.quiz.QuizQuestionAnswerData;
import com.example.kahoot.data.quiz.QuizQuestionChoicesAndAnswerData;
import com.example.kahoot.helper.AsyncCallback;
import com.example.kahoot.ui.quiz.QuizFragment;

public class QuizFragmentQuestionAnswerSetup extends QuizFragmentQuestionChoicesSetup {
    public QuizFragmentQuestionAnswerSetup(QuizFragment quizFragment, View rootView) {
        super(quizFragment, rootView);
    }

    @Override
    protected void setupQuizFragmentQuizStateData(AsyncCallback.Response<QuizQuestionChoicesAndAnswerData> response) {
        super.setupQuizFragmentQuizStateData(response);
        morphToQuestionAnswer();
    }

    private void morphToQuestionAnswer() {
        AsyncCallback.start(
                quizFragment.getQuizActivity().executorService,
                () -> new AsyncCallback.Response.Success<>(quizFragment.getQuizActivity().getQuizClient().getQuestionAnswerData(quizFragment.quizStateData)),
                AsyncCallback.Response.Error::new,
                (response) -> {
                    if (response instanceof AsyncCallback.Response.Success) {
                        QuizQuestionAnswerData quizQuestionAnswerData = ((AsyncCallback.Response.Success<QuizQuestionAnswerData>) response).data;

                        LinearLayout questionChoicesLayout = rootView.findViewById(R.id.quizQuestionChoicesLayout);

                        // animate answer
                        questionChoicesLayout.getLayoutTransition().addTransitionListener(new LayoutTransition.TransitionListener() {
                            @Override
                            public void startTransition(LayoutTransition transition, ViewGroup container, View view, int transitionType) {

                            }

                            @Override
                            public void endTransition(LayoutTransition transition, ViewGroup container, View view, int transitionType) {
                                if (transitionType == LayoutTransition.CHANGE_DISAPPEARING) {
                                    TextView quizQuestionAnswerFeedback = rootView.findViewById(R.id.quizQuestionAnswerFeedback);

                                    if (quizQuestionAnswerData.questionAnswerSubmittedIndex == null) {
                                        quizQuestionAnswerFeedback.setText(R.string.quizQuestionAnswerNullSubmittedIndexFeedback);
                                    } else {
                                        if (quizQuestionAnswerData.questionAnswerSubmittedIndex.equals(quizQuestionAnswerData.questionAnswerCorrectIndex)) {
                                            quizQuestionAnswerFeedback.setText(R.string.quizQuestionAnswerCorrectFeedback);
                                        } else {
                                            quizQuestionAnswerFeedback.setText(R.string.quizQuestionAnswerIncorrectFeedback);
                                        }
                                    }
                                    quizQuestionAnswerFeedback.setVisibility(View.VISIBLE);

                                    View quizQuestionChoiceCorrect = rootView.findViewById(questionChoiceList[quizQuestionAnswerData.questionAnswerCorrectIndex]);
                                    String text = (String) ((TextView) quizQuestionChoiceCorrect.findViewById(R.id.questionChoiceTextView)).getText();
                                    Drawable background = quizQuestionChoiceCorrect.getBackground();
                                    View quizQuestionAnswerCorrect = rootView.findViewById(R.id.quizQuestionAnswerCorrect);
                                    setupButton(quizQuestionAnswerCorrect, quizQuestionAnswerData.questionAnswerCorrectIndex, text.substring(2), background, false);
                                    ((View) quizQuestionAnswerCorrect.getParent()).setVisibility(View.VISIBLE);
                                }
                            }
                        });

                        for (int i = 0; i < 4; i++) {
                            View questionChoice = rootView.findViewById(questionChoiceList[i]);

                            // set question choice button to non-clickable
                            View questionChoiceButton = questionChoice.findViewById(R.id.questionChoiceButton);
                            questionChoiceButton.setClickable(false);

                            // animate question choice
                            View questionChoiceItem = (View) questionChoice.getParent();
                            if (quizQuestionAnswerData.questionAnswerSubmittedIndex == null) {
                                if (i == 0) {
                                    questionChoiceItem.setVisibility(View.INVISIBLE);
                                } else {
                                    questionChoiceItem.setVisibility(View.GONE);
                                }
                            } else {
                                if (i != quizQuestionAnswerData.questionAnswerSubmittedIndex) {
                                    questionChoiceItem.setVisibility(View.GONE);
                                }
                            }
                        }
                    } else {
                        ((AsyncCallback.Response.Error<QuizQuestionAnswerData>) response).exception.printStackTrace();
                        quizFragment.getQuizActivity().onBackPressed();  // todo: error handling
                    }
                }
        );
    }
}
