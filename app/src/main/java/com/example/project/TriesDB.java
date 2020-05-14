package com.example.project;

public class TriesDB {
    private int _id;
    private int testId;
    private int questionId;
    private int answer;
    private int increasing;

    public TriesDB(int _id, int testId, int questionId, int answer, int increasing){
        this._id = _id;
        this.testId = testId;
        this.questionId = questionId;
        this.answer = answer;
        this.increasing = increasing;
    }

    public int get_id() {
        return _id;
    }

    public int getTestId() {
        return testId;
    }

    public int getQuestionId() {
        return questionId;
    }

    public int getAnswer() {
        return answer;
    }

    public void setAnswer(int answer) {
        this.answer = answer;
    }

    public int getIncreasing() {
        return increasing;
    }
}
