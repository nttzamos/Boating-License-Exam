package com.example.project;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class TestScores extends AppCompatActivity {

    private LineGraphSeries<DataPoint> series;

    TextView succ;
    TextView fail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_scores);

        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
        int testSize = dbHandler.getTestSize();

		if (testSize == 0)
			return;

		int success = 0;
		int failure = 0;

        int x=0,y;
        GraphView graph=findViewById(R.id.graph);
        series = new LineGraphSeries<>();
		int[][] tests = new int[testSize][2];
        tests = dbHandler.getTests();;
        for(int i=0; i<testSize; i++){
            x++;
            y = tests[i][1];
            series.appendData(new DataPoint(x,y),true,testSize);

            if (y >= 19)
                success++;
            else failure++;
        }

        succ = findViewById(R.id.success);
        fail = findViewById(R.id.failure);

        succ.setText("Επιτυχημένα: " + success);
        fail.setText("Αποτυχημένα: " + failure);

        graph.addSeries(series);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMaxX(testSize);
    }
}
