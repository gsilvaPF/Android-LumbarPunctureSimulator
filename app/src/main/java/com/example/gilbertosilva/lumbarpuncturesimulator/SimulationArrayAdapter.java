package com.example.gilbertosilva.lumbarpuncturesimulator;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gilbertosilva on 10/26/17.
 */

public class SimulationArrayAdapter extends ArrayAdapter {

    private Context mContext;
    private ArrayList<SimulationAlert> mArrayList;

    public SimulationArrayAdapter(Context context, int resource, ArrayList<SimulationAlert> objects) {
        super(context, resource, objects);
        mContext = context;
        mArrayList = objects;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent){

        ViewHolder viewHolder;

        if(convertView == null){

            // inflate the layout
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(R.layout.simulation_cell, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.imageView = convertView.findViewById(R.id.simulationCellImageView);
            viewHolder.title = convertView.findViewById(R.id.simulationCellTitle);
            viewHolder.subtilte = convertView.findViewById(R.id.simulationCellSubtitle);
            viewHolder.timeStamp = convertView.findViewById(R.id.simulationCellTimeStamp);

            convertView.setTag(viewHolder);

        }else{
            // we've just avoided calling findViewById() on resource everytime
            // just use the viewHolder
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.imageView.setImageResource(R.drawable.ic_check_box_green_24dp);
        viewHolder.title.setText("");
        viewHolder.subtilte.setText("");
        viewHolder.timeStamp.setText("");

        SimulationAlert alert = mArrayList.get(position);

        if(alert != null){

            String type = alert.getType();

            switch (type){

                case SimulationAlert.ERROR:{

                    viewHolder.imageView.setImageResource(R.drawable.ic_error_red_24dp);
                    viewHolder.title.setText(R.string.simulation_alert_type_error_title);
                    viewHolder.subtilte.setText("Error message...");
                    viewHolder.timeStamp.setText("1hr ago");

                    break;

                }

                case SimulationAlert.WARNING:{

                    viewHolder.imageView.setImageResource(R.drawable.ic_warning_yellow_24dp);
                    viewHolder.title.setText(R.string.simulation_alert_type_warning_title);
                    viewHolder.subtilte.setText("Warning message...");
                    viewHolder.timeStamp.setText("1hr ago");

                    break;

                }

                case SimulationAlert.SUCCESS:{

                    viewHolder.imageView.setImageResource(R.drawable.ic_check_box_green_24dp);
                    viewHolder.title.setText(R.string.simulation_alert_type_success_title);
                    viewHolder.subtilte.setVisibility(View.GONE);
                    viewHolder.timeStamp.setText("1hr ago");

                    break;

                }

                default:{

                    break;

                }

            }

        }

        return convertView;

    }

    class ViewHolder{

        ImageView imageView;
        TextView title;
        TextView subtilte;
        TextView timeStamp;

    }


}
