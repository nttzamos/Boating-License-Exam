package com.example.project;

public class SavedDB {
    private int _id;
    private int questionId;

    public SavedDB(int _id, int questionId){
        this._id = _id;
        this.questionId = questionId;
    }

    public int get_id() {
        return _id;
    }

    public int getQuestionId() {
        return questionId;
    }
}