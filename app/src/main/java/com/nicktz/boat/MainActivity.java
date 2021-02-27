package com.nicktz.boat;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Locale;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class MainActivity extends AppCompatActivity {

    AlertDialog.Builder builder;
    AlertDialog alert;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
        dbHandler.initDatabase(this, getFileName());
    }

    private String getFileName() {
        Locale locale = getResources().getConfiguration().locale;
        Locale greek = new Locale("el", "GR");

        if (locale.equals(greek)) {
            return "data-gr.txt";
        }
        else {
            return "data-en.txt";
        }


//        Locale locale = new Locale(languageCode);
//        Locale.setDefault(locale);
//        Resources resources = activity.getResources();
//        Configuration config = resources.getConfiguration();
//        config.setLocale(locale);
//        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    public void goToTest(View view){
        Intent i = new Intent(this, Test.class);
        startActivity(i);
    }

    /**
     * Όταν ο χρήστης πατήσει στην επιλογή "Κατέβασε την ύλη" στο αρχικό μενού τότε το εμφανίζεται
     * ένα AlertDialog που τον ρωτάει αν είναι σίγουρος ότι θέλει να κατεβάσει την ύλη.
     * Αν ο χρήστης επιλέξει ναι τότε καλεί την συνάρτηση startDownloading().
     */
    public void goToTheory(View view){
        builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        builder.setTitle(getString(R.string.download_title));
        builder.setMessage(getString(R.string.download_message));
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startDownloading();
                dialog.cancel();

            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alert = builder.create();
        alert.show();
    }

    public void goToTestsList(View view){
        Intent i = new Intent(this, TestsList.class);
        startActivity(i);
    }

    public void goToTestScores(View view){
        Intent i = new Intent(this, TestScores.class);
        startActivity(i);
    }

    public void goToSavedQuestions(View view){
        Intent i = new Intent(this, QuestionsList.class);
        i.putExtra("code", "saved_questions");
        startActivity(i);
    }

    /**
     * Παίρνει το URI για το αρχείο "theory.pdf" από την Firebase που έχουμε φτιάξει και
     * ορίζει πως όταν γίνει η λήψη του αρχείου αυτό θα αποθηκευτεί με το όνομα
     * "Ύλη Εξέτασης - Δίπλωμα Ταχυπλόου Σκάφους.pdf". Τέλος καλεί την συνάρτηση downloadFile()
     * για να πραγματοποιήσει την λήψη του αρχείου.
     */
    private void startDownloading(){
        Locale locale = getResources().getConfiguration().locale;
        Locale greek = new Locale("el", "GR");

        String firebaseFile;
        final String downloadedFileName;
        if (locale.equals(greek)) {
            firebaseFile =  "theory-gr.pdf";
            downloadedFileName = "Ύλη Εξέτασης - Δίπλωμα Ταχυπλόου Σκάφους";
        }
        else {
            firebaseFile =  "theory-en.pdf";
            downloadedFileName = "Examination Material - Boating License Exam";
        }

        StorageReference storageReference;
        storageReference = FirebaseStorage.getInstance().getReference();
        storageReference.child(firebaseFile).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String url=uri.toString();
                downloadFile(MainActivity.this, downloadedFileName,".pdf",DIRECTORY_DOWNLOADS, url);
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
