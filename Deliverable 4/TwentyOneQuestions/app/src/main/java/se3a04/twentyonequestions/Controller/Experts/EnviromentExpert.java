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
    private final String TABLE ="Enviroment_Table";
    private String[] children = {"1"};
    private String current = "1";
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

        String query = getQuery("Question,LeftChild,RightChild", "ID=" +"'" +nextChild+"'");
        String raw = this.excuteQuery(query);
        Log.e("DATA", query);
        String[] raw_parsed = raw.split(",");
        this.children = new String[]{raw_parsed[1],raw_parsed[2]};
        current = nextChild;
        return raw_parsed[0];
    }



    /**
     * States if there are any more questions to ask
     * @return if the expert is done asking questions
     */
    @Override
    public boolean hasMoreQuestions() {
        return !(isDone());
    }

    /**
     * Gives the answer of the expert
     * @return the answer of the expert
     * @throws TimeoutException if the server could not be reached
     */
    @Override
    public String getGuess() throws TimeoutException {
        return excuteQuery(this.getQuery("Answer", current));
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

    /**
     * Determines if there the expert is finished asking questions
     * @return if the expert is done asking questions
     */
    private boolean isDone() {
        /**
         * Logic is that if is one child then return that were done
         * if there are two children we have to check the nodes
         * if there are children on both nodes then we are not done
         * if there are children on no nodes then were done
         * else we have to check what they have answered for the last questiont to see
         * if we went to a null node.
         */
        if (this.children.length==2){
            if(this.children[0] ==null && this.children[1] ==null){
                return true;

            }else if (this.children[0] !=null && this.children[1] !=null){
                return false;

            }else if (this.children[0] ==null && this.children[1] !=null) {
                if (this.answers.get(this.answers.size()-1).toLowerCase().equals("no")) {
                    return true;
                } else{
                    return false;
                }
            }else{
                if (this.answers.get(this.answers.size()-1).toLowerCase().equals("yes")) {
                    return true;
                } else{
                    return false;
                }
            }
        }
        return false;
    }
}
