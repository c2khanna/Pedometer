/* ECE 155 - LAB 2
 * (ANNA MA 20458438)
 * (RAFIC DALATI 20526978)
 * (CHAITANYA KHANNA 20542268)
 * February 23, 2014
 */

package ca.uwaterloo.Lab2_203_28;

import java.util.Arrays;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.graphics.Color;
import android.graphics.Typeface;

public class MainActivity extends Activity implements OnClickListener {
	
	// DECLARING GLOBAL VARIABLES
	LinearLayout ll;
	int position = 0;
	int steps = 0;
	
	// VARIABLES TO SHOW SENSOR VALUES
	TextView acceltv;

	
	// IMPLEMENTATION OF GRAPH
	LineGraphView graph;
	
	
	@Override
	public void onClick(View V){
		steps = 0;
	}
	// MAIN BODY CODE
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
		// CHANGES LAYOUT TO BE LINEAR
        ll = (LinearLayout) findViewById(R.id.label2);
		ll.setOrientation(LinearLayout.VERTICAL);
		
		
		// SHOW THE GRAPH ON THE APP
		graph = new LineGraphView(getApplicationContext(),
				100,
				Arrays.asList("x", "y", "z"));
		
		ll.addView(graph);
		graph.setVisibility(View.VISIBLE);
		
		
		// DISPLAY ACCELEROMETER'S SENSOR
		acceltv= new TextView(getApplicationContext());
		ll.addView(acceltv);
		
		
		// ------- SENSORS -------------	
		// -----------------------------
		// ACCELERATOR SENSOR
		
		SensorManager accelManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		
		Sensor accelSensor = accelManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

		SensorEventListener a = new AccelerometerSensorEventListener(acceltv);
		accelManager.registerListener(a, accelSensor, SensorManager.SENSOR_DELAY_FASTEST);
		
		
		// CLEAR BUTTON IMPLEMENTATION
		
		Button B = new Button(getApplicationContext());
	    B.setText("Would you rather reset?");
	    ll.addView(B);
	    B.setOnClickListener(this);
	        
    }
	
    
// ---------- SENSOR CLASSES ----------- 
// ACCELEROMETER SENSOR CLASS

class AccelerometerSensorEventListener implements SensorEventListener {
	TextView output;
	
	public AccelerometerSensorEventListener(TextView outputView){
		output = outputView;
	}
	
	public void onAccuracyChanged(Sensor s, int i) {}
	float [] smoothedAccel;
	
	public void onSensorChanged(SensorEvent ac) {
		if (ac.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
			
			// LOW-PASS FILTER
			smoothedAccel = ac.values;
			smoothedAccel[1] += (ac.values[1] - smoothedAccel[1]) / 2.5;
			smoothedAccel[0] = 0;
			smoothedAccel[2] = 0;
		
			// STATE MACHINE
			if (position == 0 && smoothedAccel[1] >= 0.1 && smoothedAccel[1] <= 0.2)
			{
				 position= 1;				// RISING STATE
			}
			if (position == 1 && smoothedAccel[1] >= 0.6 && smoothedAccel[1] <= 1.5){
				position = 2; 				// PEAK STATE
			}
			if (position == 2 && smoothedAccel[1] >= 0.6 && smoothedAccel[1] <= 1.2){
				position = 3;				//FALLING STATE
			}
			if (position == 3){
				steps++;
				position = 0;
			}
}
			
			output.setText(String.format("On the other hand, here is my counter: %d \n" 
						+"Position: %d", steps, position));
			output.setTextSize(16);
			output.setGravity(Gravity.CENTER_HORIZONTAL);
			output.setTextColor(Color.BLACK);
			output.setTypeface(output.getTypeface(), Typeface.BOLD);
			
			// GRAPH THE VALUE POINTS FOR ACCELEROMETER
			graph.addPoint(smoothedAccel);

			
		
		}
	}
}
  


    
   



