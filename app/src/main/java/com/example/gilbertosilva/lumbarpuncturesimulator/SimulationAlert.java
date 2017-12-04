package com.example.gilbertosilva.lumbarpuncturesimulator;

import java.util.Date;

/**
 * Created by gilbertosilva on 10/26/17.
 */

public class SimulationAlert {
    private String mType;
    private String mMessage;
    private Date mDate;

    final static String ERROR = "ERROR";
    final static String WARNING = "WARNING";
    final static String SUCCESS = "SUCCESS";

    public SimulationAlert(String type, String message, Date date) {

        mType = type;
        mMessage = message;
        mDate = date;

    }

    public void setDate(Date date) {

        mDate = date;

    }

    public Date getDate() {

        return mDate;

    }

    public void setType(String type) {

        mType = type;

    }

    public String getType() {

        return mType;

    }

    public void setMessage(String message) {

        mMessage = message;

    }

    public String getMessage(){

        return mMessage;

    }

}
