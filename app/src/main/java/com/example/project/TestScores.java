package com.example.project;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class TestScores extends AppCompatActivity {

    private LineGraphSeries<DataPoint> series1;
    double myArray[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_scores);


        String text1 = "", text2 = "";
        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
        int testSize = dbHandler.getTestSize();

		if (testSize == 0)
			return;

        int x=0,y;
        GraphView graph=findViewById(R.id.graph);
        series1 = new LineGraphSeries<>();
		int[][] tests = new int[testSize][2];
        tests = dbHandler.getTests();;
        for(int i=0; i<testSize; i++){
            x++;
            y=tests[i][1];
            series1.appendData(new DataPoint(x,y),true,testSize);
        }

        graph.addSeries(series1);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMaxX(testSize);
    }
}
