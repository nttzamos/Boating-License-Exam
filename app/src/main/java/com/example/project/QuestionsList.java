package com.example.project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;

public class QuestionsList extends AppCompatActivity implements QuestionsAdapter.OnQuestionListener {
    private ArrayList<String> questions;
    private ArrayList<Boolean> trueOrFalse;
    private int testId;
    private String code;

    private TextView title;
    private TextView message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions_list);
    }

    /**
     * Κάνουμε override την συνάρτηση onResume(). Αυτο γίνεται για να λειτουργεί σωστά η εφαρμογή
     * όταν ο χρήστης αφαιρεί μια ερώτηση από τις "Αποθηκευμένες Ερώτησεις" και πατάει το back button
     * για να επιστρέψει στο activity αυτό όπου και θα δει την λίστα με τις αποθηκευμένες του ερωτήσεις.
     * Αν δεν κάναμε το override αυτό στην περίπτωση που ο χρήστης είχε αρχικά 10 αποθηκευμένες ερωτήσεις,
     * πατούσε έπειτα μια από αυτές για να την δει, την διέγραφε και έπειτα πατούσε το back button για
     * να επιστρέψει στην λίστα που δείχνει τις αποθηκευμένες του ερωτήσεις θα έβλεπε πάλι τις ίδιες 10
     * καθώς παρά την ανανέωση που θα γινόταν στην βάση δεδομένων, το περιεχόμενο του recyclerView δεν
     * θα ανανεωνόταν.
     *
     * Φυσικά χρησιμοποιείται και στις υπόλοιποες περιπτώσεις που χρησιμοποιείται το activity μέσα στην
     * εφαρμογή. Τέλος ο κώδικας που βρίσκεται εδώ δεν βρίσκεται και στην onCreate() του activity, κάτι
     * που δεν δημιουργεί κάποιο πρόβλημα καθώς έτσι και αλλιώς κατά την δημιουργία του activity μετά την
     * onCreate() καλείται αυτόματα και η onResume().
     */
    @Override
    public void onResume() {
        questions = new ArrayList<>();
        trueOrFalse = new ArrayList<>();

        title = findViewById(R.id.title);
        message = findViewById(R.id.message);

        /*
            Η code είναι μια μεταβλητή που χρησιμοποιείται για να γνωρίζουμε αν την στιγμή που
            καλείται το activity δείχνουμε στον χρήστη την λίστα με τα αποτελέσματα των απαντήσεων του
            αμέσως μόλις αυτός έχει ολοκληρώσει το τεστ, αν έχει πάει στις "Προηγούμενες Προσπάθειες"
            προκειμένου να δει τις ερωτήσεις από ένα από τα προηγούμενα τεστ του ή αν βλέπει την λίστα
            με τις αποθηκευμένες ερωτήσεις του.
         */
        code = getIntent().getStringExtra("code");
        if (code.equals("test_questions") || code.equals("previous_attempts"))
            testId = getIntent().getIntExtra("testId", 0);
        initQuestions();
        super.onResume();
    }

    /**
     * Συνάρτηση που ανάλογα τον κωδικό που έχει περαστεί στο activity
     * δημιουργεί την αντίστοιχη λίστα από ερωτήσεις.
     */
    private void initQuestions(){
        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
        if (code.equals("test_questions") || code.equals("previous_attempts")) {
            int testScore = dbHandler.getTestScore(testId);
            if (testScore < 18) {
                message.setBackgroundResource(R.drawable.wrong_item);
                message.setTextColor(getResources().getColor(R.color.colorAccent));
            }
            else {
                message.setBackgroundResource(R.drawable.correct_item);;
            }
            if (code.equals("test_questions"))
                title.setText(getString(R.string.results));
            else title.setText(getString(R.string.menu_previous_attempts));
            message.setText(getString(R.string.your_score_was) + testScore + "/20");
            TestQuestionDB[] questionsDB;
            questionsDB = dbHandler.getPreviousTestQuestions(testId);
            for (int i = 0; i < 20; i++) {
                questions.add(questionsDB[i].getQuestion());
                trueOrFalse.add(questionsDB[i].getAnswer() == questionsDB[i].getCorrect_answer());
            }
        }
        else if (code.equals("saved_questions")) {
            int savedSize = dbHandler.getSavedSize();
            title.setText(getString(R.string.menu_saved_questions));
            message.setText(getString(R.string.number_of_saved_questions) + ": " + savedSize);
            message.setTextColor(getResources().getColor(R.color.orangish));
            if (savedSize > 0) {
                QuestionDB[] saved = new QuestionDB[savedSize];
                saved = dbHandler.getSaved();
                for (int i = 0; i < savedSize; i++) {
                    questions.add(saved[i].getQuestion());
                    trueOrFalse.add(true);
                }
            }
        }

        initRecyclerView();
    }
    private void initRecyclerView(){
        RecyclerView recyclerView= findViewById(R.id.recycler_view);
        QuestionsAdapter adapter = new QuestionsAdapter(questions, trueOrFalse, code, this, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * Συνάρτηση που όταν ο χρήστης κάνει κλικ σε μια ερώτηση, τον οδηγεί στο αντίστοιχο activity.
     */
    @Override
    public void onQuestionClick(int questionId) {
        if (code.equals("test_questions") || code.equals("previous_attempts")) {
            Intent i = new Intent(this, TestQuestion.class);
            i.putExtra("testQuestionId", questionId + 1);
            i.putExtra("testId", testId);
            i.putExtra("code", code);
            startActivity(i);
        }
        else if (code.equals("saved_questions")) {
            Intent i = new Intent(this, SavedQuestion.class);
            i.putExtra("savedQuestionPosition", questionId);
            startActivity(i);
        }

    }
}