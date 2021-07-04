package com.example.kahoot.helper.quiz;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.kahoot.QuizActivity;
import com.example.kahoot.R;
import com.example.kahoot.data.quiz.QuizQuestionChoicesAndAnswerData;
import com.example.kahoot.helper.AsyncCallback;
import com.example.kahoot.ui.quiz.QuizFragment;

import java.util.Locale;
import java.util.concurrent.Future;

public class QuizFragmentQuestionChoicesSetup extends QuizFragmentSetup<QuizQuestionChoicesAndAnswerData> {
    private CountDownTimer countDownTimer;
    private Future<?> getQuestionBitmapMediaFuture;
    private Future<?> setQuestionChoiceSelectedIndexFuture;

    protected static final int[] questionChoiceList = new int[]{R.id.quizQuestionChoiceA, R.id.quizQuestionChoiceB, R.id.quizQuestionChoiceC, R.id.quizQuestionChoiceD};

    public QuizFragmentQuestionChoicesSetup(QuizFragment quizFragment, View rootView) {
        super(quizFragment, rootView);
    }

    @Override
    public void close() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        if (getQuestionBitmapMediaFuture != null) {
            getQuestionBitmapMediaFuture.cancel(true);
        }
        if (setQuestionChoiceSelectedIndexFuture != null) {
            setQuestionChoiceSelectedIndexFuture.cancel(false);
        }
    }

    @Override
    public void setupQuizFragmentQuizData() {
        QuizActivity quizActivity = quizFragment.getQuizActivity();

        rootView.findViewById(R.id.quizQuestionChoiceAndAnswerLayout).setVisibility(View.VISIBLE);

        Bitmap quizLogo = quizActivity.getQuizLogo();
        ImageView quizLogoImageView = rootView.findViewById(R.id.quizQuestionChoiceAndAnswerLogoImageView);
        quizLogoImageView.setImageBitmap(quizLogo);

        String quizName = quizActivity.getQuizName();
        TextView quizNameTextView = rootView.findViewById(R.id.quizQuestionChoiceAndAnswerQuizNameTextView);
        quizNameTextView.setText(quizName.toUpperCase(Locale.ENGLISH));

        String quizID = quizActivity.getLoginQuizID();
        TextView quizIDTextView = rootView.findViewById(R.id.quizQuestionChoiceAndAnswerQuizIDTextView);
        quizIDTextView.setText(quizID);
    }

    @Override
    protected AsyncCallback.Response<QuizQuestionChoicesAndAnswerData> getQuizStateData() throws Exception {
        return new AsyncCallback.Response.Success<>(quizFragment.getQuizActivity().getQuizClient().getQuestionChoicesData(quizFragment.quizStateData));
    }

    @Override
    protected AsyncCallback.Response<QuizQuestionChoicesAndAnswerData> getQuizStateDataError(Exception e) {
        return new AsyncCallback.Response.Error<>(e);
    }

    @Override
    protected void setupQuizFragmentQuizStateData(AsyncCallback.Response<QuizQuestionChoicesAndAnswerData> response) {
        if (response instanceof AsyncCallback.Response.Success) {
            QuizQuestionChoicesAndAnswerData quizQuestionChoicesAndAnswerData = ((AsyncCallback.Response.Success<QuizQuestionChoicesAndAnswerData>) response).data;
            setQuestionNumber(quizQuestionChoicesAndAnswerData.questionNumber);
            setCountdown(quizQuestionChoicesAndAnswerData.questionCountdown);
            setQuestionText(quizQuestionChoicesAndAnswerData.questionText);
            setQuestionMedia(quizQuestionChoicesAndAnswerData.questionMediaType, quizQuestionChoicesAndAnswerData.questionMediaURL);
            setQuestionChoices(quizQuestionChoicesAndAnswerData.questionChoicesTextList, quizQuestionChoicesAndAnswerData.questionChoicesSelectedIndex, quizQuestionChoicesAndAnswerData.questionChoicesIsSubmitted);
        } else {
            ((AsyncCallback.Response.Error<QuizQuestionChoicesAndAnswerData>) response).exception.printStackTrace();
            quizFragment.getQuizActivity().onBackPressed();  // todo: error handling
        }
    }

    private void setQuestionNumber(int questionNumber) {
        int quizTotalNumberOfQuestion = quizFragment.getQuizActivity().getQuizTotalNumberOfQuestion();
        TextView questionNumberTextView = rootView.findViewById(R.id.quizQuestionChoiceAndAnswerQuestionNumberTextView);
        questionNumberTextView.setText(String.format(Locale.ENGLISH, "%02d/%02d", questionNumber, quizTotalNumberOfQuestion));
    }

    private void setCountdown(Integer countDown) {
        TextView quizCountdownTextView = rootView.findViewById(R.id.quizQuestionChoiceAndAnswerLayoutQuestionQuestionCountdownTextView);
        if (countDown != null && countDown > 0) {
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }
            countDownTimer = new CountDownTimer(countDown * 1000, 1000) {

                public void onTick(long millisUntilFinished) {
                    quizCountdownTextView.setText(String.format(Locale.ENGLISH, "%d", millisUntilFinished / 1000));
                }

                public void onFinish() {
                    quizCountdownTextView.setText("-");
                }
            }.start();
        } else {
            quizCountdownTextView.setText("-");
        }
    }

    private void setQuestionText(String questionText) {
        TextView questionTextView = rootView.findViewById(R.id.quizQuestionChoiceAndAnswerQuestionTextView);
        questionTextView.setText(questionText);
    }

    private void setQuestionMedia(QuizQuestionChoicesAndAnswerData.MEDIA_TYPE mediaType, String mediaURL) {
        ImageView questionImageView = rootView.findViewById(R.id.quizQuestionChoiceAndAnswerImageView);
        VideoView questionVideoView = rootView.findViewById(R.id.quizQuestionChoiceAndAnswerVideoView);
        questionImageView.setVisibility(View.GONE);
        questionVideoView.setVisibility(View.GONE);
        switch (mediaType) {
            case NONE:
                break;
            case IMAGE:
                if (getQuestionBitmapMediaFuture != null) {
                    getQuestionBitmapMediaFuture.cancel(true);
                }
                getQuestionBitmapMediaFuture = AsyncCallback.start(quizFragment.getQuizActivity().executorService,
                        () -> new AsyncCallback.Response.Success<>(quizFragment.getQuizActivity().getQuizClient().getImageFromURL(quizFragment.quizStateData, mediaURL)),
                        AsyncCallback.Response.Error::new,
                        response -> {
                            if (response instanceof AsyncCallback.Response.Success) {
                                Bitmap bitmap = ((AsyncCallback.Response.Success<Bitmap>) response).data;
                                questionImageView.setImageBitmap(bitmap);
                                questionImageView.setVisibility(View.VISIBLE);
                            } else {
                                ((AsyncCallback.Response.Error<Bitmap>) response).exception.printStackTrace();
                                quizFragment.getQuizActivity().onBackPressed();  // todo: error handling
                            }
                        });
                break;
            case VIDEO:
                questionVideoView.setVideoPath(mediaURL);
                questionVideoView.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void setQuestionChoices(String[] choiceTextList, Integer selectedIndex, boolean isQuestionSubmitted) {
        if (choiceTextList.length == 4) {
            for (int i = 0; i < 4; i++) {
                View questionChoice = rootView.findViewById(questionChoiceList[i]);
                setupButton(questionChoice, i, choiceTextList[i], questionChoice.getBackground(), (selectedIndex != null && i == selectedIndex));

                View questionChoiceButton = questionChoice.findViewById(R.id.questionChoiceButton);
                if (isQuestionSubmitted) {
                    questionChoiceButton.setClickable(false);
                    questionChoiceButton.setFocusable(false);
                } else {
                    questionChoiceButton.setClickable(true);
                    questionChoiceButton.setFocusable(true);
                    int finalI = i;
                    questionChoiceButton.setOnClickListener(
                            v -> {
                                for (int j = 0; j < 4; j++) {
                                    setupButton(rootView.findViewById(questionChoiceList[j]), j, null, null, finalI == j);
                                }

                                if (quizFragment.getQuizActivity().getIsQuizStateSynchronous()) {
                                    quizFragment.getQuizActivity().getQuizOverlayLayout().show(quizFragment.getQuizActivity().getResources().getString(R.string.questionChoiceSelectedWaitDialog));
                                }

                                if (setQuestionChoiceSelectedIndexFuture != null) {
                                    setQuestionChoiceSelectedIndexFuture.cancel(true);
                                }
                                setQuestionChoiceSelectedIndexFuture = AsyncCallback.start(
                                        quizFragment.getQuizActivity().executorService,
                                        () -> {
                                            QuizClient quizClient = quizFragment.getQuizActivity().getQuizClient();
                                            return new AsyncCallback.Response.Success<>(quizClient.setQuestionChoicesSelectedIndex(quizFragment.quizStateData, finalI));
                                        },
                                        AsyncCallback.Response.Error::new,
                                        (response) -> {
                                            if (!(response instanceof AsyncCallback.Response.Success)) {
                                                ((AsyncCallback.Response.Error<?>) response).exception.printStackTrace();
                                                quizFragment.getQuizActivity().onBackPressed();  // todo: error handling
                                            }
                                        }
                                );
                            }
                    );
                }
            }
        } else {
            new Exception("setQuestionChoices error. ").printStackTrace();
            quizFragment.getQuizActivity().onBackPressed();  // todo: error handling
        }
    }

    protected void setupButton(View questionChoice, Integer index, String text, Drawable background, boolean selected) {
        if (text != null) {
            TextView questionChoiceTextView = questionChoice.findViewById(R.id.questionChoiceTextView);
            questionChoiceTextView.setText(String.format(Locale.ENGLISH, "%s) %s", getCharForNumber(index + 1), text));
        }
        if (background != null) {
            questionChoice.setBackground(background);
        }
        View questionChoiceButton = questionChoice.findViewById(R.id.questionChoiceButton);
        if (selected) {
            questionChoiceButton.setAlpha(((float) quizFragment.getQuizActivity().getResources().getInteger(R.integer.questionChoiceSelectedAlphaPercent)) / 100);
        } else {
            questionChoiceButton.setAlpha(1.0f);
        }
    }

    private String getCharForNumber(int i) {
        return i > 0 && i < 27 ? String.valueOf((char) (i + 64)) : null;
    }
}
