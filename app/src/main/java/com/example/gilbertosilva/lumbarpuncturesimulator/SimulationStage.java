package com.example.gilbertosilva.lumbarpuncturesimulator;

import java.util.ArrayList;

/**
 * Created by gilbertosilva on 11/16/17.
 */

class SimulationStage {

    private int mStageNumber;
    private int mCompletion;
    private String mStageTitle;
    private String mStageMessage;
    private ArrayList<SimulationAlert> mAlerts;
    private StageProgressListener mStageProgressListener;

    interface StageProgressListener{

        public void stageProgress(int stageNumber, int progress);

    }

    SimulationStage(int stageNumber, StageProgressListener stageProgressListener){

        mStageNumber = stageNumber;
        mStageProgressListener = stageProgressListener;
        mCompletion = 0;
        mAlerts = new ArrayList<>();

        switch (stageNumber){

            case 1:{

                mStageTitle = "";
                mStageMessage = "";

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


        }


    }

    public int getStageNumber(){

        return mStageNumber;

    }

    public String getStageTitle(){

        return  mStageTitle;

    }

    public String getStageMessage(){

        return mStageMessage;

    }

    public void setCompletion(int completion){

        mCompletion = completion;

    }

    public int getCompletion(){

        return mCompletion;

    }

    public ArrayList<SimulationAlert> getAlerts(){

        return mAlerts;

    }

}
