package se3a04.twentyonequestions.Controller;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import se3a04.twentyonequestions.Controller.Database.DatabaseConnector;
import se3a04.twentyonequestions.MessagePassing.MapLocation;
import se3a04.twentyonequestions.MessagePassing.QuestionType;

/**
 * Expert
 *      Abstract class from which each expert is extended
 */
public abstract class Expert {

    /**
     * Fields
     *      questions: List of questions the expert has asked
     *      answers: List of answers for each corresponding question
     *      type: type of questions asked by the expert
     */
    protected ArrayList<String> questions;
    protected ArrayList<String> answers;
    protected QuestionType type = QuestionType.REGULAR;

    /**
     * Constructor
     *      Builds the expert object, creates questions and answers lists
     */
    public Expert(){
        questions = new ArrayList<String>();
        answers = new ArrayList<String>();
    }

    /**
     * add
     *      Adds the question and answer pair to the expert
     */
    public void add(String qustion, String answer){
        questions.add(qustion);
        answers.add(answer);
    }

    /**
     * getType
     *      Gives the experts type
     * @return: the type of questions the expert will ask
     */
    public QuestionType getType(){
        return type;
    }

    /**
     * getQuestion
     *      Gives the question to ask the user
     * @return: the address of the question table
     */
    public abstract String getQuestion() throws TimeoutException;

    /**
     * getMap
     *      Gives the map associated with the question
     */
    public abstract MapLocation getMap();

    /**
     * getImage
     *      Gives the image associated with the question
     */
    public abstract Bitmap getImage();

    /**
     * hasMoreQuestions
     *      Determines if the expert still has questions to ask.
     * @return: if there are still more questions from the expert
     */
    public abstract boolean hasMoreQuestions();


    /**
     * getGuess
     *      Gives the best guess of the expert
     * @return: the guess given by the expert
     */
    public abstract String getGuess() throws TimeoutException;


    /**
     * excuteQuery
     *      prepares and sends the information to the database connector
     * @param query: the query to perform
     */
    protected String excuteQuery(String query) throws TimeoutException {
        DatabaseConnector dbConnector = new DatabaseConnector();
        dbConnector.execute(new String[]{query});
        int reps = 0;
        while(!dbConnector.isReady() && reps <200){
            try {
                Thread.sleep(100);
            }catch(InterruptedException e){};
            reps++;
        }
        String data = dbConnector.getResult();
        if(data.equals("")){
            throw new TimeoutException("We could not reach the server");
        }
        return data;
    }



}
