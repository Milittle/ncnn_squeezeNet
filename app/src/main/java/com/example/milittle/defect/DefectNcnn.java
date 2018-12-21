package com.example.milittle.defect;

import android.graphics.Bitmap;

public class DefectNcnn {
    public native boolean Init(byte[] param, byte[] bin, byte[] words);

    public native String Detect(Bitmap bitmap);

    static {
        System.loadLibrary("defect_ncnn");
    }
}
