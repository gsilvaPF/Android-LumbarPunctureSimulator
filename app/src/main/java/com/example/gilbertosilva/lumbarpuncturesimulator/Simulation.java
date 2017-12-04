package com.example.gilbertosilva.lumbarpuncturesimulator;

import java.util.ArrayList;

/**
 * Created by gilbertosilva on 10/26/17.
 */

public class Simulation implements SimulationStage.StageProgressListener {

    private String mType;
    private int mScore;
    private int mCompletion;
    private ArrayList<SimulationStage> mStages;
    private SimulationHandler msimulationHandler;

    static final String SIMULATION_GUIDED = "GUIDED";
    static final String SIMULATION_PRACTICE = "PRACTICE";
    static final String SIMULATION_TEST = "TEST";

    interface SimulationHandler{

        public void setProgress(int progress);
        public void setCurrentStage(SimulationStage currentStage);

    }

    Simulation(String type, Simulation.SimulationHandler simulationHandler){

        mType = type;
        mScore = 0;
        mCompletion = 0;
        mStages = new ArrayList<>();
        msimulationHandler = simulationHandler;

        SimulationStage stage;

        for(int i = 1; i < 5; i ++){

            stage = new SimulationStage(i, this);
            mStages.add(stage);

        }

    }

    public String getType(){

        return mType;

    }

    public void setScore(int score){

        mScore  = score;

    }

    public int getScore(){

        return mScore;

    }

    public void setCompletion(int completion){

        mCompletion = completion;

    }

    public int getCompletion(){

        return mCompletion;

    }

    public SimulationStage getCurrentStage(){

        SimulationStage lastStage = null;
        SimulationStage stage = null;
        int completion = 0;

        for(int i = 0; i < mStages.size(); i++){

            stage = mStages.get(i);
            completion = stage.getCompletion();

            if(completion < 100){

                lastStage = stage;
                break;

            }

            if(i == mStages.size() - 1 && lastStage == null){

                lastStage = stage;

            }

        }

        return lastStage;

    }

    @Override
    public void stageProgress(int stageNumber, int progress) {



    }

//    ArrayList<SimulationAlert> getAlerts(){
//
//        return mAlerts;
//
//    }

}
