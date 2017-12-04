package com.example.gilbertosilva.lumbarpuncturesimulator;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by gilbertosilva on 11/8/17.
 */

public class SimulationBroadCastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        switch (action){

            case BluetoothDevice.ACTION_FOUND:{

                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address

                break;

            }

            case BluetoothDevice.ACTION_ACL_CONNECTED:{



                break;

            }

            case BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED:{



                break;

            }


            case BluetoothDevice.ACTION_BOND_STATE_CHANGED:{



                break;

            }

        }



        Toast.makeText(context, "Received Toast", Toast.LENGTH_SHORT).show();
    }
}
