package de.franzwegner.socketmulticam;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

public class EdlGenerator {

    long startTime;
    long endOfLastEdlClip = 0;
    ArrayList<ArrayList<Long>> cuts = new ArrayList<>();

    int amountOfCams;

    public EdlGenerator(int amountOfCams) {

        this.amountOfCams = amountOfCams;
        startTime = System.currentTimeMillis();
        addCut(0, startTime);
        printCuts();

    }

    public int getAmuntOfCuts(){


        return cuts.size() - 1;
    }

    public void handleCameraSwitch(int camNumber) {

        addCut(camNumber, System.currentTimeMillis());

    }

    private void addCut(int camNumber, long timestamp) {

        ArrayList<Long> cut = new ArrayList<>();
        cut.add((long) camNumber);
        cut.add(timestamp);


        cuts.add(cut);

        //  Log.d("EDL", generateEdl());


    }

    public void printCuts() {
        Log.d("cuts", Arrays.toString(cuts.toArray()));
    }

    public void printEdl() {
        Log.d("cuts", generateEdl());
    }

    private long msToFrames(long ms) {
        return Math.round((ms * 50) / 1000);
    }

    private long edlFrames(long frames) {

        return frames * 20;

    }

    public String generateEdl() {

        String finalString = "\"ID\";\"Track\";\"StartTime\";\"Length\";\"PlayRate\";\"Locked\";\"Normalized\";\"StretchMethod\";\"Looped\";\"OnRuler\";\"MediaType\";\"FileName\";\"Stream\";\"StreamStart\";\"StreamLength\";\"FadeTimeIn\";\"FadeTimeOut\";\"SustainGain\";\"CurveIn\";\"GainIn\";\"CurveOut\";\"GainOut\";\"Layer\";\"Color\";\"CurveInR\";\"CurveOutR\";\"PlayPitch\";\"LockPitch\";\"FirstChannel\";\"Channels\"";


        for (int i = 1; i < cuts.size() - 1; i++) {

            long startStamp = cuts.get(i).get(1) - cuts.get(0).get(1);
            long length = cuts.get(i+1).get(1) - cuts.get(i).get(1);

            long edlStartStamp = edlFrames(msToFrames(startStamp));
            long edlLength = edlFrames(msToFrames(length));

            if (edlStartStamp != endOfLastEdlClip){

                edlStartStamp = endOfLastEdlClip;

            }

            long edlEndTime = edlStartStamp + edlLength;


            finalString += "\n";
            finalString += i;
            finalString += "; 1; ";
            finalString += edlStartStamp + ".0000; " + edlLength + ".0000";
            finalString += "; 1.000000; FALSE; FALSE; 0; TRUE; FALSE; VIDEO; ";
            finalString += "U:\\Cam" + cuts.get(i).get(0) + ".MTS; 0; ";
            finalString += edlStartStamp + ".0000; " + edlLength + ".0000";
            finalString += "; 0.0000; 0.0000; 1.000000; 4; 0.000000; 4; 0.000000; 0; -1; 4; 4; 0.000000; FALSE; 0; 0";

            endOfLastEdlClip = edlEndTime;



        }


        return finalString;

    }

}
