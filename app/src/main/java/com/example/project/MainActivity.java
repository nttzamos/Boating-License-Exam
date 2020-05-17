package com.example.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class MainActivity extends AppCompatActivity {

    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    StorageReference material;

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

    public void goToTheory(View view){
        startDownloading();
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

    private void startDownloading(){
        storageReference = firebaseStorage.getInstance().getReference();
        material = storageReference.child("theory.pdf");
        material.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String url=uri.toString();
                downloadFile(MainActivity.this,"Ύλη Εξέτασης - Δίπλωμα Ταχυπλόου Σκάφους'",".pdf",DIRECTORY_DOWNLOADS, url);
            }
        });
    }

    private void downloadFile(Context context, String fileName, String fileExtension, String destinationDirectory, String url){
        DownloadManager downloadManager = (DownloadManager) getSystemService(context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(this, DIRECTORY_DOWNLOADS, fileName+fileExtension);
        downloadManager.enqueue(request);
    }
}
