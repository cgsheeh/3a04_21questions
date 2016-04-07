package se3a04.twentyonequestions.Controller.Experts;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.NoSuchElementException;
import java.util.concurrent.TimeoutException;

import se3a04.twentyonequestions.Controller.Expert;
import se3a04.twentyonequestions.MessagePassing.MapLocation;

/**
 * EnviromentExpert
 *      Expert that guesses based on the users environments
 */
public class EnviromentExpert extends Expert {
    /**
     * Fields
     *      TABLE: name of the table holding the questions for this expert
     *      children: holds the question ids of the child nodes
     *      current: current question id
     *      done: boolean indicating if the expert is done asking questions
     */
    private final String TABLE = "EnvironmentExpertTable";
    private String[] children = {"1"};
    private String current = "1";
    private boolean done = false;

    /**
     * add
     *      Adds the question and answer pair to the expert
     * @param question: question to add to the expert
     * @param answer: answer to add to the expert
     */
    @Override
    public void add(String question, String answer){
        questions.add(question);
        answers.add(answer);

    }

    /**
     * The next two methods are in place in case of future changes
     */
    @Override
    public MapLocation getMap() {
        return null;
    }
    @Override
    public Bitmap getImage() {
        return null;
    }

    /**
     * getQuestion
     *      gives the next question to ask based on what the user has previously asked
     * @return the next question
     * @throws TimeoutException if the server could not be reached
     */
    @Override
    public String getQuestion() throws TimeoutException {
        String nextChild= "";
        if(this.children.length == 1){
            nextChild = this.children[0];
        }else if (this.children.length == 2){
            if(this.answers.get(this.answers.size() - 1).toLowerCase().equals("yes")){
                nextChild = this.children[0];
            }else{
                nextChild = this.children[1];
            }
        }else{
            throw new NoSuchElementException("there is no more elements to get questions from");
        }

        String query = getQuery("Question,LeftChild,RightChild", "ID=" + "'" + nextChild + "'");

        String raw = this.excuteQuery(query).trim().replace("\n", "");
        Log.e("raw", raw);
        if(raw.equals("null")){
            return null;
        }
        if(!raw.equals("")) {
            String[] raw_parsed = raw.split(",");
            try {
                current = nextChild;
                this.children = new String[]{raw_parsed[1], raw_parsed[2]};

            }catch (ArrayIndexOutOfBoundsException a){
                this.children = new String[]{null,null};
                done = true;
            }
            Log.e("question", raw_parsed[0]);
            return raw_parsed[0];
        }else{
            done = true;
            return null;
        }
    }



    /**
     * hasMoreQuestions
     *      States if there are any more questions to ask
     * @return if the expert is done asking questions
     */
    @Override
    public boolean hasMoreQuestions() {
        return !(done);
    }

    /**
     * getGuess
     *      Gives the answer of the expert
     * @return the answer of the expert
     * @throws TimeoutException if the server could not be reached
     */
    @Override
    public String getGuess() throws TimeoutException {

        int count  = 1;

        while (this.answers.size() - count >0){
            if(this.answers.get(answers.size()-count).equals("yes")) {

                return excuteQuery(this.getQuery("Guess", "Question=" + "'" + this.questions.get(questions.size() -count) + "'"))
                        .replaceAll("</br>", "\n");
            }
            count++;
        }

        return "no result found\n";


    }

    /**
     * getQuery
     *      creates the query to execute on the database
     * @param attribute the attribute we wish to get
     * @param condition the conition in which to check for
     * @return  the query to excute on the database
     */
    private String getQuery(String attribute, String condition){
        String query= "Select " + attribute + "\n";
        query += "From " + TABLE + "\n";
        query += "Where " +condition + ";";
        return query;
    }

}
