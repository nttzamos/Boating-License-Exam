package com.example.project;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class SavedQuestion extends AppCompatActivity {

    private ArrayList<Integer> move;
    private int it;


    TextView question;
    TextView choice1;
    TextView choice2;
    TextView choice3;
    FloatingActionButton delete;
    boolean deleted;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_question);

        move = new ArrayList<>();

        it = getIntent().getIntExtra("savedQuestionPosition", 0);
        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);

        int savedSize = dbHandler.getSavedSize();
        QuestionDB[] saved = new QuestionDB[savedSize];
        saved = dbHandler.getSaved();
        for (int i = 0; i < savedSize; i++) {
            move.add(saved[i].get_id());
        }


        question = findViewById(R.id.question);

        choice1 = findViewById(R.id.choice1);
        choice2 = findViewById(R.id.choice2);
        choice3 = findViewById(R.id.choice3);

        delete = findViewById(R.id.delete);

        setTexts();
    }

    public void changeState(View view) {
        if (!deleted) {
            delete.setImageResource(R.drawable.custom_save);
            deleted = true;
        }
        else {
            delete.setImageResource(R.drawable.custom_delete);
            deleted = false;
        }
    }

    public void check() {
        if (!deleted)
            return;

        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
        dbHandler.deleteSaved(move.get(it));

        move.remove(it);

        Toast toast = Toast.makeText(this, getString(R.string.question_deleted), Toast.LENGTH_SHORT);
        toast.show();
    }

    public void next(View view) {
        check();
        if (move.size()==0) {
            super.onBackPressed();
            return;
        }
        if (move.size() == 1 && !deleted)
            return;

        if (!deleted)
            it = (it+1)%move.size();
        else it = it%move.size();
        setTexts();
    }

    public void previous(View view) {
        check();
        if (move.size()==0) {
            super.onBackPressed();
            return;
        }
        if (move.size()==1 && !deleted)
            return;

        it = it==0? move.size()-1: it-1;
        setTexts();
    }

    public void setTexts() {
        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
        QuestionDB savedQuestion = dbHandler.getSavedById(move.get(it));
        question.setText(savedQuestion.getQuestion());
        choice1.setText(savedQuestion.getChoice_1());
        choice2.setText(savedQuestion.getChoice_2());
        choice3.setText(savedQuestion.getChoice_3());
        if (savedQuestion.getCorrect_answer()==1)
            choice1.setBackgroundResource(R.drawable.correction);
        if (savedQuestion.getCorrect_answer()==2)
            choice2.setBackgroundResource(R.drawable.correction);
        if (savedQuestion.getCorrect_answer()==3)
            choice3.setBackgroundResource(R.drawable.correction);

        delete.setImageResource(R.drawable.custom_delete);
        deleted = false;
    }

    @Override
    public void onBackPressed() {
        if (deleted) {
            MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
            dbHandler.deleteSaved(move.get(it));
            Toast toast = Toast.makeText(this, getString(R.string.question_deleted), Toast.LENGTH_SHORT);
            toast.show();
        }
        super.onBackPressed();
    }
}
