package com.example.project;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class Test extends AppCompatActivity {


    private int current;
    private int it;
    private int score;
    private ArrayList<Integer> move;

    private long testDuration = 0*60000 + 25*1000;
    private long timeRemaining;

    CountDownTimer countDownTimer;
    boolean isPaused;
    AlertDialog.Builder builder;
    AlertDialog alert;

    QuestionDB[] questions;
    TriesDB[] testQuestions;

    TextView question;
    TextView timer;
    TextView counter;

    Button choice1;
    Button choice2;
    Button choice3;

    Button previous;
    Button next;
    FloatingActionButton save;
    //ImageButton save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (question != null)
            System.out.println(question.getText());

        setContentView(R.layout.activity_test);

        question = findViewById(R.id.question);
        timer = findViewById(R.id.timer);
        counter = findViewById(R.id.counter);

        choice1 = findViewById(R.id.choice1);
        choice2 = findViewById(R.id.choice2);
        choice3 = findViewById(R.id.choice3);

        previous = findViewById(R.id.previous);
        next = findViewById(R.id.next);
        save = findViewById(R.id.save);

        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
        questions = new QuestionDB[20];
        questions = dbHandler.getTestQuestions();
        testQuestions = new TriesDB[20];
        for (int i=0; i<20; i++){
            /*
            βάζω το answer -1 έτσι ώστε αν τελειώσει ο χρόνος, η προσπάθεια να σωθεί στην βάση
            και να μην είναι τα testquestions null. Πρέπει στις προηγούμενες προσπάθειες να το λάβω υπόψην.
             */
            testQuestions[i] = new TriesDB(1, 0, questions[i].get_id(), -1, i+1);
        }
        current = 0;
        it = 0;
        score = 0;


        move = new ArrayList<>();
        for (int i=0; i<20; i++){
            move.add(i);
        }

       initCountDownTimer(testDuration);
        isPaused = false;

        setTexts();
        setDialog();
    }

    private void setDialog() {
        builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        builder.setTitle("Προσοχή");

        builder.setMessage("Είστε σίγουροι οτι θέλετε να εξέλθετε; Το τεστ θα τερματιστεί.");
        builder.setPositiveButton("Ναι", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();
                countDownTimer.cancel();
            }
        });
        builder.setNegativeButton("Ακυρο",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                isPaused = false;
            }
        });
        alert = builder.create();
    }

    @Override
    public void onBackPressed(){
        alert.show();
    }

    @Override
    protected void onRestart(){
        initCountDownTimer(timeRemaining);
        super.onRestart();
    }

    @Override
    protected void onPause(){
        countDownTimer.cancel();
        super.onPause();
    }


    public void initCountDownTimer(long time){
        countDownTimer = new CountDownTimer(time, 1000) {
            public void onTick(long millisUntilFinished) {
                String addZeroMin = millisUntilFinished < 600000 ? "0":"";
                String addZeroSec = millisUntilFinished % 60000 < 10000 ? "0":"";
                timer.setText(addZeroMin + millisUntilFinished / 60000 + ":" + addZeroSec + (millisUntilFinished % 60000)/1000);
                timeRemaining = millisUntilFinished;
            }

            public void  onFinish() {
                timer.setText(getString(R.string.ending_time));
                end();
            }
        }.start();
    }

    public void choice1(View view){
        choice(1);
    }

    public void choice2(View view){
        choice(2);
    }

    public void choice3(View view){
        choice(3);
    }

    public void choice(int choice){
        if (choice == questions[current].getCorrect_answer())
            score++;

        testQuestions[current].setAnswer(choice);


        if (move.size()==1){
            move.remove(0);
            countDownTimer.cancel();
            end();
            return;
        }

        move.remove(it);
        it = it%move.size();
        current = move.get(it);

        setTexts();
    }

    public void previous(View view){
        if (move.size()==1)
            return;

        it = it==0? move.size()-1: it-1;


        current = move.get(it);

        setTexts();
    }

    public void next(View view){
        if (move.size() == 1)
            return;

        it = (it+1)%move.size();
        current = move.get(it);

        setTexts();
    }

    public void setTexts(){
        counter.setText((current+1) + "/20");
        question.setText(questions[current].getQuestion());
        choice1.setText(questions[current].getChoice_1());
        choice2.setText(questions[current].getChoice_2());
        choice3.setText(questions[current].getChoice_3());
    }

    public void save(View view){
        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
        SavedDB savedDB = new SavedDB(1, questions[current].get_id());
        boolean tmp = dbHandler.addSaved(savedDB);
        String text;
        if (tmp)
            text = getString(R.string.question_saved_successfully);
        else text = getString(R.string.question_already_saved);
        Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void end(){
        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
        dbHandler.addTest(score);
        dbHandler.addTestQuestions(testQuestions);
        int testId = dbHandler.getTestSize();
        alert.cancel();
        Intent i = new Intent(this, QuestionsList.class);
        i.putExtra("testId", testId);
        i.putExtra("code", "test_questions");
        startActivity(i);
        finish();
    }
}
