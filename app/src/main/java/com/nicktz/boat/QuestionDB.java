package com.nicktz.boat;

/**
 * Βοηθητική κλάση για τον πίνακα της βάσης δεδομένων που περιέχει όλες τις πιθανές ερωτήσεις,
 * τις ερωτήσεις δηλαδή που αποτελούν την ύλη της εξέτασης.
 */
public class QuestionDB {
    private int _id;
    private int chapter;
    private int question_no;
    private String question;
    private String choice_1;
    private String choice_2;
    private String choice_3;
    private int correct_answer;

    public QuestionDB(int _id, int chapter, int question_no, String question, String choice_1, String choice_2, String choice_3, int correct_answer){
        this._id = _id;
        this.chapter = chapter;
        this.question_no = question_no;
        this.question = question;
        this.choice_1 = choice_1;
        this.choice_2 = choice_2;
        this.choice_3 = choice_3;
        this.correct_answer = correct_answer;
    }

    public int get_id() {
        return _id;
    }

    public int getChapter() {
        return chapter;
    }

    public int getQuestion_no() {
        return question_no;
    }

    public String getQuestion() {
        return question;
    }

    public String getChoice_1() {
        return choice_1;
    }

    public String getChoice_2() {
        return choice_2;
    }

    public String getChoice_3() {
        return choice_3;
    }

    public int getCorrect_answer() {
        return correct_answer;
    }
}
