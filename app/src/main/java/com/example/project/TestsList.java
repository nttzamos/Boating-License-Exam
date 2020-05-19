package com.example.project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class TestsList extends AppCompatActivity implements TestsAdapter.OnQuestionListener {
    private ArrayList<Integer> tests;

    private TextView message1;
    private TextView message2;
    private TextView message3;
    private LinearLayout linear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tests_list);
    }

    @Override
    public void onResume() {
        System.out.println("Resumed");
        tests = new ArrayList<>();

        message1 = findViewById(R.id.message1);
        message2 = findViewById(R.id.message2);
        message3 = findViewById(R.id.message3);
        linear = findViewById(R.id.linear);

        initQuestions();
        super.onResume();
    }

    private void initQuestions(){
        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
        int testsSize = dbHandler.getTestSize();
        message1.setText(getString(R.string.menu_previous_attempts));
        message2.setText(getString(R.string.number_of_tests));
        message3.setText("" + testsSize);
        int[][] tests = new int[testsSize][2];
        tests = dbHandler.getTests();;
        for (int i=0; i<testsSize; i++)
            this.tests.add(tests[i][1]);

        initRecyclerView();
    }
    private void initRecyclerView(){
        RecyclerView recyclerView= findViewById(R.id.recycler_view);
        TestsAdapter adapter = new TestsAdapter(tests, this, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onQuestionClick(int questionId) {
        Intent i = new Intent(this, QuestionsList.class);
        i.putExtra("testId", questionId + 1);
        i.putExtra("code", "previous_attempts");
        startActivity(i);
    }
}