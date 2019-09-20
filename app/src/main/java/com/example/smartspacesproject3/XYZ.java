package com.example.smartspacesproject3;

public class XYZ {

    private float x;
    private float y;
    private float z;

    XYZ(float x, float y, float z)
    {
        this.x=x;
        this.y=y;
        this.z=z;

    }


    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public String toString()
    {
        return "x: " + x + " y: " + y +" z: " + z + "\n";
    }
}
