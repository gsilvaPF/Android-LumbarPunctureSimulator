package com.example.gilbertosilva.lumbarpuncturesimulator;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class SelectModeActivity extends AppCompatActivity {

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_mode);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null) {

            actionBar.setTitle(R.string.select_simulation_mode);

        }

        mContext = this;

        Button guidedButton = findViewById(R.id.selectModeActivityGuidedButton);
        Button practiceButton = findViewById(R.id.selectModeActivityPracticeButton);
        Button testButton = findViewById(R.id.selectModeActivityTestButton);
        ImageButton guidedInfoButton = findViewById(R.id.selectModeActivityGuidedInfoButton);
        ImageButton practiceInfoButton = findViewById(R.id.selectModeActivityPracticeInfoButton);
        ImageButton testInfoButton = findViewById(R.id.selectModeActivityTestInfoButton);

        guidedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, R.string.simulation_button_guided, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(mContext, SimulationActivity.class);
                intent.putExtra("type", Simulation.SIMULATION_GUIDED);
                startActivity(intent);
            }
        });

        practiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, R.string.simulation_button_practice, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(mContext, SimulationActivity.class);
                intent.putExtra("type", Simulation.SIMULATION_PRACTICE);
                startActivity(intent);
            }
        });

        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, R.string.simulation_button_test, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(mContext, SimulationActivity.class);
                intent.putExtra("type", Simulation.SIMULATION_TEST);
                startActivity(intent);
            }
        });

        guidedInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(R.string.info)
                        .setMessage(R.string.simulation_guided_info_button_message).setNeutralButton(R.string.close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                // Create the AlertDialog object and return it
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        practiceInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(R.string.info)
                        .setMessage(R.string.simulation_practice_info_button_message).setNeutralButton(R.string.close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                // Create the AlertDialog object and return it
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        testInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(R.string.info)
                        .setMessage(R.string.simulation_test_info_button_message).setNeutralButton(R.string.close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                // Create the AlertDialog object and return it
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

}
