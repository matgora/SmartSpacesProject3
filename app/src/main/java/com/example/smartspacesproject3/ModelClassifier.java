package com.example.smartspacesproject3;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


import weka.classifiers.Classifier;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils;

public class ModelClassifier {

    Classifier mClassifier;
    Instances structure;
    Instances labeled;
    Instances ourdata;

    public void loadModel(InputStream path)
    {

        try {
            mClassifier = (Classifier) SerializationHelper.read(path);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void loadSource(InputStream path)
    {
        ConverterUtils.DataSource source = new ConverterUtils.DataSource(path);


        try {
            structure = source.getStructure();
        } catch (Exception e) {
            e.printStackTrace();
        }
        structure.setClassIndex(structure.numAttributes()-1);

        labeled = new Instances(structure);

        ourdata = new Instances(structure);

        Log.d("structure",structure.toString());

    }

    public Instance createInstance(XYZ accelerometer, XYZ linAcceleration, XYZ gyroscope, XYZ magnetometer)
    {
        double [] instanceValue = new double[]{accelerometer.getX(),accelerometer.getY(),accelerometer.getZ(), linAcceleration.getX(),
        linAcceleration.getY(), linAcceleration.getZ(), gyroscope.getX(), gyroscope.getY(), gyroscope.getZ(), magnetometer.getX(),
        magnetometer.getY(), magnetometer.getZ(), 0};
        labeled.add(new DenseInstance(1.0, instanceValue));
        return labeled.lastInstance();

    }

    public double classifyInstance(Instance instance)
    {
        double result = -1.0;
        try {
            result =  mClassifier.classifyInstance(instance);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public String whichState(double classification)
    {
        Attribute attribute = structure.classAttribute();

        Enumeration enumeration = attribute.enumerateValues();

        if(classification>=0) {
            List list = Collections.list(enumeration);

            String state = (String) list.get((int) classification);

            return state;
        }

        return null;

    }










}
