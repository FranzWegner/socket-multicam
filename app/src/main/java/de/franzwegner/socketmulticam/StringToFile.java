package de.franzwegner.socketmulticam;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StringToFile {

    String s;
    File file;

    public StringToFile(String s){

        this.s = s;

        writeFile();

    }

    private void writeFile() {

        File StorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "SocketMulticam");

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        File cutFile = new File(StorageDir.getPath() + File.separator +
                "CUTS_" + timeStamp + ".txt");


        try {
            BufferedWriter fileWriter = new BufferedWriter(new FileWriter(cutFile));
            fileWriter.write(s);
            fileWriter.close();

            Log.d("fileWriter", "Cuts written to a file");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
