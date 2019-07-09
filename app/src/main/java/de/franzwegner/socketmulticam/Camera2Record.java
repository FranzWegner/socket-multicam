package de.franzwegner.socketmulticam;

import android.hardware.Camera;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Camera2Record {


    MediaRecorder mediaRecorder;
    private final String TAG = "ich";
    private boolean isRecording = false;
    private Surface mediaRecorderSurface;
    private boolean isVideoRecorderPrepared = false;


    public Camera2Record() {

        mediaRecorder = new MediaRecorder();


        prepareVideoRecorder();

    }

    public void startRecording(){
        Thread cameraRecordThread = new Thread(new CameraRecordThread());
        cameraRecordThread.start();
    }

    public Surface getMediaRecorderSurface() {

        return mediaRecorderSurface;
    }

    private void releaseMediaRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.reset();   // clear recorder configuration
            mediaRecorder.release(); // release the recorder object
            mediaRecorder = null;
                    // lock camera for later use
        }
    }


    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    /**
     * Create a file Uri for saving an image or video
     */
    private static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * Create a File for saving an image or video
     */
    private static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "SocketMulticam");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    private boolean prepareVideoRecorder() {

        // Step 1: Unlock and set camera to MediaRecorder



        // Step 2: Set sources
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);

        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
  //      mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
        mediaRecorder.setVideoSize(640,480);
        mediaRecorder.setVideoEncodingBitRate(4194304);
        mediaRecorder.setAudioEncodingBitRate(131072);

        // Step 4: Set output file
        mediaRecorder.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO).toString());

        // Step 5: Set the preview output
        //  mediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());

        //set output params




        // Step 6: Prepare configured MediaRecorder
        try {

           //debug here
            mediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }

        isVideoRecorderPrepared = true;


        mediaRecorderSurface = mediaRecorder.getSurface();

        return true;
    }

    public void stopRecording() {
        if (isRecording) {
            // stop recording and release camera
            mediaRecorder.stop();  // stop the recording
            releaseMediaRecorder(); // release the MediaRecorder object


            // inform the user that recording has stopped
            Log.d(TAG, "Stopping recording now");
            isRecording = false;
        }
    }

    private class CameraRecordThread implements Runnable {
        @Override
        public void run() {

            // initialize video camera
            if (isVideoRecorderPrepared) {
                // Camera is available and unlocked, MediaRecorder is prepared,
                // now you can start recording
                mediaRecorder.start();



                // inform the user that recording has started
                //       setCaptureButtonText("Stop");
                Log.d(TAG, "Started video recording");
                isRecording = true;
            } else {
                // prepare didn't work, release the camera
                releaseMediaRecorder();
                // inform user
                Log.d(TAG, "Preparing didn't work");

            }

        }
    }
}
