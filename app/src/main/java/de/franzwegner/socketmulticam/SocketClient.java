package de.franzwegner.socketmulticam;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketClient {


    Camera mCamera;
    Socket toServerSocket;
    static final int myPort = 6666;
    final String TAG = "hello";
    DataOutputStream outputStream = null;
    String host;
    Camera2Preview camera2Preview = null;
    Context context;


    BufferedReader inputStream = null;
    //   BufferedReader read = new BufferedReader(new InputStreamReader(System.in));


    private HandlerThread streamReaderThread;
    private Handler streamReaderHandler;


    public SocketClient(Context context, String host) {
        this.context = context;
        this.host = host;
        Thread socketClientThread = new Thread(new SocketClientThread());
        socketClientThread.start();
    }

    public void setCamera2Preview(Camera2Preview camera2Preview) {
        this.camera2Preview = camera2Preview;
    }

    public void setCamera(Camera cam) {
        mCamera = cam;
    }

    private class SocketClientThread implements Runnable {

        @Override
        public void run() {


            streamReaderThread = new HandlerThread("ReaderBackground");
            streamReaderThread.start();
            streamReaderHandler = new Handler(streamReaderThread.getLooper());

            Log.d(TAG, "Now trying to connect to server...");

            try {
                toServerSocket = new Socket(host, myPort);

                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView connectionInfo = (TextView) ((Activity) context).findViewById(R.id.connectionInfo);
                        connectionInfo.setText("Connected to " + toServerSocket.getInetAddress() + " - I am the Client.");
                    }
                });

                outputStream = new DataOutputStream(toServerSocket.getOutputStream());
                inputStream = new BufferedReader(new InputStreamReader(toServerSocket.getInputStream()));

                readString();

                // writeData();

            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

    private void readString() throws IOException {

        String serverResponse = null;
        while ((serverResponse = inputStream.readLine()) != null) {
            Log.d("Servernachricht", serverResponse);

            final String finalServerResponse = serverResponse;
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (finalServerResponse.equals("startRecording")) {

                        //get Camera

                        if (camera2Preview != null) {

                            //either use new handler, or use handler from camera2


                            camera2Preview.createCameraPreview(2);


                        }


                    }

                    if (finalServerResponse.equals("stopRecording")) {
                        if (camera2Preview != null) {
                            camera2Preview.stopRecording();
                            camera2Preview.createCameraPreview(1);
                        }
                    }


                }
            });

        }

    }

    private void writeData() {

        try {
            outputStream.writeUTF("Ich bin ein Test!");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void writeByteArray(byte[] byteArray, int type) {
        try {
            //type of next data 0 = preview frame, 1 = file
            outputStream.writeInt(type);

            outputStream.writeInt(byteArray.length);
            outputStream.write(byteArray);
            outputStream.flush();
            System.gc();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
