package com.nicktz.boat;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.content.ContentValues;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;

public class MyDBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "database.db";

    //Table Names
    private static final String TABLE_TESTS = "tests";
    private static final String TABLE_QUESTIONS = "questions";
    private static final String TABLE_TEST_QUESTIONS = "test_questions";
    private static final String TABLE_SAVED_QUESTIONS = "saved_questions";

    //Common column names
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_QUESTION_ID = "question_id";

    //tests table column names
    private static final String COLUMN_SCORE = "score";

    //questions table column names
    private static final String COLUMN_CHAPTER = "chapter";
    private static final String COLUMN_QUESTION_NO = "question_no";
    private static final String COLUMN_QUESTION = "question";
    private static final String COLUMN_CHOICE_1 = "choice_1";
    private static final String COLUMN_CHOICE_2 = "choice_2";
    private static final String COLUMN_CHOICE_3 = "choice_3";
    private static final String COLUMN_CORRECT_ANSWER = "correct_answer";

    //test_questions table column names
    private static final String COLUMN_TEST_ID = "test_id";
    private static final String COLUMN_ANSWER = "answer";
    private static final String COLUMN_INCREASING = "increasing";

    //tests table create statement
    private static final String CREATE_TABLE_TESTS = "CREATE TABLE "
            + TABLE_TESTS + "(" + COLUMN_ID + " INTEGER PRIMARY KEY,"
            + COLUMN_SCORE + " INTEGER" + ")";

    //questions table create statement
    private static final String CREATE_TABLE_QUESTIONS = "CREATE TABLE "
            + TABLE_QUESTIONS + "(" + COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_CHAPTER
            + " INTEGER," + COLUMN_QUESTION_NO + " INTEGER," + COLUMN_QUESTION + " TEXT,"
            + COLUMN_CHOICE_1 + " TEXT," + COLUMN_CHOICE_2 + " TEXT," + COLUMN_CHOICE_3
            + " TEXT," + COLUMN_CORRECT_ANSWER + " INTEGER" + ")";


    //test_questions table create statement
    private static final String CREATE_TABLE_TEST_QUESTIONS = "CREATE TABLE "
            + TABLE_TEST_QUESTIONS + "(" + COLUMN_ID + " INTEGER PRIMARY KEY,"
            + COLUMN_TEST_ID + " INTEGER," + COLUMN_QUESTION_ID
            + " INTEGER," + COLUMN_ANSWER + " INTEGER," + COLUMN_INCREASING + " INTEGER" + ")";

    //saved_questions table create statement
    private static final String CREATE_TABLE_SAVED_QUESTIONS = "CREATE TABLE "
            + TABLE_SAVED_QUESTIONS + "(" + COLUMN_ID + " INTEGER PRIMARY KEY,"
            + COLUMN_QUESTION_ID + " INTEGER" + ")";

    private static int currentSavedId;

    public MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_TABLE_TESTS);
        db.execSQL(CREATE_TABLE_QUESTIONS);
        db.execSQL(CREATE_TABLE_TEST_QUESTIONS);
        db.execSQL(CREATE_TABLE_SAVED_QUESTIONS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_TESTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEST_QUESTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SAVED_QUESTIONS);
        onCreate(db);
    }

    /**
     * Συνάρτηση που ελέγχει αν η βάση δεδομένων έχει δημιουργηθεί και αν δεν έχει
     * δημιουργηθεί τότε την δημιουργεί. Καλείται κάθε φορά που ανοίγει η εφαρμογή.
     * Όπως είναι λογικό η βάση δεδομένων δημιουργείται μόνο την πρώτη φορά που ο χρήστης ανοίγει
     * την εφαρμογή.
     */
    public void initDatabase(Context context, String file){
        initCurrentSavedId();
        if (getQuestionSize() > 0)
            return;

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(context.getAssets().open(file)));
            String text;
            int chapter = 0;
            int id = 0;
            while (true){
                text = reader.readLine();
                if (text.equals("END"))
                    break;
                else if (text.equals("NEW")) {
                    chapter++;
                    reader.readLine();
                    text = reader.readLine();
                }
                id++;
                int question_no = Integer.parseInt(text);
                String question = reader.readLine();
                String choice_1 = reader.readLine();
                String choice_2 = reader.readLine();
                String choice_3 = reader.readLine();
                int correctAnswer = Integer.parseInt(reader.readLine());
                reader.readLine();
                QuestionDB questionDB = new QuestionDB(id, chapter, question_no, question, choice_1, choice_2, choice_3, correctAnswer);
                addQuestion(questionDB);
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Συνάρτηση που προσθέτει μια ερώτηση στον αντίστοιχο πίνακα της βάσης δεδομένων.
     */
    public void addQuestion(QuestionDB questionDB){
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, questionDB.get_id());
        values.put(COLUMN_CHAPTER, questionDB.getChapter());
        values.put(COLUMN_QUESTION_NO, questionDB.getQuestion_no());
        values.put(COLUMN_QUESTION, questionDB.getQuestion());
        values.put(COLUMN_CHOICE_1,questionDB.getChoice_1());
        values.put(COLUMN_CHOICE_2,questionDB.getChoice_2());
        values.put(COLUMN_CHOICE_3,questionDB.getChoice_3());
        values.put(COLUMN_CORRECT_ANSWER, questionDB.getCorrect_answer());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_QUESTIONS, null, values);
        db.close();
    }

    /**
     * Συνάρτηση που επιστρέφει το πλήθος των ερωτήσεων που βρίσκονται στον πίνακα
     * που περιέχει τις ερωτήσεις της ύλης. Χρησιμοποιείται για να γίνει έλεγχος του κατά πόσο
     * η βάση δεδομένων έχει δημιουργηθεί ήδη.
     */
    public int getQuestionSize(){
        String query = "SELECT * FROM " + TABLE_QUESTIONS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        int size = cursor.getCount();
        db.close();
        return size;
    }

    /**
     * Συνάρτηση που κάνοντας χρήση της βοηθητικής κλάσης QuestionDB επιστρέφει 20 τυχαία
     * επιλεγμένα ερωτήσεις από το ευρύτερο pool ερωτήσεων.
     */
    public QuestionDB[] getTestQuestions(){
        QuestionDB[] questions = new QuestionDB[20];
        Integer[] arr = new Integer[150];
        for (int i=0; i<150; i++)
            arr[i] = i + 1;
        Collections.shuffle(Arrays.asList(arr));

        int tmp1, tmp2, tmp3, tmp8;
        String tmp4="", tmp5="", tmp6="", tmp7="";
        SQLiteDatabase db = this.getReadableDatabase();
        for (int i=0; i<20; i++){
            String query = "SELECT * FROM " + TABLE_QUESTIONS + " WHERE " +
                    COLUMN_ID + " = " + arr[i];
            Cursor cursor = db.rawQuery(query, null);
            cursor.moveToFirst();
            tmp1 = Integer.parseInt(cursor.getString(0));
            tmp2 = Integer.parseInt(cursor.getString(1));
            tmp3 = Integer.parseInt(cursor.getString(2));
            tmp8 = Integer.parseInt(cursor.getString(7));
            tmp4 = cursor.getString(3);
            tmp5 = cursor.getString(4);
            tmp6 = cursor.getString(5);
            tmp7 = cursor.getString(6);
            questions[i] = new QuestionDB(tmp1, tmp2, tmp3, tmp4, tmp5, tmp6, tmp7, tmp8);
        }
        db.close();
        return questions;
    }

    /**
     * Συνάρτηση που αποθηκεύει τα στοιχεία ενός τεστ στον αντίστοιχο πίνακα της βάσης δεδομένων.
     */
    public void addTest(int score){
        ContentValues values = new ContentValues();
        int testId = getTestSize();
        values.put(COLUMN_ID, testId+1);
        values.put(COLUMN_SCORE, score);
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_TESTS, null, values);
        db.close();
    }

    /**
     * Συνάρτηση που επιστρέφει το πλήθος των αποθηκευμένων τεστ στην βάση δεδομένων.
     */
    public int getTestSize(){
        String query = "SELECT * FROM " + TABLE_TESTS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        int size = cursor.getCount();
        db.close();
        return size;
    }

    /**
     * Συνάρτηση που επιστρέφει τα στοιχεία όλων των τεστ που έχει ολοκληρώσει ο χρήστης.
     */
    public int[][] getTests(){
        String query = "SELECT * FROM " + TABLE_TESTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        int[][] tests = new int[cursor.getCount()][2];
        int i=0;
        cursor.moveToFirst();
        tests[i][0] = Integer.parseInt(cursor.getString(0));
        tests[i++][1] = Integer.parseInt(cursor.getString(1));
        while (cursor.moveToNext()){
            tests[i][0] = Integer.parseInt(cursor.getString(0));
            tests[i++][1] = Integer.parseInt(cursor.getString(1));
        }
        db.close();
        return tests;
    }

    /**
     * Συνάρτηση που επιστρέφει το σκορ ενός τεστ.
     */
    public int getTestScore(int testId){
        String query = "SELECT " + TABLE_TESTS + "." + COLUMN_SCORE +  " FROM " + TABLE_TESTS + " WHERE " +
                TABLE_TESTS + "." + COLUMN_ID + " = " + testId;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        db.close();
        return Integer.parseInt(cursor.getString(0));
    }


    /**
     * Συνάρτηση που δέχεται μια ερώτηση και αν αυτή δεν έχει ήδη προστεθεί στις
     * αποθηκευμένες ερωτήσεις, την προσθέτει και επιστρέφει true. Αν έχει ήδη προστεθεί τότε
     * επιστρέφει false.
     */
    public boolean addSaved(SavedDB savedDB){
        /*
            Ελέγχουμε αν η ερώτηση έχει ήδη αποθηκευτεί στον πίνακα με τις αποθηκευμένες ερωτήσεις.
         */
        String query = "SELECT * FROM " + TABLE_SAVED_QUESTIONS + " WHERE " + COLUMN_QUESTION_ID + " = " + savedDB.getQuestionId();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst())
            return false;

        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, currentSavedId++);
        values.put(COLUMN_QUESTION_ID, savedDB.getQuestionId());
        db.insert(TABLE_SAVED_QUESTIONS, null, values);
        db.close();
        return true;
    }

    /**
     * Συνάρτηση που με χρήση της βοηθητικής κλάσης QuestionDB επισρέφει έναν πίνακα
     * με τα στοιχεία όλων των αποθηκευμένων ερωτήσεων της βάσης δεδομένων.
     */
    public QuestionDB[] getSaved(){
        String query = "SELECT " + TABLE_QUESTIONS + ".* FROM " + TABLE_SAVED_QUESTIONS + " INNER JOIN " +
                TABLE_QUESTIONS + " ON " + TABLE_SAVED_QUESTIONS + "." + COLUMN_QUESTION_ID + " = "
                + TABLE_QUESTIONS + "." + COLUMN_ID;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        QuestionDB[] questions = new QuestionDB[cursor.getCount()];
        int tmp1, tmp2, tmp3, tmp8;
        String tmp4="", tmp5="", tmp6="", tmp7="";
        int i=0;
        cursor.moveToFirst();
        do {
            tmp1 = Integer.parseInt(cursor.getString(0));
            tmp2 = Integer.parseInt(cursor.getString(1));
            tmp3 = Integer.parseInt(cursor.getString(2));
            tmp8 = Integer.parseInt(cursor.getString(7));
            tmp4 = cursor.getString(3);
            tmp5 = cursor.getString(4);
            tmp6 = cursor.getString(5);
            tmp7 = cursor.getString(6);
            questions[i++] = new QuestionDB(tmp1, tmp2, tmp3, tmp4, tmp5, tmp6, tmp7, tmp8 );
        } while (cursor.moveToNext());
        db.close();
        return questions;
    }

    /**
     * Η συνάρτηση αυτή καλείται προκειμένου να αρχικοποιηθεί το currentSavedId. Στην μεταβλητή αυτή
     * αποθηκεύουμε το μέγιστο Id που υπάρχει στον πίνακα saved_questions συν ένα προκειμένου το
     * Id για όλες τις αποθηκευμένες ερωτήσεις του πίνακα να είναι unique. Ουσιαστικά το currentSavedId
     * είναι η τιμή που θα πάρει ως Id η επόμενη ερώτηση που θα αποθηκευτεί.
     *
     * Π.χ. αν έχουμε 4 απθηκευμένες ερωτήσεις με Ids 1,3,4,8 μετά την αρχικοποίησηη το currentSavedId
     * είναι 9. Οι αριθμοί των Ids δεν είναι διαδοχικοί καθώς ενδέχται κάποια ερώτηση που κάποτε ήταν
     * αποθηκευμένη, αργότερα να διαγράφτηκε από τον χρήστη.
     *
     * Το currentSavedId δεν έχει την τιμή του μεγέθους του πίνακα καθώς αυτός μπορεί να έχει μέγεθος 6 αλλά τα Ids
     * των ερωτήσεων είναι 1,2,4,5,6,7. Επομένως αν ίσχυε ότι currentSavedId = size + 1 θα είχαμε πρόβλημα.
     */
    public void initCurrentSavedId(){
        String selectQuery = "SELECT MAX(" + TABLE_SAVED_QUESTIONS + "." + COLUMN_ID +  ") FROM " +  TABLE_SAVED_QUESTIONS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst() && cursor.getString(0) != null )
            currentSavedId = Integer.parseInt(cursor.getString(0)) + 1;
        else currentSavedId = 1;
        db.close();
    }

    /**
     * Συνάρτηση που επιστρέφει μια συγκεκριμένη ερώτηση από τις αποθηκευμένες ερωτήσεις της βάσης δεδομένων.
     */
    public QuestionDB getSavedById(int savedQuestionId){
        String query = "SELECT " + TABLE_QUESTIONS + ".* FROM " + TABLE_QUESTIONS +
                " WHERE " + TABLE_QUESTIONS + "." + COLUMN_ID + " = " + savedQuestionId;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        int tmp1, tmp2, tmp3, tmp8;
        String tmp4="", tmp5="", tmp6="", tmp7="";
        tmp1 = Integer.parseInt(cursor.getString(0));
        tmp2 = Integer.parseInt(cursor.getString(1));
        tmp3 = Integer.parseInt(cursor.getString(2));
        tmp8 = Integer.parseInt(cursor.getString(7));
        tmp4 = cursor.getString(3);
        tmp5 = cursor.getString(4);
        tmp6 = cursor.getString(5);
        tmp7 = cursor.getString(6);
        QuestionDB question = new QuestionDB(tmp1, tmp2, tmp3, tmp4, tmp5, tmp6, tmp7, tmp8 );
        db.close();
        return question;
    }

    /**
     * Συνάρτηση που αφαιρεί μια ερώτηση από τις αποθηκευμένες ερωτήσεις της βάσης δεδομένων.
     */
    public void deleteSaved(int savedQuestionId){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SAVED_QUESTIONS, COLUMN_QUESTION_ID + " =?", new String[]{Integer.toString(savedQuestionId)});
        db.close();
    }

    /**
     * Συνάρτηση που επιστρέφει το πλήθος των ερωτήσεων που περιέχονται στον πίνακα με τις
     * αποθηκευμένες ερωτήσεις της βάσης δεδομένων.
     */
    public int getSavedSize(){
        String query = "SELECT * FROM " + TABLE_SAVED_QUESTIONS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        int size = cursor.getCount();
        db.close();
        return size;
    }

    /**
     * Συνάρτηση που προσθέτει στην βάση δεδομένων τις απαντήσεις του χρήστη σε κάποιο τεστ που
     * αυτός ολοκλήρωσε. Για να το πετύχει αυτό κάνει χρήση της βοηθητικής κλάσης TriesDB.
     */
    public void addTestQuestions(TriesDB[] testQuestions){
        int testId = getTestSize();
        int testQuestionId = testId*20+1;

        SQLiteDatabase db = this.getWritableDatabase();
        for (int i=0; i<20; i++) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_ID, testQuestionId++);
            values.put(COLUMN_TEST_ID, testId);
            values.put(COLUMN_QUESTION_ID, testQuestions[i].getQuestionId());
            values.put(COLUMN_ANSWER, testQuestions[i].getAnswer());
            values.put(COLUMN_INCREASING, testQuestions[i].getIncreasing());
            db.insert(TABLE_TEST_QUESTIONS, null, values);
        }
        db.close();
    }

    /**
     * Συνάρτηση που επιστρέφει τόσο τις ερωτήσεις όσο και τις απαντήσεις του χρήστη σε αυτές από ένα
     * παλιότερο τεστ που αυτός έχει ήδη ολοκληρώσει. Για να το πετύχει αυτό κάνει χρήση της βοηθητικής
     * κλάσης TestQuestionDB.
     */
    public TestQuestionDB[] getPreviousTestQuestions(int testId){
        String query = "SELECT " +
                TABLE_QUESTIONS + "." + COLUMN_ID + "," +
                TABLE_QUESTIONS + "." + COLUMN_QUESTION + "," +
                TABLE_QUESTIONS + "." + COLUMN_CHOICE_1 + "," +
                TABLE_QUESTIONS + "." + COLUMN_CHOICE_2 + "," +
                TABLE_QUESTIONS + "." + COLUMN_CHOICE_3 + "," +
                TABLE_QUESTIONS + "." + COLUMN_CORRECT_ANSWER + "," +
                TABLE_TEST_QUESTIONS + "." + COLUMN_ANSWER +
                " FROM " + TABLE_TESTS + " INNER JOIN " + TABLE_TEST_QUESTIONS + " ON " +
                TABLE_TESTS + "." + COLUMN_ID + " = " + TABLE_TEST_QUESTIONS + "." + COLUMN_TEST_ID +
                " INNER JOIN " + TABLE_QUESTIONS + " ON " + TABLE_TEST_QUESTIONS + "." + COLUMN_QUESTION_ID +
                " = " + TABLE_QUESTIONS + "." + COLUMN_ID + " WHERE " + TABLE_TEST_QUESTIONS + "." +
                COLUMN_TEST_ID + " = " + testId;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        TestQuestionDB[] questions = new TestQuestionDB[20];
        int tmp1, tmp6, tmp7;
        String tmp2="", tmp3="", tmp4="", tmp5="";
        int i=0;
        cursor.moveToFirst();
        do {
            tmp1 = Integer.parseInt(cursor.getString(0));
            tmp6 = Integer.parseInt(cursor.getString(5));
            tmp7 = Integer.parseInt(cursor.getString(6));
            tmp2 = cursor.getString(1);
            tmp3 = cursor.getString(2);
            tmp4= cursor.getString(3);
            tmp5 = cursor.getString(4);
            questions[i++] = new TestQuestionDB(tmp1, tmp2, tmp3, tmp4, tmp5, tmp6, tmp7);
        } while (cursor.moveToNext());
        db.close();
        return questions;
    }
}
