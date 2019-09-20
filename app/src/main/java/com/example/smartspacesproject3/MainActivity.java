package com.example.smartspacesproject3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;

import java.io.IOException;
import java.util.Vector;

import weka.classifiers.Classifier;
import weka.core.Instance;

public class MainActivity extends FragmentActivity {

    private SensorManager sensorManager;
    private Sensor accelerometer, linearAccelerationSensor, gyroscope, magnetometer;

    private SensorEventListener listener;

    private Vector<XYZ> accValues = new Vector<>();
    private Vector<XYZ> linAccValues = new Vector<>();
    private Vector<XYZ> gyroValues = new Vector<>();
    private Vector<XYZ> magValues = new Vector<>();

    private ModelClassifier modelClassifier = new ModelClassifier();

    DrawView drawView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        drawView = new DrawView(this);
        setContentView(drawView);


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        linearAccelerationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        listener = createListener();
        // sensorManager.registerListener(createListener(), accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        try {
            modelClassifier.loadModel(MainActivity.this.getApplicationContext().getAssets().open("model.model"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            modelClassifier.loadSource(MainActivity.this.getApplicationContext().getAssets().open("source.arff"));
        } catch (IOException e) {
            e.printStackTrace();
        }


        Thread thread = createThread();
        thread.start();
    }

    @Override
    protected void onStart() {
        super.onStart();



        if(accelerometer!=null)
        {
            sensorManager.registerListener(listener,accelerometer,SensorManager.SENSOR_DELAY_FASTEST);
            Log.d("onStart", "accelerometer registered");
        }

        if(linearAccelerationSensor!=null)
        {
            sensorManager.registerListener(listener,linearAccelerationSensor,SensorManager.SENSOR_DELAY_FASTEST);
            Log.d("onStart", "linear acceleration sensor registered");
        }

        if(gyroscope!=null)
        {
            sensorManager.registerListener(listener,gyroscope,SensorManager.SENSOR_DELAY_FASTEST);
            Log.d("onStart", "gyroscope registered");
        }

        if(magnetometer!=null)
        {
            sensorManager.registerListener(listener,magnetometer,SensorManager.SENSOR_DELAY_FASTEST);
            Log.d("onStart", "magnetometer registered");
        }




    }

    @Override
    protected void onStop() {
        super.onStop();
        sensorManager.unregisterListener(listener);
    }

    private SensorEventListener createListener()
    {
        return new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {

                int sensorType = sensorEvent.sensor.getType();



                switch(sensorType) {

                    case Sensor.TYPE_ACCELEROMETER:
                        XYZ currentValuesa = new XYZ(sensorEvent.values[0],sensorEvent.values[1], -sensorEvent.values[2]);
                        accValues.add(currentValuesa); break;
                    case Sensor.TYPE_GYROSCOPE:
                        XYZ currentValuesb = new XYZ(sensorEvent.values[0],sensorEvent.values[1], sensorEvent.values[2]);
                        gyroValues.add(currentValuesb); break;

                    case Sensor.TYPE_MAGNETIC_FIELD:
                        XYZ currentValuesc = new XYZ(sensorEvent.values[0],sensorEvent.values[1], sensorEvent.values[2]);
                        magValues.add(currentValuesc); break;

                    case Sensor.TYPE_LINEAR_ACCELERATION:
                        XYZ currentValuesd = new XYZ(sensorEvent.values[0],sensorEvent.values[1], sensorEvent.values[2]);
                        linAccValues.add(currentValuesd); break;

                    default: break;
                }

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
    }

    private Thread createThread()
    {
        return new Thread(){
            @Override
            public void run() {
                super.run();
                long currentTime = System.currentTimeMillis();

                while (true) {
                    if (System.currentTimeMillis() - currentTime > 1000) {


                        currentTime += 1000;
                        Log.d("run", "accelerometer: " + accValues.size() + "\nlinear acc: " + linAccValues.size() + "\ngyroscope: " + gyroValues.size() + "\nmagnetometer: " + magValues.size());
                        //Log.d("classification", classify());
                        int result = classify();

                        changeColor(result);
                        drawView.updateView();



                        clearVectors();
                    }
                }
            }
        };
    }

    private void clearVectors()
    {
        accValues.clear();
        linAccValues.clear();
        gyroValues.clear();
        magValues.clear();
    }


    private int classify()
    {
        accValues = movingAverage.movingAverage(10, accValues);
        linAccValues = movingAverage.movingAverage(2, linAccValues);
        gyroValues = movingAverage.movingAverage(10, gyroValues);
        magValues = movingAverage.movingAverage(2, magValues);

        int []states = new int[7];
        for(int i = 0; i<accValues.size() && i<linAccValues.size() && i<gyroValues.size() && i<magValues.size(); ++i)
        {

            //Log.d("sensordata", "acc "+accValues.get(i).toString()+linAccValues.get(i).toString()+gyroValues.get(i).toString()+magValues.get(i).toString() );
            Instance instance = modelClassifier.createInstance(accValues.get(i),linAccValues.get(i),gyroValues.get(i),magValues.get(i));
            double result = modelClassifier.classifyInstance(instance);

            if(result>=0 && result <7)
                ++states[(int) result];

        }
        int state = 0;
        int value = 0;

        for(int i = 0; i<7; ++i)
        {
            Log.d("state","state: " + i +" instances: " + states[i]);
            if(value<states[i]) {
                value = states[i];
                state = i;
            }

        }



        return state;




    }


    private void changeColor(int result)
    {

        //0-walking-green
        //1-sitting-red
        //2-jogging-purple
        //3-sitting-brown
        //4-biking-orange
        //5-upstairs-white
        //6-downstairs-black
        switch(result)
        {
            case 0: drawView.changePaintColor(Color.GREEN); break;

            case 1: drawView.changePaintColor(Color.RED); break;

            case 2: drawView.changePaintColor(Color.rgb(128,0,128));break;

            case 3: drawView.changePaintColor(Color.rgb(0,0,255));break;

            case 4: drawView.changePaintColor(Color.rgb(255,165,0));break;

            case 5: drawView.changePaintColor(Color.WHITE); break;

            case 6: drawView.changePaintColor(Color.BLACK); break;

            default:
                break;

        }


    }






}

