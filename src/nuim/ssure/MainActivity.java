package nuim.ssure;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphView.LegendAlign;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements SensorEventListener {
	private boolean mInitialized; 
	private SensorManager mSensorManager; 
	private Sensor mAccelerometer, mGyroscope, mGravity; 
	/** Called when the activity is first created. */

	private final Handler mHandler = new Handler();
	private Runnable mTimer;
	private double graph2LastXValue = 50d;
	private GraphViewSeries AcceX, AcceY, AcceZ;
	private GraphViewSeries GyroX, GyroY, GyroZ;
	private GraphViewSeries GravX, GravY, GravZ;
	private float[] acce = new float[3], gyro = new float[3], grav = new float[3];
	private int idA = 0, idG = 0;
	
	//private File filename;
	private char Mode = 'A';
	private boolean SaveMode = false;
	private dataHelper dataHelper;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mInitialized = false;
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		mGravity = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
		mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
		mSensorManager.registerListener(this, mGravity, SensorManager.SENSOR_DELAY_NORMAL);

		AcceX = new GraphViewSeries("X", null, new GraphViewData[] {});
		AcceY = new GraphViewSeries("Y", null, new GraphViewData[] {});
		AcceZ = new GraphViewSeries("Z", null, new GraphViewData[] {});
		
		GyroX = new GraphViewSeries("X", null, new GraphViewData[] {});
		GyroY = new GraphViewSeries("Y", null, new GraphViewData[] {});
		GyroZ = new GraphViewSeries("Z", null, new GraphViewData[] {});
		
		GravX = new GraphViewSeries("X", null, new GraphViewData[] {});
		GravY = new GraphViewSeries("Y", null, new GraphViewData[] {});
		GravZ = new GraphViewSeries("Z", null, new GraphViewData[] {});
		
		AcceX.getStyle().color = Color.RED;
		GyroX.getStyle().color = Color.RED;
		GravX.getStyle().color = Color.RED;
		
		AcceY.getStyle().color = Color.GREEN;
		GyroY.getStyle().color = Color.GREEN;
		GravY.getStyle().color = Color.GREEN;
		
		AcceZ.getStyle().color = Color.YELLOW;
		GyroZ.getStyle().color = Color.YELLOW;
		GravZ.getStyle().color = Color.YELLOW;
		
		final GraphView graphViewA = new LineGraphView(this, "GraphAccelerometer");
		final GraphView graphViewG = new LineGraphView(this, "GraphGyroscope");
		final GraphView graphViewV = new LineGraphView(this, "GraphGravity");
		
		//Accelerometer
		graphViewA.addSeries(AcceX);
		graphViewA.addSeries(AcceY);
		graphViewA.addSeries(AcceZ);
		
		graphViewA.setViewPort(0,1000);
		graphViewA.setScalable(true);
		graphViewA.setShowLegend(true);
		graphViewA.setLegendAlign(LegendAlign.BOTTOM);
		graphViewA.getGraphViewStyle().setVerticalLabelsWidth(1);
		graphViewA.getGraphViewStyle().setLegendBorder(20);
		graphViewA.getGraphViewStyle().setLegendSpacing(20);
		graphViewA.getGraphViewStyle().setLegendWidth(120);
		
		//Gyroscope
		graphViewG.addSeries(GyroX);
		graphViewG.addSeries(GyroY);
		graphViewG.addSeries(GyroZ);
		
		graphViewG.setViewPort(0,1000);
		graphViewG.setScalable(true);
		graphViewG.setShowLegend(true);
		graphViewG.getGraphViewStyle().setVerticalLabelsWidth(1);
		graphViewG.setLegendAlign(LegendAlign.BOTTOM);
		graphViewG.getGraphViewStyle().setLegendBorder(20);
		graphViewG.getGraphViewStyle().setLegendSpacing(20);
		graphViewG.getGraphViewStyle().setLegendWidth(120);
		
		//Gravity
		graphViewV.addSeries(GravX);
		graphViewV.addSeries(GravY);
		graphViewV.addSeries(GravZ);
		
		graphViewV.setViewPort(0,1000);
		graphViewV.setScalable(true);
		graphViewV.setShowLegend(true);
		graphViewV.getGraphViewStyle().setVerticalLabelsWidth(1);
		graphViewV.setLegendAlign(LegendAlign.BOTTOM);
		graphViewV.getGraphViewStyle().setLegendBorder(20);
		graphViewV.getGraphViewStyle().setLegendSpacing(20);
		graphViewV.getGraphViewStyle().setLegendWidth(120);
		
		((LineGraphView) graphViewA).setDrawBackground(false);
		((LineGraphView) graphViewG).setDrawBackground(false);
		((LineGraphView) graphViewV).setDrawBackground(false); 
		
		LinearLayout layout = (LinearLayout) findViewById(R.id.ssure);
		layout.addView(graphViewA);
		layout.addView(graphViewG);
		layout.addView(graphViewV);
		
		graphViewA.setVisibility(View.VISIBLE);
		graphViewG.setVisibility(View.GONE);
		graphViewV.setVisibility(View.GONE);
				
		final TextView TAccelerometer = (TextView) findViewById(R.id.t_accelerometer);
		final TableLayout TAAccelerometer = (TableLayout) findViewById(R.id.ta_accelerometer);
		TAccelerometer.setVisibility(View.VISIBLE);
		TAAccelerometer.setVisibility(View.VISIBLE);
		
        final TextView TGyroscope = (TextView) findViewById(R.id.t_gyroscope);
        final TableLayout TAGyroscope = (TableLayout) findViewById(R.id.ta_gyroscope);
        TGyroscope.setVisibility(View.GONE);
        TAGyroscope.setVisibility(View.GONE);
        
        final TextView TGravity = (TextView) findViewById(R.id.t_gravity);
        final TableLayout TAGravity = (TableLayout) findViewById(R.id.ta_gravity);
        TGravity.setVisibility(View.GONE);
        TAGravity.setVisibility(View.GONE);

        final Button BStart= (Button) findViewById(R.id.b_start);
        final Button BStop= (Button) findViewById(R.id.b_stop);    
        final Button BExp= (Button) findViewById(R.id.b_export);  
        final Button BAccelerometer= (Button) findViewById(R.id.b_accelerometer);
        final Button BGyroscope= (Button) findViewById(R.id.b_gyroscope);
        final Button BGravity= (Button) findViewById(R.id.b_gravity);
        
        BStart.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		AcceX.resetData(new GraphViewData[] {});
        		AcceY.resetData(new GraphViewData[] {});
        		AcceZ.resetData(new GraphViewData[] {});
        		
        		GravX.resetData(new GraphViewData[] {});
    			GravY.resetData(new GraphViewData[] {});
    			GravZ.resetData(new GraphViewData[] {});
        		
        		SaveMode = true;
        		dataHelper = new dataHelper(getApplicationContext());
     
        		BStart.setVisibility(Button.INVISIBLE);
        		BStop.setVisibility(Button.VISIBLE);
        		BExp.setVisibility(Button.INVISIBLE);
        		
        		BAccelerometer.setVisibility(Button.INVISIBLE);
                BGyroscope.setVisibility(Button.INVISIBLE);
                BGravity.setVisibility(Button.INVISIBLE);
                
                TAccelerometer.setVisibility(View.VISIBLE);
 	        	TAAccelerometer.setVisibility(View.VISIBLE);
	        	TGyroscope.setVisibility(View.GONE);
	        	TAGyroscope.setVisibility(View.GONE);
	        	TGravity.setVisibility(View.VISIBLE);
 	        	TAGravity.setVisibility(View.VISIBLE);
                
                graphViewA.setVisibility(View.GONE);
	    		graphViewG.setVisibility(View.GONE);
	    		graphViewV.setVisibility(View.GONE);
	    		
        	}
        });
        BStop.setVisibility(Button.INVISIBLE);
        BStop.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		if (Mode=='A') {
        			TAccelerometer.setVisibility(View.VISIBLE);
    	        	TAAccelerometer.setVisibility(View.VISIBLE);
    	        	TGravity.setVisibility(View.GONE);
    	        	TAGravity.setVisibility(View.GONE);
                    
                    graphViewA.setVisibility(View.VISIBLE);
    	    		graphViewV.setVisibility(View.GONE);
        		
        		} else if (Mode=='G') {   		
	        		TAccelerometer.setVisibility(View.GONE);
		        	TAAccelerometer.setVisibility(View.GONE);
		        	TGravity.setVisibility(View.VISIBLE);
		        	TAGravity.setVisibility(View.VISIBLE);
	                
	                graphViewA.setVisibility(View.GONE);
		    		graphViewV.setVisibility(View.VISIBLE);
        		}
        		
        		AcceX.resetData(new GraphViewData[] {});
    			AcceY.resetData(new GraphViewData[] {});
    			AcceZ.resetData(new GraphViewData[] {});

    			GravX.resetData(new GraphViewData[] {});
        		GravY.resetData(new GraphViewData[] {});
        		GravZ.resetData(new GraphViewData[] {});
        		
        		SaveMode = false;
        		dataHelper = null;
        		
        		BStart.setVisibility(Button.VISIBLE);
        		BStop.setVisibility(Button.INVISIBLE);
        		BExp.setVisibility(Button.VISIBLE);
	    		
	    		BAccelerometer.setVisibility(Button.VISIBLE);
                BGyroscope.setVisibility(Button.VISIBLE);
                BGravity.setVisibility(Button.VISIBLE);
                
	        	TGyroscope.setVisibility(View.GONE);
	        	TAGyroscope.setVisibility(View.GONE);
                
	    		graphViewG.setVisibility(View.GONE);
        	}
        });
        BExp.setVisibility(Button.INVISIBLE);
        BExp.setOnClickListener(new OnClickListener () {
        	public void onClick(View v) {
        		final Context context = getApplicationContext();
        		final AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        	    final EditText input = new EditText(MainActivity.this);
        	    input.setText("ExportExcel"); //default
        	    alert.create();
        	    alert.setMessage("Insert File Name..");
        	    alert.setView(input);
        	    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
        	    	@Override
        	        public void onClick(DialogInterface dialog, int whichButton) {
        	    		String exportFileName= input.getText().toString().trim();
        	            Toast.makeText(context, exportFileName, Toast.LENGTH_SHORT).show();
        	            
        	            ExportExcel ExportExcel=new ExportExcel(context);
                		try {
            				ExportExcel.exportDataToCSV(exportFileName, 'A');
            				ExportExcel.exportDataToCSV(exportFileName, 'G');
            				Toast.makeText(context, "File exported successfully", 1).show();
            				clearData();
                    		BExp.setVisibility(Button.INVISIBLE);
            			} catch (Exception e) {
            				Toast.makeText(context, "Please,Import data first", 1).show();
            			
            			}
        	        }
    	    	});
        	    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
        	    	@Override
        	        public void onClick(DialogInterface dialog, int which) { 
        	            // do nothing
        	        }
        	     });
        	    alert.show();
        	}
        });
        BAccelerometer.setOnClickListener(new OnClickListener() {
	        public void onClick(View v) {
	        	TAccelerometer.setVisibility(View.VISIBLE);
	        	TAAccelerometer.setVisibility(View.VISIBLE);
	        	TGyroscope.setVisibility(View.GONE);
	        	TAGyroscope.setVisibility(View.GONE);
	        	TGravity.setVisibility(View.GONE);
	        	TAGravity.setVisibility(View.GONE);
	        	
	        	AcceX.resetData(new GraphViewData[] {});
        		AcceY.resetData(new GraphViewData[] {});
        		AcceZ.resetData(new GraphViewData[] {});

	        	graphViewA.setVisibility(View.VISIBLE);
	    		graphViewG.setVisibility(View.GONE);
	    		graphViewV.setVisibility(View.GONE);
	    		
	    		BStart.setVisibility(Button.VISIBLE);
        		BStop.setVisibility(Button.INVISIBLE);
	    		
	    		Mode = 'A';
	        }
        });
        BGyroscope.setOnClickListener(new OnClickListener() {
	        public void onClick(View v) {
	        	TAccelerometer.setVisibility(View.GONE);
	        	TAAccelerometer.setVisibility(View.GONE);
	        	TGyroscope.setVisibility(View.VISIBLE);
	        	TAGyroscope.setVisibility(View.VISIBLE);
	        	TGravity.setVisibility(View.GONE);
	        	TAGravity.setVisibility(View.GONE);
	        	
	        	GyroX.resetData(new GraphViewData[] {});
        		GyroY.resetData(new GraphViewData[] {});
        		GyroZ.resetData(new GraphViewData[] {});

	        	graphViewA.setVisibility(View.GONE);
	    		graphViewG.setVisibility(View.VISIBLE);
	    		graphViewV.setVisibility(View.GONE);
	    		
	    		BStart.setVisibility(Button.INVISIBLE);
        		BStop.setVisibility(Button.INVISIBLE);
	    		
	    		Mode = 'G';
	        } 
        });	        
        BGravity.setOnClickListener(new OnClickListener() {
	        public void onClick(View v) {
	        	TAccelerometer.setVisibility(View.GONE);
	        	TAAccelerometer.setVisibility(View.GONE);
	        	TGyroscope.setVisibility(View.GONE);
	        	TAGyroscope.setVisibility(View.GONE);
	        	TGravity.setVisibility(View.VISIBLE);
	        	TAGravity.setVisibility(View.VISIBLE);
	        	
	        	GravX.resetData(new GraphViewData[] {});
        		GravY.resetData(new GraphViewData[] {});
        		GravZ.resetData(new GraphViewData[] {});

	        	graphViewA.setVisibility(View.GONE);
	    		graphViewG.setVisibility(View.GONE);
	    		graphViewV.setVisibility(View.VISIBLE);
	    		
	    		BStart.setVisibility(Button.VISIBLE);
        		BStop.setVisibility(Button.INVISIBLE);
	    		
	    		Mode = 'V';
	        }
        });
        
	}

	@Override
	protected void onResume() {
		super.onResume();
		mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
		mSensorManager.registerListener(this, mGravity, SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		//mSensorManager.unregisterListener(this);
		mHandler.removeCallbacks(mTimer);;
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// can be safely ignored for this demo
	}
		
	@Override
	public void onSensorChanged(SensorEvent event) {
		final float alpha = 0.8f;
		Sensor sensor = event.sensor;  	
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {   
        	acce[0] = event.values[0];
        	acce[1] = event.values[1];
        	acce[2] = event.values[2];
        	//acce[0] = alpha * acce[0] + (1 - alpha) * event.values[0];
        	//acce[1] = alpha * acce[1] + (1 - alpha) * event.values[1];
        	//acce[2] = alpha * acce[2] + (1 - alpha) * event.values[2];
        	
        	TextView tvX= (TextView)findViewById(R.id.ax_axis);
    		TextView tvY= (TextView)findViewById(R.id.ay_axis);
    		TextView tvZ= (TextView)findViewById(R.id.az_axis);
    		if (!mInitialized) {
    			tvX.setText("0.0");
    			tvY.setText("0.0");
    			tvZ.setText("0.0");
    			mInitialized = true;
    		} else {
    			tvX.setText(Float.toString(acce[0]));
    			tvY.setText(Float.toString(acce[1]));
    			tvZ.setText(Float.toString(acce[2]));
    		}
    		
    		if (!SaveMode) {
    			if(Mode=='A') {
	    			mTimer = new Runnable() {
		    			@Override
		    			public void run() {
		    				graph2LastXValue += 1d;
		    				AcceX.appendData(new GraphViewData(graph2LastXValue, acce[0]), true, 1000);
		    				AcceY.appendData(new GraphViewData(graph2LastXValue, acce[1]), true, 1000);
		    				AcceZ.appendData(new GraphViewData(graph2LastXValue, acce[2]), true, 1000);
		    				//mHandler.postDelayed(this, 1000);
		    			}
		    		};
    			}
    		} else {
				idA++;
				dataHelper.open();
				dataHelper.insertDataA(idA, acce[0], acce[1], acce[2], System.currentTimeMillis());
				dataHelper.close();
    		}
    		
        } else if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
        	gyro[0] = event.values[0];
        	gyro[1] = event.values[1];
        	gyro[2] = event.values[2];
        	
        	TextView tvX= (TextView)findViewById(R.id.gx_axis);
    		TextView tvY= (TextView)findViewById(R.id.gy_axis);
    		TextView tvZ= (TextView)findViewById(R.id.gz_axis);
    		if (!mInitialized) {
    			tvX.setText("0.0");
    			tvY.setText("0.0");
    			tvZ.setText("0.0");
    			mInitialized = true;
    		} else {
    			tvX.setText(Float.toString(gyro[0]));
    			tvY.setText(Float.toString(gyro[1]));
    			tvZ.setText(Float.toString(gyro[2]));
    		}
    		
    		if (!SaveMode) {
    			if (Mode=='G') {
	    			mTimer = new Runnable() {
		    			@Override
		    			public void run() {
		    				graph2LastXValue += 1d;
		    				GyroX.appendData(new GraphViewData(graph2LastXValue, gyro[0]), true, 1000);
		    				GyroY.appendData(new GraphViewData(graph2LastXValue, gyro[1]), true, 1000);
		    				GyroZ.appendData(new GraphViewData(graph2LastXValue, gyro[2]), true, 1000);
		    				//mHandler.postDelayed(this, 1000);
		    			}
		    		};
	    		}
    		}
    		
        } else if (sensor.getType() == Sensor.TYPE_GRAVITY) {
        	grav[0] = event.values[0];
        	grav[1] = event.values[1];
        	grav[2] = event.values[2];
        	
        	TextView tvX= (TextView)findViewById(R.id.rx_axis);
    		TextView tvY= (TextView)findViewById(R.id.ry_axis);
    		TextView tvZ= (TextView)findViewById(R.id.rz_axis);
    		if (!mInitialized) {
    			tvX.setText("0.0");
    			tvY.setText("0.0");
    			tvZ.setText("0.0");
    			mInitialized = true;
    		} else {
    			tvX.setText(Float.toString(grav[0]));
    			tvY.setText(Float.toString(grav[1]));
    			tvZ.setText(Float.toString(grav[2]));
    		}
    		
    		if (!SaveMode) {
    			if (Mode=='V') {
	    			mTimer = new Runnable() {
		    			@Override
		    			public void run() {
		    				graph2LastXValue += 1d;
		    				GravX.appendData(new GraphViewData(graph2LastXValue, grav[0]), true, 1000);
		    				GravY.appendData(new GraphViewData(graph2LastXValue, grav[1]), true, 1000);
		    				GravZ.appendData(new GraphViewData(graph2LastXValue, grav[2]), true, 1000);
		    				//mHandler.postDelayed(this, 1000);
		    			}
		    		};
    			}
    		} else {
				idG++;
				dataHelper.open();
				dataHelper.insertDataG(idG, grav[0], grav[1], grav[2], System.currentTimeMillis());
				dataHelper.close();
    		}
    	} 
        
        mHandler.postDelayed(mTimer, 50);
	}
	
	private void clearData() {
		idA=0; idG=0;
		dataHelper = new dataHelper(getApplicationContext());
		dataHelper.open();
		dataHelper.reset();
		dataHelper.close();
		dataHelper = null;
	}
	
}