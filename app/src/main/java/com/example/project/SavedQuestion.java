package com.example.project;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class SavedQuestion extends AppCompatActivity {

    private ArrayList<Integer> move;
    private int it;

    TextView question;
    TextView choice1;
    TextView choice2;
    TextView choice3;
    FloatingActionButton delete;

    /*
        Μεταβλητή που χρησιμοποιούμε προκειμένου να γνωρίζουμε αν ο χρήστης έχει αφήσει το
        κουμπί διαγραφής σε κατάσταση που θα οδηγήσει στην διαγραφή της ερώτησης ή όχι.
     */
    boolean toDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_question);

        move = new ArrayList<>();

        it = getIntent().getIntExtra("savedQuestionPosition", 0);
        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);

        int savedSize = dbHandler.getSavedSize();
        QuestionDB[] saved = new QuestionDB[savedSize];
        saved = dbHandler.getSaved();
        for (int i = 0; i < savedSize; i++) {
            move.add(saved[i].get_id());
        }

        question = findViewById(R.id.question);
        choice1 = findViewById(R.id.choice1);
        choice2 = findViewById(R.id.choice2);
        choice3 = findViewById(R.id.choice3);
        delete = findViewById(R.id.delete);

        setTexts();
    }

    /*
        Κάθε φορά που ο χρήστης πατάει το κουμπί διαγραφής αλλάζουμε το σχέδιο που εμφανίζεται
        στην οθόνη καθώς και την τιμή της μεταβλητής deleted
     */
    public void changeState(View view) {
        if (!toDelete) {
            delete.setImageResource(R.drawable.custom_save);
            toDelete = true;
        }
        else {
            delete.setImageResource(R.drawable.custom_delete);
            toDelete = false;
        }
    }

    /**
     * Συναρτηση που καλείται για να ελέγξει αν ο χρήστης επιθυμεί να διαγράψει μια ερώτηση. Αν ναι
     * τότε την διαγράφει, την αφαιρεί από το ArrayList move και τέλος εμφανίζει κατάλληλο μήνυμα στον
     * χρήστη για να τον ενημερώσει.
     */
    public void check() {
        if (!toDelete)
            return;

        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
        dbHandler.deleteSaved(move.get(it));

        move.remove(it);

        Toast toast = Toast.makeText(this, getString(R.string.question_deleted), Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * Συνάρτηση που καλείται στο πάτημα (onClick δηλαδή) του κουμπιού "ΠΡΟΗΓΟΥΜΕΝΗ". Καλεί αρχικά την
     * check() για να ελέγξει αν η ερώτηση που βρισκόμαστε πρέπει να διαγραφεί προτού πάμε στην επόμενη.
     * Αν η ερώτηση κατά την εκτέλεση της check() διαγράφτηκε και δεν υπάρχει άλλη αποθηκευμένη ερώτηση
     * προς την οποία μπορούμε να μετακινηθούμε καλεί την super.onBackPressed() για να πάμε πίσω
     * στην λίστα με τις αποθηκευμένες ερωτήσεις (η οποία προφανώς θα είναι κενή).
     * Αν η ερώτηση στην οποία βρισκόμαστε είναι η μοναδική που έχει απομείνει τότε δεν κάνει
     * τίποτα και απλά επιστρέφει. Αν βρισκόμαστε στην ερώτηση που αντιστοχεί στο πρώτο στοιχείο
     * του ArrayList move τότε πηγαίνουμε στο τελευταίο του αλλίως πάμε στο προηγούμενο διαθέσιμο. Τέλος,
     * καλείται η setTexts() για να ανανεωθεί το κείμενο της ερώτησης και των απαντήσεων.
     */
    public void previous(View view) {
        check();
        if (move.size()==0) {
            super.onBackPressed();
            return;
        }
        if (move.size()==1 && !toDelete)
            return;

        it = it==0? move.size()-1: it-1;
        setTexts();
    }

    /**
     * Συνάρτηση που καλείται στο πάτημα (onClick δηλαδή) του κουμπιού "ΕΠΟΜΕΝΗ". Καλεί αρχικά την
     * check() για να ελέγξει αν η ερώτηση που βρισκόμαστε πρέπει να διαγραφεί προτού πάμε στην επόμενη.
     * Αν η ερώτηση κατά την εκτέλεση της check() διαγράφτηκε και δεν υπάρχει άλλη αποθηκευμένη ερώτηση
     * προς την οποία μπορούμε να μετακινηθούμε καλεί την super.onBackPressed() για να πάμε πίσω
     * στην λίστα με τις αποθηκευμένες ερωτήσεις (η οποία προφανώς θα είναι κενή).
     * Αν η ερώτηση στην οποία βρισκόμαστε είναι η μοναδική που έχει απομείνει τότε δεν κάνει
     * τίποτα και απλά επιστρέφει. Αν βρισκόμαστε στην ερώτηση που αντιστοχεί στο τελευταίο στοιχείο
     * του ArrayList move τότε πηγαίνουμε στο πρώτο του αλλίως πάμε στο επόμενο διαθέσιμο. Τέλος,
     * καλείται η setTexts() για να ανανεωθεί το κείμενο της ερώτησης και των απαντήσεων.
     */
    public void next(View view) {
        check();
        if (move.size()==0) {
            super.onBackPressed();
            return;
        }
        if (move.size() == 1 && !toDelete)
            return;

        if (!toDelete)
            it = (it+1)%move.size();
        else it = it%move.size();
        setTexts();
    }

    /**
     * Συνάρτηση που καλείται για να ανανεωθεί το κείμενο της ερώτησης και των απαντήσεων. Επίσης
     * ανάλογα ποια από τις απαντήσεις είναι σωστή αλλάζει το περίγραμμα της αντίστοιχης απάντησης
     * προκειμένου να υποδεικνύεται στον χρήστη ποια είναι η σωστή απάντηση.
     * Τέλος αρχικοποιεί εκ νεου το σχέδιο του κουμπιού διαγραφής και την τιμή της μεταβλητής toDelete.
     */
    public void setTexts() {
        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
        QuestionDB savedQuestion = dbHandler.getSavedById(move.get(it));
        question.setText(savedQuestion.getQuestion());
        choice1.setText(savedQuestion.getChoice_1());
        choice2.setText(savedQuestion.getChoice_2());
        choice3.setText(savedQuestion.getChoice_3());
        if (savedQuestion.getCorrect_answer()==1)
            choice1.setBackgroundResource(R.drawable.correction);
        if (savedQuestion.getCorrect_answer()==2)
            choice2.setBackgroundResource(R.drawable.correction);
        if (savedQuestion.getCorrect_answer()==3)
            choice3.setBackgroundResource(R.drawable.correction);

        delete.setImageResource(R.drawable.custom_delete);
        toDelete = false;
    }

    /**
     * Κάνουμε override την συνάρτηση onBackPressed() έτσι ώστε σε περίπτωση που ο χρήστης πατήσει
     * το back button και έχει πατήσει και το κουμπί διαγραφής, να διαγραφεί η ερώτηση από τις
     * "Αποθηκευμένες Ερωτήσεις". Αμέσως μετά την διαγραφή της ερώτησης εμφανίζεται στον χρήστη
     * και ένα μήνυμα που τον ενημερώνει για την επιτυχημένη αφαίρεση της ερώτησης από τις
     * "Αποθηκευμένες Ερωτήσεις".
     */
    @Override
    public void onBackPressed() {
        if (toDelete) {
            MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
            dbHandler.deleteSaved(move.get(it));
            Toast toast = Toast.makeText(this, getString(R.string.question_deleted), Toast.LENGTH_SHORT);
            toast.show();
        }
        super.onBackPressed();
    }
}
