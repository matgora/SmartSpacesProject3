package com.example.smartspacesproject3;

import java.util.Vector;

public class movingAverage {

    public static Vector<XYZ> movingAverage(int windowSize, Vector<XYZ> data)
    {
        //create a new vector for the averaged data
        Vector<XYZ> newData = new Vector<>();

        //for every windowSize value (if windowSize is 5, for every 5th value)
        for(int i = 0; i <= data.size() - windowSize; i += windowSize)
        {
            //add the next windowSize values
            float sumx = (float) 0.0;
            float sumy = (float) 0.0;
            float sumz = (float) 0.0;
            for(int j = 0; j < windowSize; j++)
            {
                sumx += data.get(i+j).getX();
                sumy += data.get(i+j).getY();
                sumz += data.get(i+j).getZ();

            }
            //get the average of these values
            float averagex = sumx / windowSize;
            float averagey = sumy / windowSize;
            float averagez = sumz / windowSize;


            //add to the new data
            newData.add(new XYZ(averagex,averagey,averagez));
        }
        return newData;
    }

}
