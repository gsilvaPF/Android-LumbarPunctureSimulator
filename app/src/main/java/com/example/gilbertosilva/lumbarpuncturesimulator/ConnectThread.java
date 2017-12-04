package com.example.gilbertosilva.lumbarpuncturesimulator;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.os.ParcelUuid;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

import static android.content.ContentValues.TAG;

/**
 * Created by gilbertosilva on 11/8/17.
 */

public class ConnectThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private BluetoothAdapter mBlueToothAdapter;
    private SimulationActivity mSimulationActivity;

    ConnectThread(BluetoothDevice device) {
        // Use a temporary object that is later assigned to mmSocket
        // because mmSocket is final.
        BluetoothSocket tmp = null;
        mmDevice = device;
        ParcelUuid[] uuids = mmDevice.getUuids();
        UUID uuid = null;

        if(uuids.length > 0){

            uuid = uuids[0].getUuid();

        }

        try {
            // Get a BluetoothSocket to connect with the given BluetoothDevice.
            // MY_UUID is the app's UUID string, also used in the server code.
            tmp = device.createRfcommSocketToServiceRecord(uuid);
        } catch (IOException e) {
            Log.e(TAG, "Socket's create() method failed", e);
        }
        mmSocket = tmp;
    }

    @Override
    public void run() {
        // Cancel discovery because it otherwise slows down the connection.
        mBlueToothAdapter.cancelDiscovery();

        try {
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            Log.d(TAG, "Trying to connect to socket.");

            mmSocket.connect();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and return.
            Log.e(TAG, "Could not connect to client socket");
            try {
                Log.e(TAG, "Trying to close the client socket");
                mmSocket.close();
            } catch (IOException closeException) {
                Log.e(TAG, "Could not close the client socket", closeException);
            }
            return;
        }

        // The connection attempt succeeded. Perform work associated with
        // the connection in a separate thread.
        mSimulationActivity.manageMyConnectedSocket(mmSocket);
    }

    // Closes the client socket and causes the thread to finish.
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not close the client socket", e);
        }
    }

    void setUpThread(BluetoothAdapter bluetoothAdapter, SimulationActivity simulationActivity){

        mBlueToothAdapter = bluetoothAdapter;
        mSimulationActivity = simulationActivity;

    }

}
