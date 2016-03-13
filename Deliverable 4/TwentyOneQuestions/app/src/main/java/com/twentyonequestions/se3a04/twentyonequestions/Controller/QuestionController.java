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
                }else{
                    checkGuesses();
                }

                if (isFinished()) {
                    current_question = null;
                    break;
                } else {
                    getNextQuestion();
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
     * Gives the next question to ask the user
     * @return the next question for the user to answer
     */
    private void getNextQuestion(){

        String table = experts.get(experts_turn).getQuestionTable();
        String query = experts.get(experts_turn).getQueryForQuestion();
        this.current_question = excuteQuery(table,query);

    }


    /**
     * prepairs and send the information to the database connector
     * @param table the table to preform the query on
     * @param query the query to preform
     */
    private String excuteQuery(String table, String query) {
        DatabaseConnector dbConnector = new DatabaseConnector();
        dbConnector.execute(new String[]{table,query});
        int reps = 0;
        while(dbConnector.getResult().equals("") && reps <200){
            try {
                Thread.sleep(100);
            }catch(InterruptedException e){};
            reps++;
        }

        return dbConnector.getResult();
    }

    /**
     * sets the answer and updates the expert count
     */
    private void addAnswer(){
        experts.get(experts_turn).add(current_question,channel.getAnswer());
        checkGuesses();
        experts_turn = (experts_turn+1)%experts.size();
    }


    /**
     * Checks all of the experts guesses to ensure that they are valid
     */
    private void checkGuesses() {
        for (int i = 0; i < experts.get(experts_turn).amountofGuesses() ; i++) {
            if(!experts.get(experts_turn).isValidGuess(i)){
                String table = experts.get(experts_turn).getGuessTable();
                String query = experts.get(experts_turn).getQueryForGuess();
                String newGuess = this.excuteQuery(table,query);
                experts.get(experts_turn).setNewGuess(i,newGuess);
            }
        }
    }

    /**
     * returns the answer based on each  expert
     * @return the answer from the expert
     */
    public String getAnswer() {
        String answer= "";
        for (int i = 0; i < experts.size() ; i++) {
            answer += experts.get(i).getBestGuess();
        }
        return answer;
    }


}
