package com.example.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeDatabase();
    }

    public void goToTest(View view){
        Intent i = new Intent(this, Test.class);
        startActivity(i);
    }

    public void goToTestsList(View view){
        Intent i = new Intent(this, TestsList.class);
        startActivity(i);
    }

    public void goToScoresReal(View view){
        Intent i = new Intent(this, TestScores.class);
        startActivity(i);
    }

    public void goToSaved(View view){
        Intent i = new Intent(this, QuestionsList.class);
        i.putExtra("code", "saved_questions");
        startActivity(i);
    }

    private void initializeDatabase(){
        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
        //dbHandler.deleteEverything();
        dbHandler.initCurrentSavedId();
        if (dbHandler.getQuestionSize() > 0) {
            System.out.println(dbHandler.getQuestionSize());
            return;
        }

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(getAssets().open("data.txt")));
            String text;
            int chapter = 0;
            int id = 0;
            while (true){
                //System.out.println(id);
                text = reader.readLine();
                if (text.equals("END"))
                    break;
                else if (text.equals("NEW")) {
                    System.out.println(id + " " + chapter);
                    chapter++;
                    reader.readLine();
                    text = reader.readLine();
                }
                id++;
                int question_no = Integer.parseInt(text);
                //reader.readLine();
                String question = reader.readLine();
                String choice_1 = reader.readLine();
                String choice_2 = reader.readLine();
                String choice_3 = reader.readLine();
                reader.readLine();
                QuestionDB questionDB = new QuestionDB(id, chapter, question_no, question, choice_1, choice_2, choice_3, 1);
                dbHandler.addQuestion(questionDB);
            }
        } catch (IOException e){
            e.printStackTrace();
        }

        System.out.print("Done");
    }

}
