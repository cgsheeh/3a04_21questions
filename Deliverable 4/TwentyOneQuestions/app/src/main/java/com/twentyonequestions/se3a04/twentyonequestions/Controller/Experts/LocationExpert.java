package com.twentyonequestions.se3a04.twentyonequestions.Controller.Experts;

import com.twentyonequestions.se3a04.twentyonequestions.Controller.Expert;

/**
 * Created by curtis on 12/03/16.
 */
public class LocationExpert extends Expert {

    @Override
    public String getQuestionTable() {
        return "";
    }

    @Override
    public String getQueryForQuestion() {
        return "";
    }

    @Override
    public String getGuessTable() {
        return "";
    }

    @Override
    public String getQueryForGuess() {
        return "";
    }

    @Override
    public boolean isValidGuess(int i) {
        return true;
    }
}
