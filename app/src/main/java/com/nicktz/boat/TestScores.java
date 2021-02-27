package com.nicktz.boat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

/**
 * Κλάση που δημιουργεί ένα γράφημα με όλα τα σκορ των τεστ του χρήστη.
 */
public class TestScores extends AppCompatActivity {

    private LineGraphSeries<DataPoint> series;

    TextView successes;
    TextView failures;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_scores);

        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
        int testSize = dbHandler.getTestSize();

		if (testSize == 0)
			return;

		int successesSum = 0;
		int failuresSum = 0;

        int x=0,y;
        GraphView graph=findViewById(R.id.graph);

        //Στην series θα αποθηκεύσουμε όλα τα σημεία τύπου DataPoint που θα απεικονιστούν στο γράφημα.
        series = new LineGraphSeries<>();
		int[][] tests = new int[testSize][2];
        tests = dbHandler.getTests();;
        for(int i=0; i<testSize; i++){
            x++;
            y = tests[i][1];
            series.appendData(new DataPoint(x,y),true,testSize);

            if (y >= 18)
                successesSum++;
            else failuresSum++;
        }

        successes = findViewById(R.id.successes);
        failures = findViewById(R.id.failures);

        successes.setText(getString(R.string.successes) + " " + successesSum);
        failures.setText(getString(R.string.failures) + " " + failuresSum);

        //Δημιουργείται το γράφημα.
        graph.addSeries(series);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMaxX(testSize);
    }
}
