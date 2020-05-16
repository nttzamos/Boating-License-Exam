package com.example.project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class ItemsList extends AppCompatActivity implements Adapter.OnQuestionListener {
    private ArrayList<String> questions;
    private ArrayList<Integer> savedQuestions;
    private ArrayList<Boolean> trueOrFalse;
    private int testId;
    private String code;

    private TextView message1;
    private TextView message2;
    private TextView message3;
    private LinearLayout linear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items_list);
    }

    @Override
    public void onResume() {
        System.out.println("Resumed");
        questions = new ArrayList<>();
        savedQuestions = new ArrayList<>();
        trueOrFalse = new ArrayList<>();

        message1 = findViewById(R.id.message1);
        message2 = findViewById(R.id.message2);
        message3 = findViewById(R.id.message3);
        linear = findViewById(R.id.linear);

        code = getIntent().getStringExtra("code");
        if (code.equals("test_questions") || code.equals("previous_attempts"))
            testId = getIntent().getIntExtra("testId", 0);
        initQuestions();
        super.onResume();
    }

    private void initQuestions(){
        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
        if (code.equals("test_questions") || code.equals("previous_attempts")) {
            int testScore = dbHandler.getTestScore(testId);
            if (testScore < 19) {
                linear.setBackgroundColor(getResources().getColor(R.color.reddish));
                message1.setText("You did not pass the test :(");
                message2.setText("Your score was :");
                message3.setText(testScore + "/20");
            }
            else {
                linear.setBackgroundColor(getResources().getColor(R.color.greenish));
                message1.setText("You passed the test :)");
                message2.setText("Your score was :");
                message3.setText(testScore + "/20");
            }
            TestQuestionDB[] questionsDB;
            questionsDB = dbHandler.getPreviousTestQuestions(testId);
            for (int i = 0; i < 20; i++) {
                questions.add(questionsDB[i].getQuestion());
                trueOrFalse.add(questionsDB[i].getAnswer() == questionsDB[i].getCorrect_answer() ? true : false);
            }
        }
        else if (code.equals("saved_questions")) {
            int savedSize = dbHandler.getSavedSize();
            message1.setText("These are your saved questions");
            message2.setText("Number of questions saved :");
            message3.setText("" + savedSize);
            if (savedSize > 0) {
                QuestionDB[] saved = new QuestionDB[savedSize];
                saved = dbHandler.getSaved();
                for (int i = 0; i < savedSize; i++) {
                    savedQuestions.add(saved[i].get_id());
                    questions.add(saved[i].getQuestion());
                    trueOrFalse.add(true);
                }
            }
        }
        else if (code.equals("tests_list")) {
            int testsSize = dbHandler.getTestSize();
            message1.setText("These are your previous attempts");
            message2.setText("Number of tests taken :");
            message3.setText("" + testsSize);
            int[][] tests = new int[testsSize][2];
            tests = dbHandler.getTests();;
            for (int i=0; i<testsSize; i++){
                questions.add("" + tests[i][1]);
                trueOrFalse.add(true);
            }
        }
        initRecyclerView();
    }
    private void initRecyclerView(){
        RecyclerView recyclerView= findViewById(R.id.recycler_view);
        Adapter adapter = new Adapter(questions, trueOrFalse, code,this,this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onQuestionClick(int questionId) {
        if (code.equals("test_questions") || code.equals("previous_attempts")) {
            Intent i = new Intent(this, TestQuestion.class);
            i.putExtra("testQuestionId", questionId + 1);
            i.putExtra("testId", testId);
            System.out.println("pos : " + questionId + " , test : " + testId);
            startActivity(i);
        }
        else if (code.equals("saved_questions")) {
            Intent i = new Intent(this, SavedQuestion.class);
            i.putExtra("savedQuestionId", savedQuestions.get(questionId) );
            startActivity(i);
        }
        else if (code.equals("tests_list")) {
            Intent i = new Intent(this, ItemsList.class);
            i.putExtra("testId", questionId + 1);
            i.putExtra("code", "previous_attempts");
            startActivity(i);
        }

    }
}