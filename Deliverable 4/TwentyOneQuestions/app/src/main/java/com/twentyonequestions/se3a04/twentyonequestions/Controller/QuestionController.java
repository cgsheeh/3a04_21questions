package com.twentyonequestions.se3a04.twentyonequestions.Controller;

import android.util.Log;

import com.twentyonequestions.se3a04.twentyonequestions.Controller.Database.DatabaseConnector;
import com.twentyonequestions.se3a04.twentyonequestions.Controller.Experts.EnviromentExpert;
import com.twentyonequestions.se3a04.twentyonequestions.Controller.Experts.EstablishmentExpert;
import com.twentyonequestions.se3a04.twentyonequestions.Controller.Experts.LocationExpert;

import java.util.ArrayList;

/**
 * Created by curtis on 12/03/16.
 */
public class QuestionController extends Thread {
    private int MAX_QUESTIONS =21;

    private ArrayList<Expert>  experts =  new ArrayList<Expert>();//list of experts

    private int questions_asked =1;
    private int experts_turn = 0;
    private MessageChannel channel;
    private String current_question;

    public QuestionController(MessageChannel channel){
        //adding of experts

        experts.add(new EstablishmentExpert());
        experts.add(new EnviromentExpert());
        experts.add(new LocationExpert());
        this.channel = channel;

    }

    @Override
    public void run(){
        try {
            while (true) {
                if(questions_asked !=0) {
                    while (!channel.canGetAnswer()) { //wait for answer

                        Thread.sleep(100);

                    }
                    addAnswer();
                }
                if (isFinished()) {
                    current_question = null;
                    break;
                } else {
                    current_question = experts.get(experts_turn).getQuestion();
                }

                channel.setQuestion("Question " + this.questions_asked + ": " + this.current_question);
                questions_asked++;
            }
        }catch (InterruptedException io){

        }
    }


    /**
     * States if the game is over or not
     * @return if the game is over or not
     */
    public boolean isFinished(){
        return questions_asked>MAX_QUESTIONS;

    }


    /**
     * returns the answer based on each  expert
     * @return the answer from the expert
     */
    public String getAnswer() {
        String answer= "";
        for (int i = 0; i < experts.size() ; i++) {
            answer += experts.get(i).getGuess();
        }
        return answer;
    }


    /**
     * sets the answer and updates the expert count
     */
    private void addAnswer(){
        experts.get(experts_turn).add(current_question,channel.getAnswer());
        experts_turn = (experts_turn+1)%experts.size();
    }
}
