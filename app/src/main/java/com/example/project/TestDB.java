package com.example.project;

import java.util.Arrays;
import java.util.Collections;

public class TestDB {
    private int _id;
    private int score;

    public TestDB(int _id, int score){
        this._id = _id;
        this.score = score;
    }

    public int get_id() {
        return _id;
    }

    public int getScore() {
        return score;
    }

}
