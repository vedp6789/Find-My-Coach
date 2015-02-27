package com.findmycoach.app.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import java.io.ByteArrayOutputStream;

/**
 * Created by IgluLabs on 1/22/2015.
 */
public class BinaryForImage {

    public static Bitmap getBitmapFromBinaryString(String binaryObject){
        byte[] decodeArray = Base64.decode(binaryObject, Base64.DEFAULT);
        return  BitmapFactory.decodeByteArray(decodeArray, 0, decodeArray.length);
    }

    public static String getBinaryStringFromBitmap(Bitmap userPic) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        userPic.compress(Bitmap.CompressFormat.PNG,100,stream);
        return Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);
    }
}
