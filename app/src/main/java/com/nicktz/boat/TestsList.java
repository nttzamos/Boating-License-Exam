package com.nicktz.boat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;

public class TestsList extends AppCompatActivity implements TestsAdapter.OnQuestionListener {
    private ArrayList<Integer> tests;

    private TextView message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tests_list);
        tests = new ArrayList<>();
        message = findViewById(R.id.message);
        initTests();
    }

    /**
     * Συνάρτηση που δημιουργεί τον πίνακα των τεστ.
     */
    private void initTests(){
        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
        int testsSize = dbHandler.getTestSize();
        message.setText(getString(R.string.number_of_tests) + ": " + testsSize);
        if (testsSize == 0)
            return;
        int[][] tests = new int[testsSize][2];
        tests = dbHandler.getTests();;
        for (int i=0; i<testsSize; i++)
            this.tests.add(tests[i][1]);

        initRecyclerView();
    }

    /**
     * Συνάρτηση που δημιουργεί το RecyclerView.
     */
    private void initRecyclerView(){
        RecyclerView recyclerView= findViewById(R.id.recycler_view);
        TestsAdapter adapter = new TestsAdapter(tests, this, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * Συνάρτηση που όταν ο χρήστης κάνει κλικ σε ένα τεστ, τον οδηγεί
     * στο activity με τις ερωτήσεις του συγκεκριμένου τεστ.
     */
    @Override
    public void onQuestionClick(int questionId) {
        Intent i = new Intent(this, QuestionsList.class);
        i.putExtra("testId", questionId + 1);
        i.putExtra("code", "previous_attempts");
        startActivity(i);
    }
}