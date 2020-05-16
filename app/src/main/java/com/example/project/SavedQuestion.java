package com.example.project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class SavedQuestion extends AppCompatActivity {

    private int savedQuestionId;

    TextView question;
    TextView timer;
    TextView counter;

    TextView choice1;
    TextView choice2;
    TextView choice3;
    FloatingActionButton delete;
    boolean deleted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_question);

        savedQuestionId = getIntent().getIntExtra("savedQuestionId", 0);
        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
        QuestionDB savedQuestion = dbHandler.getSavedById(savedQuestionId);

        question = findViewById(R.id.question);

        choice1 = findViewById(R.id.choice1);
        choice2 = findViewById(R.id.choice2);
        choice3 = findViewById(R.id.choice3);

        question.setText(savedQuestion.getQuestion());
        choice1.setText(savedQuestion.getChoice_1());
        choice2.setText(savedQuestion.getChoice_2());
        choice3.setText(savedQuestion.getChoice_3());
        choice1.setBackgroundResource(R.drawable.correction);

        delete = findViewById(R.id.delete);
        deleted = false;
    }

    public void delete(View view) {
//        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
//        dbHandler.deleteSaved(savedQuestionId);
//        finish();
        if (!deleted) {
            delete.setImageResource(R.drawable.custom_save);
            deleted = true;
        }
        else {
            delete.setImageResource(R.drawable.custom_delete);
            deleted = false;
        }
    }

    @Override
    public void onBackPressed() {
        if (deleted) {
            MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
            dbHandler.deleteSaved(savedQuestionId);
        }
        super.onBackPressed();
    }
}
