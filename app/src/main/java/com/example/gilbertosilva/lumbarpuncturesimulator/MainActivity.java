package com.example.gilbertosilva.lumbarpuncturesimulator;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private ProgressBar mProgressBar;
    private Button mRetryButton;
    private Context mContext;

    final static int REQUEST_ENABLE_BT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);

        mContext = this;

        mProgressBar = findViewById(R.id.mainActivityProgressBar);
        mRetryButton = findViewById(R.id.mainActivityRetryButton);
        mProgressBar.setVisibility(View.INVISIBLE);
        mRetryButton.setVisibility(View.INVISIBLE);

        mRetryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });

        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    @Override
    public void onResume(){
        super.onResume();

        refresh();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode,
                           int resultCode,
                           Intent data){
//        super.onActivityResult(requestCode,resultCode,data);

        if(resultCode == RESULT_OK){

            goToSelectModeActivity();

        }
        else if(resultCode == RESULT_CANCELED){

            Toast.makeText(mContext, R.string.error_could_not_enable_bluetooth, Toast.LENGTH_LONG).show();

        }

    }

    public void refresh(){

        mProgressBar.setVisibility(View.VISIBLE);
        mRetryButton.setVisibility(View.INVISIBLE);
        goToSelectModeActivity();

//        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//
//        if (mBluetoothAdapter == null) {
//
//            mRetryButton.setVisibility(View.VISIBLE);
//            mProgressBar.setVisibility(View.INVISIBLE);
//
//            AlertDialog.Builder builder = new AlertDialog.Builder(mContext).setTitle(R.string.error)
//                    .setMessage(R.string.error_device_does_not_support_bluetooth_message).setNeutralButton(R.string.close, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    });
//            AlertDialog dialog = builder.create();
//            dialog.show();
//
//        }
//        else{
//
//            if (!mBluetoothAdapter.isEnabled()) {
//
//                mRetryButton.setVisibility(View.VISIBLE);
//                mProgressBar.setVisibility(View.INVISIBLE);
//                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//
//            }
//            else{
//
//                goToSelectModeActivity();
//
//            }
//
//        }

    }

    public void goToSelectModeActivity(){

        new CountDownTimer(2000, 1000) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                mProgressBar.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(MainActivity.this, SelectModeActivity.class);
                startActivity(intent);
            }

        }.start();

    }

}
