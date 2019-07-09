package de.franzwegner.socketmulticam;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import java.net.Socket;

public class WiFiDirectBroadcastReceiver extends BroadcastReceiver implements WifiP2pManager.ConnectionInfoListener {

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private MainActivity myActivity;
    private String deviceNameToLookFor;


    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, MainActivity activity) {
        super();
        this.mManager = manager;
        this.mChannel = channel;
        this.myActivity = activity;
    }


    public void setDeviceNameToLookFor(String deviceNameToLookFor) {
        this.deviceNameToLookFor = deviceNameToLookFor;
    }

    Socket socket = new Socket();

    @Override
    public void onReceive(Context context, final Intent intent) {

        String action = intent.getAction();
        WifiP2pManager.PeerListListener myPeerListListener = new WifiP2pManager.PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList peers) {
                //Log.d("fff", peers.toString());
                for (final WifiP2pDevice device : peers.getDeviceList()) {
                    // Log.d("fff", device.deviceName);

                //    deviceNameToLookFor = "Poco";

                    if (device.deviceName.equals(deviceNameToLookFor)) {

                        // WifiP2pDevice pocoDevice;
                        WifiP2pConfig config = new WifiP2pConfig();
                        config.deviceAddress = device.deviceAddress;


                        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
                            @Override
                            public void onSuccess() {

                                Log.d("aaa", "Connected to " + deviceNameToLookFor);
//                                  String host = intent.getExtras().getString("go host");
//                                 Log.d("fff", host);

                                // device = Poco
                                 if (device.isGroupOwner()){
                                  //   Log.d("grouOwner", "Poco ist Group Owner");
                                 } else {
                                 //    Log.d("grouOwner", "Poco ist NICHT Group Owner");
                                 }


                            }

                            @Override
                            public void onFailure(int reason) {

                            }
                        });
                    }
                }
            }
        };

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Check to see if Wi-Fi is enabled and notify appropriate activity
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                //p2p enabled
                Log.d("fff", "p2p enabled");
            } else {
                // p2p not enabled
                Log.d("fff", "p2p NOT enabled");
            }


        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Call WifiP2pManager.requestPeers() to get a list of current peers
            if (mManager != null) {
                mManager.requestPeers(mChannel, myPeerListListener);
            }


        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Respond to new connection or disconnections
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
        }

    }

    @Override
    public void onConnectionInfoAvailable(final WifiP2pInfo info) {

        // InetAddress from WifiP2pInfo struct.
        String groupOwnerAddress = info.groupOwnerAddress.getHostAddress();
        Log.d("aaa", groupOwnerAddress);


        // After the group negotiation, we can determine the group owner
        // (server).
        if (info.groupFormed && info.isGroupOwner) {
            // Do whatever tasks are specific to the group owner.
            // One common case is creating a group owner thread and accepting
            // incoming connections.
        } else if (info.groupFormed) {
            // The other device acts as the peer (client). In this case,
            // you'll want to create a peer thread that connects
            // to the group owner.
        }
    }
}
