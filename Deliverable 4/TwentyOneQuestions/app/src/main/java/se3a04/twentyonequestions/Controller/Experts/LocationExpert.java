package se3a04.twentyonequestions.Controller.Experts;

import android.graphics.Bitmap;

import java.util.concurrent.TimeoutException;

import se3a04.twentyonequestions.Controller.Expert;
import se3a04.twentyonequestions.MessagePassing.MapLocation;

/**
 * Created by curtis on 12/03/16.
 */
public class LocationExpert extends Expert {
    private final String TABLE ="Location_Table";

    @Override
    public String getQuestion() throws TimeoutException {
        return null;
    }

    @Override
    public MapLocation getMap() {
        return null;
    }

    @Override
    public Bitmap getImage() {
        return null;
    }

    @Override
    public boolean hasMoreQuestions() {
        return false;
    }

    @Override
    public String getGuess() throws TimeoutException {
        return null;
    }


    /**
     * Determines if there the expert is finished asking questions
     * @return if the expert is done asking questions
     */
    private boolean isDone() {
        return true;


    }

    private String getQuery(String attribute,String condition){
        String query= "Select " + attribute +"\n";
        query += "From " + TABLE + "\n";
        query += "Where " +condition +";";
        return query;
    }
}