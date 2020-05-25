package com.example.project;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TestQuestion extends AppCompatActivity {

    TextView title;
    TextView question;
    TextView timer;
    TextView counter;

    TextView[] choices;

    Button previous;
    Button next;
    FloatingActionButton save;

    private String code;
    private int testId;
    private int current;
    private TestQuestionDB[] questions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_question);

        title = findViewById(R.id.title);

        testId = getIntent().getIntExtra("testId", 0);
        current = getIntent().getIntExtra("testQuestionId", 0) - 1;

        /*
            Η code είναι μια μεταβλητή που χρησιμοποιείται για να γνωρίζουμε αν την στιγμή που
            καλείται το activity δείχνουμε στον χρήστη τα αποτελέσματα αμέσως μόλις αυτός έχει
            ολοκληρώσει το τεστ ή αν έχει πάει στις "Προηγούμενες Προσπάθειες" προκειμένου να δει
            κάποια ερώτηση από ένα από τα προηγούμενα τεστ του.
         */
        code = getIntent().getStringExtra("code");

        /*
            Θέτουμε ανάλογα τον κατάλληλο τίτλο.
         */
        if (code.equals("previous_attempts"))
            title.setText(getString(R.string.menu_previous_attempts));
        else if (code.equals("test_questions"))
            title.setText(getString(R.string.results));

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

    /**
     * Συνάρτηση που καλείται στο πάτημα (onClick δηλαδή) του κουμπιού "ΠΡΟΗΓΟΥΜΕΝΗ".
     * Αν βρισκόμαστε στην ερώτηση που αντιστοχεί στο πρώτο στοιχείο
     * του ArrayList move τότε πηγαίνουμε στο τελευταίο του αλλίως πάμε στο προηγούμενο διαθέσιμο.
     * Τέλος, καλείται η setTexts() για να ανανεωθεί το κείμενο της ερώτησης και των απαντήσεων.
     */
    public void previous(View view){
        current = current==0? current=19: current-1;
        setTexts();
    }

    /**
     * Συνάρτηση που καλείται στο πάτημα (onClick δηλαδή) του κουμπιού "ΕΠΟΜΕΝΗ".
     * Αν βρισκόμαστε στην ερώτηση που αντιστοχεί στο τελευταίο στοιχείο
     * του ArrayList move τότε πηγαίνουμε στο πρώτο του αλλίως πάμε στο επόμενο διαθέσιμο. Τέλος,
     * καλείται η setTexts() για να ανανεωθεί το κείμενο της ερώτησης και των απαντήσεων.
     */
    public void next(View view){
        current = current==19? current=0: current+1;
        setTexts();
    }

    /**
     * Συνάρτηση που αλλάζει κάθε φορά τόσο το κείμενο της ερώτησης και των πιθανών
     * απαντήσεων της όσο και τα χρώματα στο παρασκήνιο των σχημάτων των απαντήσεων προκειμένου
     * να φαίνεται αν η απάντηση του χρήστη στην εκάστοτε ερώτηση ήταν σωστή ή λάθος,
     * ενώ στην περίπτωση που ήταν λάθος να υποδεικνύεται η σωστή απάντηση.
     */
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
        else if (answer!=-1)
            choices[correctAnswer-1].setBackgroundResource(R.drawable.correct);
    }

    /**
     * Συνάρτηση που ελέγχει αν η ερώτηση που ο χρήστης προσπαθεί να αποθηκεύσει είναι
     * ήδη αποθηκευμένη ή όχι. Του εμφανίζει αντίστοιχο μήνυμα για να τον ενημερώσει
     * και σε περίπτωση που δεν είναι αποθηκευμένη τότε την αποθηκεύει.
     */
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
}
