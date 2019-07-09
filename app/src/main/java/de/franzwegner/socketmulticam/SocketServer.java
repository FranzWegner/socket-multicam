package de.franzwegner.socketmulticam;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SocketServer {


    boolean waitingForIncomingConnections = false;
    boolean connectedToClient = false;

    ServerSocket serverSocket;
    static final int myPort = 6666;
    final String TAG = "hello";
    DataInputStream inputStream;
    Socket client;
    StreamDisplay streamDisplay;

    Context context;

    PrintWriter outputStream;

    public SocketServer(Context context, StreamDisplay sDisplay) {

       this.context = context;
        streamDisplay = sDisplay;
        Thread socketServerThread = new Thread(new SocketServerThread());
        socketServerThread.start();
    }

    public SocketServer() {

        Thread socketServerThread = new Thread(new SocketServerThread());
        socketServerThread.start();
    }

    public void setWaitingForIncomingConnections(boolean waitingForIncomingConnections) {
        this.waitingForIncomingConnections = waitingForIncomingConnections;
    }

    public boolean isWaitingForIncomingConnections() {
        return waitingForIncomingConnections;
    }

    public boolean isConnectedToClient() {
        return connectedToClient;
    }

    private class SocketServerThread implements Runnable {

        @Override
        public void run() {
            // super.run();
            Log.d(TAG, "trying now to create a server Socket");

            try {
                serverSocket = new ServerSocket(myPort);


                Log.d(TAG, "Waiting for incoming connection, my IP is " + serverSocket.getInetAddress());
                setWaitingForIncomingConnections(true);


                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView serverInfo = (TextView) ((Activity) context).findViewById(R.id.serverInfo);
                        serverInfo.setText("Server running");
                    }
                });

                client = serverSocket.accept();

                connectedToClient = true;


                outputStream = new PrintWriter(client.getOutputStream(),true);


                Log.d(TAG, "Connected to " + client.getInetAddress());



                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView connectionInfo = (TextView) ((Activity) context).findViewById(R.id.connectionInfo);
                        connectionInfo.setText("Connected to " + client.getInetAddress() + " - I am the Server.");
                    }
                });

//                TextViewChanger textViewChanger = new TextViewChanger(context);
//                textViewChanger.changeText("connectionInfo", "BlaBla");

                while (true) {

                    // Log.d(TAG, "Reading inputstream....");



                    //display on StreamDisplay

                 //   writeString("ICH MELDE MICH!!!");


                    //check if needed


                    List<Object> typeAndData = new ArrayList<Object>();
                    typeAndData = readAndGetByteArray();

                    // it's is preview frame
                    if ((int) typeAndData.get(0) == 0){
                        streamDisplay.updateDisplay((byte[]) typeAndData.get(1));
                    } else {
                        // is has to be data, that should be saved
                        saveByteArrayOnStorage((byte[]) typeAndData.get(1));
                    }



                }


            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void saveByteArrayOnStorage(byte[] bytes) {

        Log.d(TAG, "START FILE SAVING");

        File photo=new File(Environment.getExternalStorageDirectory(), "video.mp4");

        if (photo.exists()) {
            photo.delete();
        }

        try {
            FileOutputStream fos=new FileOutputStream(photo.getPath());

            fos.write(bytes);
            fos.close();
            Log.d(TAG, "DONE WITH SAVING FILE");
        }
        catch (java.io.IOException e) {
            Log.e("PictureDemo", "Exception in photoCallback", e);
        }

    }

    public void writeString(String message) {

        outputStream.write(message + "\r\n");
        outputStream.flush();

    }

    private List<Object> readAndGetByteArray() {
        int arrayLength = 0;
        byte[] resultArray = null;
        List<Object> resultList = new ArrayList<Object>();
        try {
            inputStream = new DataInputStream(client.getInputStream());

            //0 = preview frame, 1 = data file
           int type = inputStream.readInt();

            arrayLength = inputStream.readInt();

            resultArray = new byte[arrayLength];

            inputStream.readFully(resultArray, 0, arrayLength);
            System.gc();

         //   Log.d(TAG, Arrays.toString(resultArray));


            resultList.add(type);
            resultList.add(resultArray);

        } catch (IOException e) {
            e.printStackTrace();
        }



        return resultList;

    }

    private void readData() {

        try {
            inputStream = new DataInputStream(client.getInputStream());

            String recData = inputStream.readUTF();

            Log.d(TAG, recData);


        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
