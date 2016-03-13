package com.twentyonequestions.se3a04.twentyonequestions.Controller;

import java.util.ArrayList;

/**
 * Created by curtis on 12/03/16.
 */
public abstract class Expert {
    private final String TABLE ="";
    private ArrayList<String> questions;
    private ArrayList<String> answers;
    private ArrayList<String> currentGuess;

    public Expert(){
        questions = new ArrayList<String>();
        answers = new ArrayList<String>();
        currentGuess = new ArrayList<String>();
    }

    /**
     * Addes the question and answer pair to the expert
     */
    public void add(String qustion, String answer){
        questions.add(qustion);
        answers.add(answer);

    }

    /**
     * Gives the table in which to preform the query on
     * @return the address of the question table
     */
    public abstract String getQuestionTable();


    /**
     * Gives the query based on the previous questions and current guesses
     * @return a query to run on the question table
     */
    public abstract String getQueryForQuestion();


    /**
     * Gives the table in which to search for new guesses for
     * @return the address of the guess table
     */
    public abstract String getGuessTable();


    /**
     * Gives the query for a new guess if the old one is no longer valid
     * @return a query to run on the guess table
     */
    public abstract String getQueryForGuess();

    /**
     * Returns if the guess is still valid given new information
     * @param i the position in the guess array (ment to be itterated over)
     * @return if the guess is still valid
     */
    public abstract boolean isValidGuess(int i);


    /**
     * Gives the best guess of the expert
     * @return the guess given by the expert
     */
    public String getBestGuess(){
        if(currentGuess.size() >0){
            return questions.get(0);
        }else{
            return "";
        }
    }

    /**
     * gives the amount of guesses the expert has
     * @return the amount of guesses the expert has
     */
    public int amountofGuesses(){
        return currentGuess.size();
    }

    public void setNewGuess(int i, String newGuess) {
        this.currentGuess.set(i,newGuess);
    }
}
