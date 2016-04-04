package se3a04.twentyonequestions.Controller.Experts;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.NoSuchElementException;
import java.util.concurrent.TimeoutException;

import se3a04.twentyonequestions.Controller.Expert;
import se3a04.twentyonequestions.MessagePassing.MapLocation;

/**
 * Created by curtis on 12/03/16.
 */
public class EnviromentExpert extends Expert {
    private final String TABLE ="EnvironmentExpertTable";
    private String[] children = {"1"};
    private String current = "1";
    private boolean done = false;
    /**
     * Addes the question and answer pair to the expert
     */
    @Override
    public void add(String qustion, String answer){
        questions.add(qustion);
        answers.add(answer);

    }

    @Override
    public MapLocation getMap() {
        return null;
    }

    @Override
    public Bitmap getImage() {
        return null;
    }

    /**
     * gives the next question to ask based on what the user has previously asked
     * @return the next question
     * @throws TimeoutException if the server could not be reached
     */
    @Override
    public String getQuestion() throws TimeoutException {
        String nextChild= "";
        if(this.children.length ==1){
            nextChild = this.children[0];
        }else if (this.children.length == 2){
            if(this.answers.get(this.answers.size()-1).toLowerCase().equals("yes")){
                nextChild = this.children[1];
            }else{
                nextChild = this.children[0];
            }
        }else{
            throw new NoSuchElementException("there is no more elements to get questions from");
        }

        String query = getQuery("Question,LeftChild,RightChild", "ID=" + "'" + nextChild + "'");
        String raw = this.excuteQuery(query).trim().replace("\n", "");
        if(!raw.equals("") || raw == "null") {
            String[] raw_parsed = raw.split(",");
            try {
                current = nextChild;
                this.children = new String[]{raw_parsed[1], raw_parsed[2]};

            }catch (ArrayIndexOutOfBoundsException a){
                this.children = new String[]{null,null};
                done = true;
            }
            return raw_parsed[0];
        }else{
            done = true;
            return null;
        }
    }



    /**
     * States if there are any more questions to ask
     * @return if the expert is done asking questions
     */
    @Override
    public boolean hasMoreQuestions() {
        return !(done);
    }

    /**
     * Gives the answer of the expert
     * @return the answer of the expert
     * @throws TimeoutException if the server could not be reached
     */
    @Override
    public String getGuess() throws TimeoutException {

        int count  = 1;

        while (this.answers.size() - count >0){
            if(this.answers.get(answers.size()-count).equals("yes")) {

                return excuteQuery(this.getQuery("Guess", "Question=" + "'" + this.questions.get(questions.size() -count) + "'"));
            }
            count++;
        }

        return "no result found";


    }

    /**
     * creates the query to excute on the database
     * @param attribute the attribute we wish to get
     * @param condition the conition in which to check for
     * @return  the query to excute on the database
     */
    private String getQuery(String attribute,String condition){
        String query= "Select " + attribute +"\n";
        query += "From " + TABLE + "\n";
        query += "Where " +condition +";";
        return query;
    }

}
