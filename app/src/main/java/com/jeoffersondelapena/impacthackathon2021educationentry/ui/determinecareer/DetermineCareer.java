package com.jeoffersondelapena.impacthackathon2021educationentry.ui.determinecareer;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.jeoffersondelapena.impacthackathon2021educationentry.R;
import com.jeoffersondelapena.impacthackathon2021educationentry.data.model.CareerChoice;
import com.jeoffersondelapena.impacthackathon2021educationentry.data.model.Question;
import com.jeoffersondelapena.impacthackathon2021educationentry.data.repository.CareerChoiceRepository;
import com.jeoffersondelapena.impacthackathon2021educationentry.data.repository.QuestionRepository;
import com.jeoffersondelapena.impacthackathon2021educationentry.util.AlertDialogManager;
import com.jeoffersondelapena.impacthackathon2021educationentry.util.NavigationManager;

public class DetermineCareer extends AppCompatActivity {
    private int currentIndex;
    private Question currentQuestion;

    private RecyclerView recyclerViewCareerChoices;
    private TextView lblQuestionContent;
    private Button btnYes;
    private Button btnNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_determine_career);

        recyclerViewCareerChoices = findViewById(R.id.recycler_view_career_choices);
        lblQuestionContent = findViewById(R.id.lbl_question_content);
        btnYes = findViewById(R.id.btn_yes);
        btnNo = findViewById(R.id.btn_no);

        btnYes.setOnClickListener(v -> onBtnYesClicked());
        btnNo.setOnClickListener(v -> onBtnNoClicked());

        for (CareerChoice careerChoice: CareerChoiceRepository.careerChoices) {
            careerChoice.score = .0;
        }
        resetCurrentIndex();
    }

    @Override
    public void onBackPressed() {
        AlertDialogManager.showAlertDialogBuilder(
                DetermineCareer.this,
                "Leave page?",
                "Your answers will not be saved."
        ).setPositiveButton(
                "Leave",
                (dialog, which) -> DetermineCareer.super.onBackPressed()
        ).setNegativeButton("No", null)
                .show();
    }

    private void onBtnYesClicked() {
        doMainOperationOnScores(true);
    }

    private void onBtnNoClicked() {
        doMainOperationOnScores(false);
    }

    private void doMainOperationOnScores(boolean isYes) {
        for (String careerChoiceString: currentQuestion.careerChoices) {
            for (CareerChoice careerChoice: CareerChoiceRepository.careerChoices) {
                if (careerChoiceString.equalsIgnoreCase(careerChoice.careerChoiceName)) {
                    if (isYes) {
                        careerChoice.score += careerChoice.getScoreChangeAmount();
                    } else {
                        careerChoice.score -= careerChoice.getScoreChangeAmount();
                    }
                }
            }
        }
        updateCurrentIndex();
    }

    private void resetCurrentIndex() {
        currentIndex = 0;
        updateCurrentObject();
    }

    private void updateCurrentIndex() {
        if (currentIndex >= (QuestionRepository.questions.size() - 1)) {
            NavigationManager.goToActivity(DetermineCareer.this, CareerResult.class, true);
        } else {
            currentIndex++;
        }
        updateCurrentObject();
    }

    private void updateCurrentObject() {
        currentQuestion = QuestionRepository.questions.get(currentIndex);
        updateUi();
    }

    private void updateUi() {
        lblQuestionContent.setText(currentQuestion.questionContent);
        recyclerViewCareerChoices.setAdapter(
                new CareerChoiceAdapter(CareerChoiceRepository.getFilteredCareerChoices())
        );
    }
}