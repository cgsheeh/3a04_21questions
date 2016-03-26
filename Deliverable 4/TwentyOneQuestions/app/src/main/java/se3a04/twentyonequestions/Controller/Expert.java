package se3a04.twentyonequestions.Controller;

import java.util.ArrayList;

import se3a04.twentyonequestions.Controller.Database.DatabaseConnector;

/**
 * Created by curtis on 12/03/16.
 */
public abstract class Expert {

    private ArrayList<String> questions;
    private ArrayList<String> answers;
    private int position = 1;

    public Expert(){
        questions = new ArrayList<String>();
        answers = new ArrayList<String>();
    }

    /**
     * Addes the question and answer pair to the expert
     */
    public void add(String qustion, String answer){
        questions.add(qustion);
        answers.add(answer);

    }

    /**
     * Gives the question to ask the user
     * @return the address of the question table
     */
    public abstract String getQuestion();





    /**
     * Gives the best guess of the expert
     * @return the guess given by the expert
     */
    public abstract String getGuess();


    /**
     * prepairs and send the information to the database connector
     * @param table the table to preform the query on
     * @param query the query to preform
     */
    protected String excuteQuery(String table, String query) {
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

}
