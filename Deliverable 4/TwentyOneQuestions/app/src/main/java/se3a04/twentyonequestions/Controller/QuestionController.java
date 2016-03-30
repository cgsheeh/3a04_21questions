package se3a04.twentyonequestions.Controller;

import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import se3a04.twentyonequestions.Controller.Experts.EnviromentExpert;
import se3a04.twentyonequestions.Controller.Experts.EstablishmentExpert;
import se3a04.twentyonequestions.Controller.Experts.LocationExpert;
import se3a04.twentyonequestions.MessagePassing.MapLocation;
import se3a04.twentyonequestions.MessagePassing.MessageChannel;
import se3a04.twentyonequestions.MessagePassing.QuestionType;

/**
 * Created by curtis on 12/03/16.
 */
public class QuestionController extends Thread {

    private ArrayList<Expert>  experts =  new ArrayList<Expert>();//list of experts

    private int questions_asked =0;
    private int experts_turn = -1;
    private MessageChannel channel;
    private String current_question;
    private boolean timeoutError = false;
    private boolean finished = false;

    public QuestionController(MessageChannel channel){
        //adding of experts

        experts.add(new EstablishmentExpert());
        experts.add(new EnviromentExpert());
        experts.add(new LocationExpert());
        this.channel = channel;


    }

    @Override
    public void run(){
        try {
            while (!isFinished()) {
                if(questions_asked !=0) {
                    while (!channel.canGetAnswer()) { //wait for answer

                        Thread.sleep(100);

                    }
                    addAnswer();
                }

                Log.e("Expert", "turn " +this.experts_turn);
                int nextC= 0;
                current_question = null;
                while(nextC < this.experts.size()) {
                    experts_turn = (experts_turn + 1)  % experts.size();
                    if(this.experts.get(experts_turn).hasMoreQuestions()){
                        current_question = experts.get(experts_turn).getQuestion();
                    }

                    if(current_question !=null){
                        break;
                    }
                    nextC++;
                }


                if(current_question !=null){
                    channel.setQuestion("Question " + this.questions_asked + ": " + this.current_question);
                    channel.setType(experts.get(experts_turn).getType());
                    if(this.experts.get(experts_turn).getType() == QuestionType.MAP){
                        channel.setExtra(experts.get(experts_turn).getMap());
                    }else if (this.experts.get(experts_turn).getType() == QuestionType.IMAGE){
                        channel.setExtra(experts.get(experts_turn).getImage());
                    }
                    questions_asked++;
                }else{
                    finished = true;
                }

            }
        }catch (InterruptedException io){

        } catch (TimeoutException e) {
            current_question = null;
            timeoutError = true;
        }
    }


    /**
     * States if the game is over or not
     * @return if the game is over or not
     */
    public boolean isFinished(){
        return finished || timeoutError;
    }


    /**
     * returns the answer based on each  expert
     * @return the answer from the expert
     */
    public String getAnswer() {
        if(timeoutError){
            return "Error could not connect to internet";
        }
        String answer= "";
        for (int i = 0; i < experts.size() ; i++) {
            try {
                answer += experts.get(i).getGuess();
            } catch (TimeoutException e) {
                return "Error could not connect to internet";
            }
        }
        return answer;
    }


    /**
     * sets the answer and updates the expert count
     */
    private void addAnswer(){
        experts.get(experts_turn).add(current_question,channel.getAnswer());

    }

    public MapLocation getMapAnswer(){
        return new MapLocation(0,0,40);
    }
}
