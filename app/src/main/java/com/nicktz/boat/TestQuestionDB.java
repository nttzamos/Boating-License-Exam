package com.nicktz.boat;

/**
 * Βοηθητική κλάση που χρησιμοποιείται για να εμφανιστούν στον χρήστη οι απαντήσεις του σε μια
 * από της προηγούμενες προσπάθειες του. Είναι μια μίξη της QuestionDB και της TriesDB καθώς
 * περιλαμβάνει τόσο τα στοιχεία μιας ερώτησης όσο και την απάντηση του χρήστη σε αυτήν.
 */
public class TestQuestionDB {
    private int _id;
    private String question;
    private String choice_1;
    private String choice_2;
    private String choice_3;
    private int correct_answer;
    private int answer;

    public TestQuestionDB(int _id, String question, String choice_1, String choice_2, String choice_3, int correct_answer, int answer){
        this._id = _id;
        this.question = question;
        this.choice_1 = choice_1;
        this.choice_2 = choice_2;
        this.choice_3 = choice_3;
        this.correct_answer = correct_answer;
        this.answer = answer;
    }

    public int get_id() {
        return _id;
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

    public int getAnswer() {
        return answer;
    }
}
