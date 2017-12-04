package com.example.gilbertosilva.lumbarpuncturesimulator;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.DataSetObserver;
import android.icu.text.UnicodeSetSpanner;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

import static android.content.ContentValues.TAG;
import static com.example.gilbertosilva.lumbarpuncturesimulator.MainActivity.REQUEST_ENABLE_BT;

public class SimulationActivity extends AppCompatActivity implements Simulation.SimulationHandler {

    private Context mContext;
    private SimulationArrayAdapter mArrayAdapter;
    private ListView mListView;
    private ArrayList<SimulationAlert> mArrayList;
    private Simulation mSimulation;
    private SimulationStage mSimulationCurrentStage;
    private BluetoothAdapter mBlueToothAdapter;
    private BluetoothDevice mBlueToothDevice;
    private ArrayList<BluetoothDevice> mArrayListBlueToothDevices = new ArrayList<>();
    private Handler mHandler; // handler that gets info from Bluetooth service
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;

    private int mSimulationMode;
    private int mDepth;
    private int mXAngle;
    private int mYAngle;
    private int mZAngle;
    private boolean mSimulationStarted;
    private boolean mNewMessage;
    private String mMessage;

    private TextView mSimulationStepTitle;
    private TextView mSimulationStepMessage;

    private ProgressBar mSimulationProgressBar;
    private TextView mSimulationProgressTextView;
    private TextView mSimulationDepthTextView;
    private TextView mSimulationXAxisTextView;
    private TextView mSimulationYAxisTextView;
    private TextView mSimulationZAxisTextView;

    public final static int OFFLINE_MODE = 0;
    public final static int GUIDED_MODE = 1;
    public final static int PRACTICE_MODE = 2;
    public final static int TEST_MODE = 3;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if(action != null){

                switch (action){

                    case BluetoothAdapter.ACTION_DISCOVERY_STARTED:{

                        Toast.makeText(mContext, "Started Discovery!", Toast.LENGTH_SHORT).show();

                        if(mArrayListBlueToothDevices != null){

                            mArrayListBlueToothDevices.clear();

                        }

                        break;

                    }

                    case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:{

                        Toast.makeText(mContext, "Finished Discovery!", Toast.LENGTH_SHORT).show();

                        if(mContext != null){

                            selectDevice();

                        }

                        break;

                    }

                    case BluetoothDevice.ACTION_FOUND:{

                        Toast.makeText(mContext, "Found Device!", Toast.LENGTH_SHORT).show();

                        // Discovery has found a device. Get the BluetoothDevice
                        // object and its info from the Intent.
                        BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        String deviceName = bluetoothDevice.getName();
                        String deviceHardwareAddress = bluetoothDevice.getAddress(); // MAC address

                        if(mArrayListBlueToothDevices != null){

                            mArrayListBlueToothDevices.add(bluetoothDevice);

                        }

                        break;

                    }

                    case BluetoothDevice.ACTION_ACL_CONNECTED:{

                        checkState();

                        break;

                    }

                    case BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED:{

                        checkState();

                        break;

                    }


                    case BluetoothDevice.ACTION_BOND_STATE_CHANGED:{

                        checkState();

                        break;

                    }

                }

            }

        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulation);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null){

            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);

        }

        Intent intent = getIntent();
        String type = Simulation.SIMULATION_GUIDED;

        if(intent != null){

            type = intent.getStringExtra("type");

        }

        mContext = this;
        mSimulationMode = SimulationActivity.OFFLINE_MODE;
        mSimulationStarted = false;
        mNewMessage = true;
        mMessage = "";
        mDepth = 0;
        mXAngle = 0;
        mYAngle = 0;
        mZAngle = 0;


        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_LOCAL_NAME_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        filter.addAction(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_CLASS_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_NAME_CHANGED);
//        filter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
        filter.addAction(BluetoothDevice.ACTION_UUID);
        this.registerReceiver(mBroadcastReceiver, filter);

        mListView = findViewById(R.id.listView);
        mSimulationProgressBar = findViewById(R.id.simulationActivityBottomFragmentProgressBar);
        mSimulationProgressTextView = findViewById(R.id.simulationActivityBottomFragmentProgressTextView);
        mSimulationDepthTextView = findViewById(R.id.simulationActivityBottomFragmentDepthTextView);
        mSimulationXAxisTextView = findViewById(R.id.simulationActivityBottomFragmentXAxisTextView);
        mSimulationYAxisTextView = findViewById(R.id.simulationActivityBottomFragmentYAxisTextView);
        mSimulationZAxisTextView = findViewById(R.id.simulationActivityBottomFragmentZAxisTextView);
        mSimulationStepTitle = findViewById(R.id.simulationActivityTopFragmentStepTextView);
        mSimulationStepMessage = findViewById(R.id.simulationActivityTopFragmentStepMessageTextView);
        LinearLayout topLinearLayout = findViewById(R.id.simulationActivityTopLinearLayout);

        topLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDevice();
            }
        });

        mSimulation = new Simulation(type, this);
        mSimulationCurrentStage = mSimulation.getCurrentStage();
        mArrayList = mSimulationCurrentStage.getAlerts();
        mArrayAdapter = new SimulationArrayAdapter(mContext,
                R.layout.simulation_cell, mArrayList);
        mListView.setAdapter(mArrayAdapter);

        switch (type){

            case Simulation.SIMULATION_GUIDED:{

                mSimulationMode = SimulationActivity.GUIDED_MODE;
                SimulationAlert simulationAlert;
                simulationAlert = new SimulationAlert(SimulationAlert.SUCCESS,"",new Date());
                mArrayList.add(simulationAlert);
                simulationAlert = new SimulationAlert(SimulationAlert.WARNING,"",new Date());
                mArrayList.add(simulationAlert);
                simulationAlert = new SimulationAlert(SimulationAlert.ERROR,"",new Date());
                mArrayList.add(simulationAlert);
                simulationAlert = new SimulationAlert(SimulationAlert.WARNING,"",new Date());
                mArrayList.add(simulationAlert);
                simulationAlert = new SimulationAlert(SimulationAlert.SUCCESS,"",new Date());
                mArrayList.add(simulationAlert);
                simulationAlert = new SimulationAlert(SimulationAlert.WARNING,"",new Date());
                mArrayList.add(simulationAlert);
                simulationAlert = new SimulationAlert(SimulationAlert.ERROR,"",new Date());
                mArrayList.add(simulationAlert);
                simulationAlert = new SimulationAlert(SimulationAlert.WARNING,"",new Date());
                mArrayList.add(simulationAlert);
                simulationAlert = new SimulationAlert(SimulationAlert.SUCCESS,"",new Date());
                mArrayList.add(simulationAlert);
                simulationAlert = new SimulationAlert(SimulationAlert.WARNING,"",new Date());
                mArrayList.add(simulationAlert);
                simulationAlert = new SimulationAlert(SimulationAlert.ERROR,"",new Date());
                mArrayList.add(simulationAlert);
                simulationAlert = new SimulationAlert(SimulationAlert.WARNING,"",new Date());
                mArrayList.add(simulationAlert);
                mArrayAdapter.notifyDataSetChanged();

                break;

            }

            case Simulation.SIMULATION_PRACTICE:{

                mSimulationMode = SimulationActivity.PRACTICE_MODE;
                topLinearLayout.setVisibility(View.GONE);
                SimulationAlert simulationAlert;
                simulationAlert = new SimulationAlert(SimulationAlert.WARNING,"",new Date());
                mArrayList.add(simulationAlert);
                mArrayAdapter.notifyDataSetChanged();

                break;

            }

            case Simulation.SIMULATION_TEST:{

                mSimulationMode = SimulationActivity.TEST_MODE;
                topLinearLayout.setVisibility(View.GONE);

                break;

            }

        }

        mBlueToothAdapter = BluetoothAdapter.getDefaultAdapter();
        mHandler = new Handler(Looper.getMainLooper()){

          @Override
          public void handleMessage(Message inputMessage) {
              // Gets the image task from the incoming Message object.

              Log.d(TAG, "Message received.!");

              int type = inputMessage.what;
             switch (type){

                 case ConnectedThread.MESSAGE_READ:{

                     byte[] data = (byte[]) inputMessage.obj;

                     try {
                         String message = new String(data, "US-ASCII");

//                         Log.d(TAG, "message = " + message);

                     } catch (UnsupportedEncodingException e) {
                         e.printStackTrace();
                         Toast.makeText(mContext, R.string.error_invalid_charset_format, Toast.LENGTH_SHORT).show();
                     }

                     break;

                 }

                 case ConnectedThread.MESSAGE_TOAST:{

                     break;

                 }

                 case ConnectedThread.MESSAGE_WRITE:{

                     break;

                 }
             }
          }

        };






//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

//        if (findViewById(R.id.simulationActivityTopFragmentStepMessageTextView) != null) {
//
//            // However, if we're being restored from a previous state,
//            // then we don't need to do anything and should return or else
//            // we could end up with overlapping fragments.
//            if (savedInstanceState != null) {
//                return;
//            }
//
//            // Create a new Fragment to be placed in the activity layout
//            SimulationTopFragment topFragment = new SimulationTopFragment();
//
//            // In case this activity was started with special instructions from an
//            // Intent, pass the Intent's extras to the fragment as arguments
////            firstFragment.setArguments(getIntent().getExtras());
//
//            // Add the fragment to the 'fragment_container' FrameLayout
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.simulationActivityTopFragmentStepMessageTextView, topFragment).commit();
//        }

//        if (findViewById(R.id.simulationActivityBottomFragmentProgressBar) != null) {
//
//            // However, if we're being restored from a previous state,
//            // then we don't need to do anything and should return or else
//            // we could end up with overlapping fragments.
//            if (savedInstanceState != null) {
//                return;
//            }
//
//            // Create a new Fragment to be placed in the activity layout
//            SimulationBottomFragment bottomFragment = new SimulationBottomFragment();
//
//            // In case this activity was started with special instructions from an
//            // Intent, pass the Intent's extras to the fragment as arguments
////            firstFragment.setArguments(getIntent().getExtras());
//
//            // Add the fragment to the 'fragment_container' FrameLayout
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.simulationActivityBottomFragmentProgressBar, bottomFragment).commit();
//        }

    }

    @Override
    public void onActivityResult(int requestCode,
                                 int resultCode,
                                 Intent data){
//        super.onActivityResult(requestCode,resultCode,data);

        if(resultCode == RESULT_OK){

            checkState();

        }
        else if(resultCode == RESULT_CANCELED){

            Toast.makeText(mContext, R.string.error_could_not_enable_bluetooth, Toast.LENGTH_LONG).show();

        }

    }

    @Override
    public void onResume(){
        super.onResume();

        checkState();

    }

    @Override
    public void onPause(){
        super.onPause();

        if(mBlueToothAdapter != null){

            mBlueToothAdapter.cancelDiscovery();

        }

    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        unregisterReceiver(mBroadcastReceiver);

    }

    public void checkState(){

        if(mBlueToothAdapter == null){

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle(R.string.error)
                    .setMessage(R.string.error_device_does_not_support_bluetooth_message)
                    .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();


        }
        else{

            int state = mBlueToothAdapter.getState();

            switch (state){

                case BluetoothAdapter.STATE_ON:{

                    Toast.makeText(mContext, R.string.bluetoothS_state_on, Toast.LENGTH_SHORT).show();

                    if(mBlueToothDevice == null){

                        Set<BluetoothDevice> bondedDevices = mBlueToothAdapter.getBondedDevices();

                        if(bondedDevices != null){

                            mArrayListBlueToothDevices.clear();
                            mArrayListBlueToothDevices.addAll(bondedDevices);

                            if(mArrayListBlueToothDevices.isEmpty()){

                                Toast.makeText(mContext, R.string.error_bluetooth_no_devices_connected, Toast.LENGTH_SHORT).show();

//                                mBlueToothAdapter.startDiscovery();

                            }
                            else{

                                if(mArrayListBlueToothDevices.size() == 1){

                                    mBlueToothDevice = mArrayListBlueToothDevices.get(0);
                                    connectToDevice();
                                    Toast.makeText(mContext, "Connected to: " + mBlueToothDevice.getName(), Toast.LENGTH_SHORT).show();

                                }
                                else{

                                    selectDevice();

                                }

                            }


                        }
                        else{

                            Toast.makeText(mContext, R.string.error_retreiving_bonded_devices, Toast.LENGTH_SHORT).show();
                            finish();

                        }

                    }
                    else{



                    }

                    break;

                }

                case BluetoothAdapter.STATE_TURNING_ON:{

                    break;

                }

                case BluetoothAdapter.STATE_OFF:{

                    if(mSimulationStarted){

                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setTitle(R.string.error_bluetooth_disconnected_title)
                                .setMessage(R.string.error_bluetooth_disconnected_message)
                                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                })
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();

                    }
                    else{

                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

                    }

                    break;

                }

                case BluetoothAdapter.STATE_TURNING_OFF:{

                    break;

                }

            }

        }

    }

    private void selectDevice(){

        String[] names = new String[mArrayListBlueToothDevices.size()];
        String name;
        int index = 0;

//        for(int i = 0; i < names.length; i++){
//
//            name = "name: " + i;
//            names[i] = name;
//
//        }

        for(BluetoothDevice device: mArrayListBlueToothDevices){

            name = device.getName();
            names[index] = name;
            index++;

        }

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setSingleChoiceItems(names, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String name;
                int index = 0;

                for(BluetoothDevice device: mArrayListBlueToothDevices){

                    if(which == index){

                        mBlueToothDevice = device;
                        Toast.makeText(mContext, mBlueToothDevice.getName(), Toast.LENGTH_SHORT).show();

                    }

                    index++;

                }

//                String name = "";
//
//                for(int i = 0; i < names.length; i++){
//
//                    if(i == which){
//
//                        name = names[i].toString();
//
//                    }
//
//                }
//
//                Toast.makeText(mContext, which+"", Toast.LENGTH_SHORT).show();

            }
        })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        connectToDevice();
                        dialog.dismiss();

                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void connectToDevice(){

        Toast.makeText(mContext, "here", Toast.LENGTH_SHORT).show();
        Log.v("SimulationActivity","Connec to device here");

        if(mBlueToothDevice != null){

            Toast.makeText(mContext, "Bluetooth non-null", Toast.LENGTH_SHORT).show();
            Log.v("SimulationActivity","Bluetooth non-null");

            if(mConnectThread == null){

                Toast.makeText(mContext, "Trying to connect", Toast.LENGTH_SHORT).show();
                Log.v("SimulationActivity","Trying to connect");

                mConnectThread = new ConnectThread(mBlueToothDevice);
                mConnectThread.setUpThread(mBlueToothAdapter, this);
                mConnectThread.start();

            }
//            else{
//
//                Toast.makeText(mContext, "ConnectThread non-null", Toast.LENGTH_SHORT).show();
//                Log.v("SimulationActivity","ConnectThread non-null");
//
//                if(!mConnectThread.isAlive()){
//
//                    mConnectThread.setUpThread(mBlueToothAdapter, this);
//                    mConnectThread.start();
//
//                }
//
//            }

        }

    }

    public void manageMyConnectedSocket(BluetoothSocket bluetoothSocket){

        if(mConnectedThread == null){

            mConnectedThread = new ConnectedThread(bluetoothSocket);
            mConnectedThread.setUpThread(mHandler);
            mConnectedThread.start();
            byte[] data = new byte[1];
            char ch = (char) (mSimulationMode + 48);
            data[0] = (byte) ch;
            mConnectedThread.write(data);

        }
//        else{
//
//            if(!mConnectedThread.isAlive()){
//
//                mConnectedThread.setUpThread(mHandler);
//                mConnectedThread.start();
//
//            }
//
//        }

    }

    private void createAlert(String message){

        switch(message){

            case "a":{

               break;

            }

        }

    }

    private void setSimulationProgress(int progress){

        if(progress < 0){

            progress = 0;

        }

        if(progress > 100){

            progress = 100;

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mSimulationProgressBar.setProgress(progress, true);
        }
        else {
            mSimulationProgressBar.setProgress(progress);
        }

        mSimulationProgressTextView.setText(progress + "%");

    }

    private void setDepth(int depth){

        if(depth < 0){

            depth = 0;

        }

    }

    private void setXAngle(int angle){

        if(angle < 0){

            angle = 0;

        }

        mXAngle = angle;

    }

    private void setYAngle(int angle){

        if(angle < 0){

            angle = 0;

        }

        mYAngle = angle;

    }

    private void setZAngle(int angle){

        if(angle < 0){

            angle = 0;

        }

        mZAngle = angle;

    }

    private int getXAngle(){

        return mXAngle;

    }

    private int getYAngle(){

        return mYAngle;

    }

    private int getZAngle(int angle){

        return mZAngle;

    }

    private void updateSpatialDisplay(){

        mSimulationDepthTextView.setText("" + mDepth);
        mSimulationXAxisTextView.setText("" + mXAngle);
        mSimulationYAxisTextView.setText("" + mYAngle);
        mSimulationZAxisTextView.setText("" + mZAngle);

    }

    private void setStage(SimulationStage stage){

        mSimulationCurrentStage = stage;

        int stageNumber = stage.getStageNumber();

        switch (stageNumber){

            case 1:{

                break;

            }

            case 2:{

                break;

            }

            case 3:{

                break;

            }

            case 4:{

                break;

            }

            case 5:{

                break;

            }

            case 6:{

                break;

            }

        }

    }

    private void getNextStage(int currentStage){



    }

    @Override
    public void setCurrentStage(SimulationStage currentStage) {

    }

//    @Override
//    public void stageProgress(int stageNumber, int progress) {
//
//        setSimulationProgress(progress);
//
//        if(progress == 100){
//
//            getNextStage(stageNumber);
//
//        }
//
//    }



}
