package se3a04.twentyonequestions.Controller;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import se3a04.twentyonequestions.Controller.Database.DatabaseConnector;
import se3a04.twentyonequestions.MessagePassing.MapLocation;
import se3a04.twentyonequestions.MessagePassing.QuestionType;

/**
 * Created by curtis on 12/03/16.
 */
public abstract class Expert {

    protected ArrayList<String> questions;
    protected ArrayList<String> answers;
    protected QuestionType type = QuestionType.REGULAR;

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
     * Gives the type of expert it is
     * @return the type of questions the expert will ask
     */
    public QuestionType getType(){
        return type;
    }

    /**
     * Gives the question to ask the user
     * @return the address of the question table
     */
    public abstract String getQuestion() throws TimeoutException;

    /**
     * Gives the map of asociated with the question
     */
    public abstract MapLocation getMap();

    /**
     * Gives the question associated with the question
     */
    public abstract Bitmap getImage();

    /**
     * Determines if the expert still has questions to ask.
     * @return if there are still more questions from the expert
     */
    public abstract boolean hasMoreQuestions();


    /**
     * Gives the best guess of the expert
     * @return the guess given by the expert
     */
    public abstract String getGuess() throws TimeoutException;


    /**
     * prepairs and send the information to the database connector
     * @param query the query to preform
     */
    protected String excuteQuery( String query) throws TimeoutException {
        DatabaseConnector dbConnector = new DatabaseConnector();
        dbConnector.execute(new String[]{query});
        int reps = 0;
        while(dbConnector.getResult().equals("") && reps <200){
            try {
                Thread.sleep(100);
            }catch(InterruptedException e){};
            reps++;
        }

        if(dbConnector.getResult().equals("")){
            throw new TimeoutException("We could not reach  the server");
        }
        return dbConnector.getResult();
    }



}
