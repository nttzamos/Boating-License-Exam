package com.example.project;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TestQuestion extends AppCompatActivity {

    TextView question;
    TextView timer;
    TextView counter;

    TextView[] choices;

    Button previous;
    Button next;
    FloatingActionButton save;

    private int testId;
    private int current;
    private TestQuestionDB[] questions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_question);

        testId = getIntent().getIntExtra("testId", 0);
        current = getIntent().getIntExtra("testQuestionId", 0) - 1;

        question = findViewById(R.id.question);
        timer = findViewById(R.id.timer);
        counter = findViewById(R.id.counter);

        choices = new TextView[3];
        choices[0] = findViewById(R.id.choice1);
        choices[1] = findViewById(R.id.choice2);
        choices[2] = findViewById(R.id.choice3);

        previous = findViewById(R.id.previous);
        next = findViewById(R.id.next);
        save = findViewById(R.id.save);

        MyDBHandler dbHandler = new MyDBHandler(this, null , null , 1);
        questions = dbHandler.getPreviousTestQuestions(testId);

        setTexts();
    }

    public void previous(View view){
        current = current==0? current=19: current-1;
        setTexts();
    }

    public void next(View view){
        current = current==19? current=0: current+1;
        setTexts();
    }

    public void setTexts(){
        choices[0].setBackgroundResource(R.drawable.choice);
        choices[1].setBackgroundResource(R.drawable.choice);
        choices[2].setBackgroundResource(R.drawable.choice);
        counter.setText((current+1) + "/20");
        question.setText(questions[current].getQuestion());
        choices[0].setText(questions[current].getChoice_1());
        choices[1].setText(questions[current].getChoice_2());
        choices[2].setText(questions[current].getChoice_3());
        int answer = questions[current].getAnswer();
        int correctAnswer = questions[current].getCorrect_answer();
        choices[correctAnswer-1].setBackgroundResource(R.drawable.correction);
        if (answer!=correctAnswer && answer!=-1)
            choices[answer-1].setBackgroundResource(R.drawable.wrong);
        else choices[correctAnswer-1].setBackgroundResource(R.drawable.correct);
    }

    public void save(View view){
        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
        SavedDB savedDB = new SavedDB(1, questions[current].get_id());
        boolean tmp = dbHandler.addSaved(savedDB);
        String text;
        if (tmp)
            text = "Question saved successfully";
        else text = "Question has already been saved";
        Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        toast.show();
    }
}
