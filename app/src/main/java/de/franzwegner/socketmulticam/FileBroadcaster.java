package de.franzwegner.socketmulticam;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileBroadcaster {

  //  DataInputStream inputStream;
    DataOutputStream outputStream;
    String filename;
    File file;
    Context context;
    private String TAG = "fileBroadcaster";
    SocketClient socketClient;


    public FileBroadcaster(SocketClient socketClient) {
        this.socketClient = socketClient;


        // DataOutputStream out, String fileN, Context context
      //  outputStream = out;
       // filename = fileN;
       // this.context = context;

        Thread fileBroadcasterThread = new Thread(new FileBroadcasterThread());
        fileBroadcasterThread.start();

    }


    private class FileBroadcasterThread implements Runnable {
        @Override
        public void run() {

            File f = new File(Environment.getExternalStorageDirectory().toString()+"/Download/video.mp4");
            Log.d(TAG, f.getAbsolutePath());
            socketClient.writeByteArray(fileToByteArray(f), 1);

        }
    }

    private byte[]fileToByteArray(File f) {

        byte[] bytesArray = new byte[(int) f.length()];

        try {
            FileInputStream fis = new FileInputStream(f);

            //TO DO read chunk of file stream, send it directly to outputstream, type 2 = chunk data

            fis.read(bytesArray); //read file into bytes[]
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bytesArray;
    }
}
