package de.franzwegner.socketmulticam;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.hardware.Camera;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;

import android.os.StrictMode;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class MainActivity extends AppCompatActivity {

    EdlGenerator edl = null;

    WifiP2pManager manager;
    WifiP2pManager.Channel channel;
    WiFiDirectBroadcastReceiver receiver;
    IntentFilter intentFilter;


    private static final String LOG_TAG = "loglog";
    public static Camera mCamera;
    SocketServer server;
    SocketClient client;

    int counter = 0;

    //  public Camera mCamera;


    private Camera2Preview mPreview2 = null;

    private StreamDisplay streamDisplay = null;


    private boolean iAmTheClient = false;

    ImageView cam1Overlay;
    ImageView cam2Overlay;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen


        setContentView(R.layout.activity_main);

      AutoFitTextureView textureView = findViewById(R.id.texture);

   //   textureView.setEnabled(false);

        cam1Overlay = findViewById(R.id.cam1Overlay);
        cam1Overlay.setAlpha(0f);
        cam2Overlay = findViewById(R.id.cam2Overlay);
        cam2Overlay.setAlpha(0f);

        //set ip
        ((TextView) findViewById(R.id.myIp)).setText("My IP: " + getLocalIpAddress());


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        streamDisplay = new StreamDisplay(this);

        //wifi p2p stuff

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);
        receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);

        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

    }

    private String getLocalIpAddress() {
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

        return ip;
    }


    public void wifiP2pButton(View v) {
        String p2pDeviceName = ((EditText) findViewById(R.id.p2pDeviceToConnectTo)).getText().toString();
        receiver.setDeviceNameToLookFor(p2pDeviceName);
        discoverP();


    }


    public void discoverP() {
        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d("fff", "suc");


                //schick mal data an einen server


            }

            @Override
            public void onFailure(int reason) {
                Log.d("fff", "fail");
            }
        });
    }


    /**
     * A safe way to get an instance of the Camera object.
     */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }


    public void serverButton(View v) {
        server = new SocketServer(this, streamDisplay);
    }

    public void clientButton(View v) {
        String ipToConnectTo = ((EditText) findViewById(R.id.ipToConnectTo)).getText().toString();
        client = new SocketClient(this, ipToConnectTo);
        iAmTheClient = true;

    }

    public void fileButton(View v) {

        FileBroadcaster fB = new FileBroadcaster(client);

    }

    public void cameraPreviewButton(View v) {

/*        // Create an instance of Camera
        mCamera = getCameraInstance();

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
        mPreview.client = client;*/

        mPreview2 = new Camera2Preview(this);


        if (iAmTheClient) {
            client.setCamera2Preview(mPreview2);

            if (client != null) {
                mPreview2.setClient(client);
            }
        }


    }

    public void recordCamerasButton(View v) {

        if (mPreview2 != null) {
            edl = new EdlGenerator(2);

            mPreview2.createCameraPreview(2);
        }


        //   cameraRecord = new CameraRecord(mCamera);

        //set client camera
//        client.setCamera(mCamera);

        // TO DO when starting recording, socket preview freezes

        if (server != null && server.isConnectedToClient()) {
            server.writeString("startRecording");
        }


        //send message

    }

    public void stopRecordingsButton(View v) {

        //    cameraRecord.stopRecording();


        if (mPreview2 != null) {
            mPreview2.stopRecording();
            mPreview2.createCameraPreview(1);
            StringToFile fToF = new StringToFile(edl.generateEdl());
        }


        if (server != null && server.isConnectedToClient()) {
            server.writeString("stopRecording");
        }

//       AutoFitTextureView textureView = findViewById(R.id.texture);
//
//        textureView.setEnabled(false);
//
//        Log.d("texture", ""+ textureView.isEnabled());

    }

    public void messageToClientButton(View v) {

        server.writeString("what what" + counter);
        counter++;

    }

    /* register the broadcast receiver with the intent values to be matched */
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, intentFilter);
    }

    /* unregister the broadcast receiver */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }


    public void cameraPreviewClick(View v) {
        Log.d(LOG_TAG, "CAM 1");

        cam1Overlay.setAlpha(1f);
        cam2Overlay.setAlpha(0f);

        if (edl != null) {
            edl.handleCameraSwitch(1);
            TextView cutsInfo = findViewById(R.id.cutsInfo);
            cutsInfo.setText("Cuts recorded: " + edl.getAmuntOfCuts());
        }


    }

    public void clientPreviewClick(View v) {
        Log.d(LOG_TAG, "CAM 2");

        cam1Overlay.setAlpha(0f);
        cam2Overlay.setAlpha(1f);

        if (edl != null) {
            edl.handleCameraSwitch(2);
            TextView cutsInfo = findViewById(R.id.cutsInfo);
            cutsInfo.setText("Cuts recorded: " + edl.getAmuntOfCuts());
        }


    }

}
