package com.example.accelgraph;

import com.example.accelgraph.R;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements SensorEventListener {

    private final static String TAG = "MainActivity";

    private SensorManager sensorMgr;
    private Sensor accelerometer;

    private TextView rateView;
    private GraphView xView, yView, zView;

    private float vx, vy, vz;
    private float rate;
    private long prevTs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        setContentView(R.layout.activity_main);

        rateView = (TextView) findViewById(R.id.rate_view);
        xView = (GraphView) findViewById(R.id.x_view);
        yView = (GraphView) findViewById(R.id.y_view);
        zView = (GraphView) findViewById(R.id.z_view);

        sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer == null) {
            Toast.makeText(this, "No acceleromter available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        sensorMgr.registerListener(this, accelerometer, 10 * 1000);
        th = new DisplayThread();
        th.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
        th = null;
        sensorMgr.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        vx = event.values[0];
        vy = event.values[1];
        vz = event.values[2];
        rate = ((float) (event.timestamp - prevTs)) / (1000 * 1000);
        prevTs = event.timestamp;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.i(TAG, "onAccuracyChanged: ");
    }

    Handler handler = new Handler();

    private DisplayThread th = null;
    
    private final static long DISPLAY_RATE = 50;

    private class DisplayThread extends Thread {
        public void run() {
            try {
                while (th != null) {
                    handler.post(new Runnable() {
                        public void run() {
                            rateView.setText(Float.toString(rate));
                            xView.setVal(vx);
                            yView.setVal(vy);
                            zView.setVal(vz);
                            xView.invalidate();
                            yView.invalidate();
                            zView.invalidate();
                        }
                    });
                    Thread.sleep(DISPLAY_RATE);
                }
            }
            catch (InterruptedException e) {}
        }
    }

}
