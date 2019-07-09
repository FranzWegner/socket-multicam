package de.franzwegner.socketmulticam;

import android.graphics.Rect;
import android.graphics.YuvImage;

import java.io.ByteArrayOutputStream;

public class ConverterHelper {


    public ConverterHelper() {
        // nothing
    }


    public byte[] compressToJpg(byte[] data, int previewFormat, int width, int height) {

        YuvImage yuv_image = new YuvImage(data, previewFormat, width, height, null); // all bytes are in YUV format therefore to use the YUV helper functions we are putting in a YUV object
        Rect rect = new Rect(0, 0, width, height);
        ByteArrayOutputStream output_stream = new ByteArrayOutputStream();
        yuv_image.compressToJpeg(rect, 20, output_stream);// image has now been converted to the jpg format and bytes have been written to the output_stream object

        byte[] tmp = output_stream.toByteArray();//getting the byte array

        return tmp;
    }
}
