package com.twentyonequestions.se3a04.twentyonequestions.Interface;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.twentyonequestions.se3a04.twentyonequestions.Controller.MessageChannel;
import com.twentyonequestions.se3a04.twentyonequestions.Controller.QuestionController;
import com.twentyonequestions.se3a04.twentyonequestions.R;

public class GUI_Controller extends AppCompatActivity implements View.OnClickListener{

    //the buttons from the boundry activities
    private Button btStart;
    private FloatingActionButton btSetting;
    private Button btYes;
    private Button btNo;
    private Button btMaybe;
    private Button btCorrect;
    private Button btIncorrect;

    //textview of screens
    private TextView lblQuestionAsked;
    private EditText txtAnswerMap;

    private QuestionController question_controller;
    private MessageChannel channel;
    private String overallanswer = "";
    //private fields
    private Screen screen = Screen.START_SCREEN;


    /**
     * Constructor for creating the view of the start screen
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setScreen();

    }


    /**
     *Logic for on click listeners
     * @param v The view that was clicked
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){//FSM regarding how to manage the pages
            case R.id.btStartGame:
                screen = Screen.QUESTION_SCREEN;
                channel = new MessageChannel();
                question_controller = new QuestionController(channel);
                question_controller.start();
                setScreen();

                waitForQuestion();
                break;
            case R.id.btSettings:
                screen = Screen.SETTING_SCREEN;
                setScreen();
                break;
            case R.id.btYes:
                nextQuestionRequest("yes");

                break;
            case R.id.btNo:
                nextQuestionRequest("no");
                break;
            case R.id.btMaybe:
                nextQuestionRequest("maybe");

                break;
            case R.id.btCorrect:
            case R.id.btIncorrect:
                screen = Screen.START_SCREEN;
                setScreen();
                break;
        }
    }


    /**
     * Logic for when the back button is pressed
     */
    @Override
    public void onBackPressed() {
        switch (screen){
            case START_SCREEN:
                //kill the application
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
                break;
            case SETTING_SCREEN:
                screen = Screen.START_SCREEN;
                setScreen();
                break;
            case QUESTION_SCREEN:
                screen = Screen.START_SCREEN;
                question_controller.interrupt();
                question_controller =null;
                channel = null;
                setScreen();
                break;
            case MAP_SCREEN:
                screen = Screen.START_SCREEN;
                setScreen();
                break;
        }
    }

    /**
     * sets the content of the screen
     */
    private void setScreen(){
        int layout= 0;//the layout screen to set too
        switch (screen){
            case SETTING_SCREEN:
                layout= R.layout.setting_screen;
                break;
            case QUESTION_SCREEN:
                layout= R.layout.question_screen;
                break;
            case MAP_SCREEN:
                layout= R.layout.map_screen;
                break;
            default:
                layout= R.layout.start_screen;
        }
        setContentView(layout);
        setListeners();
    }

    /**
     * Sets the listeners of the application based on the view
     */
    private void setListeners(){
        /**
         * Takes the current screen given and instatiates the buttons
         * Then it will set the listeners from the boundry classes
         */
        switch (screen){
            case SETTING_SCREEN:

                break;
            case QUESTION_SCREEN:

                this.btYes = (Button) findViewById(R.id.btYes);
                this.btNo = (Button) findViewById(R.id.btNo);
                this.btMaybe = (Button) findViewById(R.id.btMaybe);
                this.lblQuestionAsked = (TextView) findViewById(R.id.lblQuestion);

                this.btYes.setOnClickListener(this);
                this.btNo.setOnClickListener(this);
                this.btMaybe.setOnClickListener(this);
                break;
            case MAP_SCREEN:
                this.btCorrect = (Button) findViewById(R.id.btCorrect);
                this.btIncorrect = (Button) findViewById(R.id.btIncorrect);
                this.txtAnswerMap = (EditText) findViewById(R.id.txtAnswerMap);
                this.txtAnswerMap.setText(this.overallanswer);
                this.btCorrect.setOnClickListener(this);
                this.btIncorrect.setOnClickListener(this);
                displayMap();
                break;
            default:
                this.btStart = (Button) findViewById(R.id.btStartGame);
                this.btSetting = (FloatingActionButton) findViewById(R.id.btSettings);
                this.btStart.setOnClickListener(this);
                this.btSetting.setOnClickListener(this);
        }

    }



    /**
     * Logic for when a question is question, if it is finished then it will get the answer and
     * display it on a map
     * @param answer the answer that was given by the user
     */
    private void nextQuestionRequest(String answer) {
        if(question_controller.isFinished()){

            this.overallanswer= question_controller.getAnswer();
            this.screen = Screen.MAP_SCREEN;
            this.question_controller = null;
            this.channel = null;
            setScreen();
        }else{
            channel.setAnswer(answer);
            waitForQuestion();
        }
    }

    /**
     * Waits untill the question becomes avalible.
     * Then displats the question
     */
    private void waitForQuestion() {
        while (!channel.canGetQuestion()) { //wait for answer
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        this.lblQuestionAsked.setText(channel.getQuestion());
    }


    /**
     * TODO use the google map to display the final answer
     */
    private void displayMap() {
    }

}
