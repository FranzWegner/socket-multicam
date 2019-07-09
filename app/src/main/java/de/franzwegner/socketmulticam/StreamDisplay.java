package de.franzwegner.socketmulticam;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

public class StreamDisplay extends Activity{

    private static final String TAG = "inputPreview";
    ImageView imageView;

    public StreamDisplay(
            Context context) {

        imageView = (ImageView) ((Activity) context).findViewById(R.id.streamImageView);
     //   imageView.setImageResource(R.drawable.bildd);

    }

    public void updateDisplay(final byte[] jpgData){


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Bitmap bmp= BitmapFactory.decodeByteArray(jpgData,0,jpgData.length);
                imageView.setImageBitmap(bmp);

              //  Log.d(TAG, "updated display");
            }
        });


    }


}
