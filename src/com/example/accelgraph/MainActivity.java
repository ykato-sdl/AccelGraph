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

    private TextView rateView, accuracyView;
    private GraphView xView, yView, zView;

    private SensorManager sensorMgr;
    private Sensor accelerometer;

    private final static long GRAPH_REFRESH_WAIT_MS = 20;

    private GraphRefreshThread th = null;
    private Handler handler;

    private float vx, vy, vz;
    private float rate;
    private int accuracy;
    private long prevTs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        setContentView(R.layout.activity_main);

        rateView = (TextView) findViewById(R.id.rate_view);
        accuracyView = (TextView) findViewById(R.id.accuracy_view);
        xView = (GraphView) findViewById(R.id.x_view);
        yView = (GraphView) findViewById(R.id.y_view);
        zView = (GraphView) findViewById(R.id.z_view);

        sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer == null) {
            Toast.makeText(this, "No acceleromter available", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        handler = new Handler();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        sensorMgr.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        th = new GraphRefreshThread();
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
        this.accuracy = accuracy;
    }

    private class GraphRefreshThread extends Thread {
        public void run() {
            try {
                while (th != null) {
                    handler.post(new Runnable() {
                        public void run() {
                            rateView.setText(Float.toString(rate));
                            accuracyView.setText(Integer.toString(accuracy));
                            xView.setVal(vx);
                            yView.setVal(vy);
                            zView.setVal(vz);
                            xView.invalidate();
                            yView.invalidate();
                            zView.invalidate();
                        }
                    });
                    Thread.sleep(GRAPH_REFRESH_WAIT_MS);
                }
            }
            catch (InterruptedException e) {
                th = null;
            }
        }
    }

}
