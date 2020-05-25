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

    private long testDuration = 40*60000 + 0*1000;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        /*
            Βάζω στο answer την τιμή -1 έτσι ώστε αν τελειώσει ο χρόνος, η προσπάθεια να σωθεί στην βάση
            και να μην έχει κάποιο από τα αντικείμενα testQuestions μεταβλητή με τιμή null.
         */
        for (int i=0; i<20; i++){
            testQuestions[i] = new TriesDB(1, 0, questions[i].get_id(), -1, i+1);
        }

        //Αρχικοποιώ τις διάφορες μεταβλητές.
        current = 0;
        it = 0;
        score = 0;

        /*
            Αρχικά στο ArrayList move βάζω τις τιμές 0, 1, ..., 19. Αυτές αντιστοιχούν στις 20 ερωτήσεις του τεστ.
            Το ArrayList αυτό χρησιμοποιείται για την σωστή μετακίνηση από ερώτηση σε ερώτηση όταν ο χρήστης απαντάει
            κάποια ερώτηση ή όταν πατάει το αντίστοιχο κουμπί για να μετακινηθεί στην επόμενη ή στην προηγούμενη
            ερώτηση του τεστ. Όποτε κάποια ερώτηση απαντιέται ο αντίστοιχος αριθμός στο ArrayList αφαιρείται.
            Για τον κατάλληλο χειρισμό των παραπάνω ενεργειών (απάντηση, επόμενο, προηγούμενο) χρησιμοποιούνται
            οι συναρτήσεις choice(), next(), previous().
         */
        move = new ArrayList<>();
        for (int i=0; i<20; i++){
            move.add(i);
        }

        initCountDownTimer(testDuration);
        isPaused = false;

        setTexts();
        setDialog();
    }

    /**
     * Συνάρτηση που δημιουργεί ένα AlertDialog που πρόκειται να χρησιμοποιηθεί στην περίπτωση που ο χρήστης
     * πατήσει το back button. Το AlertDialog τον ενημερώνει πως αν εξέλθει από το τεστ η προσπάθεια του
     * θα τερματιστεί. Αν αυτός επιθυμεί όντως να βγει από το τεστ τότε καλείται η συνάρτηση finish()
     * προκειμένου ο χρήστης να μην μπορεί να επιστρέψει στο activity του τεστ.
     */
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

    /**
     * Κάνουμε override την συνάρτηση onBackPressed() έτσι ώστε να εμφανίζει το AlertDialog
     * στην οθόνη του χρήστη.
     */
    @Override
    public void onBackPressed(){
        alert.show();
    }

    /**
     * Κάνουμε override την συνάρτηση onPause() έτσι ώστε όταν καλείται να σταματάει το χρονόμετρο
     * της εξέτασης. Χρησιμοποιείται δηλαδή όταν ο χρήστης πατάει το home είτε το overview button.
     */
    @Override
    protected void onPause(){
        countDownTimer.cancel();
        super.onPause();
    }

    /**
     * Κάνουμε override την συνάρτηση onRestart() έτσι ώστε όταν καλείται να συνεχίζει το χρονόμετρο
     * της εξέτασης.
     */
    @Override
    protected void onRestart(){
        initCountDownTimer(timeRemaining);
        super.onRestart();
    }

    /**
     * Συνάρτηση που δημιουργεί το χρονόμετρο και ορίζει τις λειτουργίες που θα εκτελούνται όταν
     * περνάει ένα δευτερόλεπτο ή όταν ο χρόνος του τεστ τελειώνει.
     * @param time
     */
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

    /**
     * Συνάρτηση που καλείται από τις choice1(), choice2(), choice3() και εκτελεί τις ενέργειες
     * που πρέπει να γίνουν όταν ο χρήστης επιλέγει κάποια απάντηση. Οι ενέργειες αυτές περιλαμβάνουν
     * την ανανέωση του σκορ του χρήστη αν η απάντηση του ήταν σωστή, την αποθήκευση της απάντησης του,
     * την σωστή μετακίνηση προς την επόμενη ερώτηση του τεστ και σε περίπτωση που αυτή που απαντήθηκε
     * ήταν η τελευταία ερώτηση τότε η κλήση της συνάρτησης end() προκειμένουν να ολοκληρωθεί η εξέταση..
     */
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

    /**
     * Συνάρτηση που καλείται στο πάτημα (onClick δηλαδή) του κουμπιού "ΠΡΟΗΓΟΥΜΕΝΗ".
     * Αν η ερώτηση στην οποία βρισκόμαστε είναι η μοναδική που έχει απομείνει τότε δεν κάνει
     * τίποτα και απλά επιστρέφει. Αν βρισκόμαστε στην ερώτηση που αντιστοχεί στο πρώτο στοιχείο
     * του ArrayList move τότε πηγαίνουμε στο τελευταίο του αλλίως πάμε στο προηγούμενο διαθέσιμο.
     * Τέλος, καλείται η setTexts() για να ανανεωθεί το κείμενο της ερώτησης και των απαντήσεων.
     */
    public void previous(View view){
        if (move.size()==1)
            return;

        it = it==0? move.size()-1: it-1;
        current = move.get(it);

        setTexts();
    }

    /**
     * Συνάρτηση που καλείται στο πάτημα (onClick δηλαδή) του κουμπιού "ΕΠΟΜΕΝΗ".
     * Αν η ερώτηση στην οποία βρισκόμαστε είναι η μοναδική που έχει απομείνει τότε δεν κάνει
     * τίποτα και απλά επιστρέφει. Αν βρισκόμαστε στην ερώτηση που αντιστοχεί στο τελευταίο στοιχείο
     * του ArrayList move τότε πηγαίνουμε στο πρώτο του αλλίως πάμε στο επόμενο διαθέσιμο. Τέλος,
     * καλείται η setTexts() για να ανανεωθεί το κείμενο της ερώτησης και των απαντήσεων.
     */
    public void next(View view){
        if (move.size() == 1)
            return;

        it = (it+1)%move.size();
        current = move.get(it);

        setTexts();
    }

    /**
     * Συνάρτηση που ανανεώνει το κείμενο της ερώτησης και των απαντήσεων καθώς και τον
     * αριθμό της ερώτησης του τεστ στον οποίο βρισκόμαστε.
     */
    public void setTexts(){
        counter.setText((current+1) + "/20");
        question.setText(questions[current].getQuestion());
        choice1.setText(questions[current].getChoice_1());
        choice2.setText(questions[current].getChoice_2());
        choice3.setText(questions[current].getChoice_3());
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

    /**
     * Συνάρτηση που καλείται όταν το τεστ ολοκληρώνεται. Αποθηκεύει στην βάση δεδομένων
     * τα στοιχεία του τεστ και των ερωτήσεων του, στέλνει ένα intent για να ξεκινήσει το
     * activity που θα δείξει στον χρήστη τα αποτελέσματα του και τέλος καλεί την συνάρτηση
     * finish() έτσι ώστε όταν ο χρήστης πατήσει το back button να μεταφερθεί αμέσως στο
     * αρχικό μενού της εφαρμογής.
     */
    public void end(){
        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
        dbHandler.addTest(score);
        dbHandler.addTestQuestions(testQuestions);
        int testId = dbHandler.getTestSize();

        /*
            Αυτή η γραμμή υπάρχει για την περίπτωση που ο χρήστης έχει πατήσει το back button και του
            έχει εμφανιστεί το AlertDialog αλλά ταυτόχρονα ο χρόνος του τεστ τελειώνει, οπότε το AlertDialog
            φεύγει από την οθόνη του χρήστη και του εμφανίζονται τα αποτελέσματα του τεστ.
         */
        alert.cancel();

        Intent i = new Intent(this, QuestionsList.class);
        i.putExtra("testId", testId);
        i.putExtra("code", "test_questions");
        startActivity(i);
        finish();
    }
}
