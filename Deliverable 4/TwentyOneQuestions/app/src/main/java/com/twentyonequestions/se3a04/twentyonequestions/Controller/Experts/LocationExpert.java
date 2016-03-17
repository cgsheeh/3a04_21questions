package com.twentyonequestions.se3a04.twentyonequestions.Controller.Experts;

import com.twentyonequestions.se3a04.twentyonequestions.Controller.Expert;

/**
 * Created by curtis on 12/03/16.
 */
public class LocationExpert extends Expert {
    private final String TABLE ="";

    @Override
    public String getQuestion() {
        return this.excuteQuery(TABLE,"");
    }

    @Override
    public String getGuess() {
        return "";
    }

    private String generateQuery(){
        return "";
    }
}