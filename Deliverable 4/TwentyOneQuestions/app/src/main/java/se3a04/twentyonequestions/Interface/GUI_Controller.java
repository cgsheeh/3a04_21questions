package se3a04.twentyonequestions.Interface;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.MapFragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import se3a04.twentyonequestions.MessagePassing.MapLocation;
import se3a04.twentyonequestions.MessagePassing.MessageChannel;
import se3a04.twentyonequestions.Controller.QuestionController;
import se3a04.twentyonequestions.MessagePassing.QuestionType;
import se3a04.twentyonequestions.R;


/**
 * GUI_Controller
 *      Controller class handling gui states and control flow
 *      Extends FragmentActivity as the view will be displaying Maps fragments
 */
public class GUI_Controller extends FragmentActivity implements View.OnClickListener {

    /**
     * Fields
     *      btStart: Start button
     *      btSetting: Setting button
     *      btYes: Yes button
     *      btNo: No button
     *      btCorrect: Correct button
     *      btIncorrect: Incorrect button
     *      lblQuestionAsked: View used to display the question string
     *      txtAnswerMap: View used to display the answer
     *      question_controller: Question controller instance
     *      channel: MessageChannel instance
     *      overallanswer: Final answer for the game
     *      screen: variable indicating the state of the screen
     *      map: Map fragment to display
     *      displayMap: boolean indicating if the map should be displayed
     *      location: MapLocation ADT instance holding location data for map fragment
     *
     */
    private Button btStart;
    private FloatingActionButton btSetting;
    private Button btYes;
    private Button btNo;
    private Button btCorrect;
    private Button btIncorrect;

    private TextView lblQuestionAsked;
    private EditText txtAnswerMap;

    private QuestionController question_controller;
    private MessageChannel channel;
    private String overallanswer = "";

    private Screen screen = Screen.START_SCREEN;


    private GoogleMapsFragment map ;
    private boolean displayMap = false;
    private MapLocation location;

    /**
     * onCreate
     *      Behaviour for creating the view of the start screen
     *
     * @param savedInstanceState:
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setScreen();


    }


    /**
     * onClick
     *      Implements the logic for on click listeners
     * @param v: The view that was clicked
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {//FSM regarding how to manage the pages
            case R.id.btStartGame:
                screen = Screen.QUESTION_SCREEN;
                this.displayMap=false;
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
            case R.id.btCorrect:
                this.question_controller = null;
                screen = Screen.START_SCREEN;
                setScreen();
                break;
            case R.id.btIncorrect:
                this.makeDialog("Report Issue", "What was the answer you were thinking of?");
            break;
        }
    }


    /**
     * onBackPressed
     *      Implements logic for when the back button is pressed
     */
    @Override
    public void onBackPressed() {
        switch (screen) {
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
                question_controller = null;
                channel = null;
                //this.map.onDestroyView();
                setScreen();

                break;
            case MAP_SCREEN:
                screen = Screen.START_SCREEN;
                this.question_controller = null;
                setScreen();
                break;
        }
    }

    /**
     * setScreen
     *      Sets the content of the screen
     */
    private void setScreen() {
        int layout = 0;//the layout screen to set too
        switch (screen) {
            case SETTING_SCREEN:
                layout = R.layout.setting_screen;
                break;
            case QUESTION_SCREEN:
                layout = R.layout.question_screen;
                break;
            case MAP_SCREEN:
                layout = R.layout.map_screen;
                break;
            default:
                layout = R.layout.start_screen;
        }

        setContentView(layout);
        setListeners();
    }

    /**
     * setListeners
     *      Sets the listeners of the application based on the view
     */
    private void setListeners() {
        /**
         * Takes the current screen given and instatiates the buttons
         * Then it will set the listeners from the boundry classes
         */
        switch (screen) {
            case SETTING_SCREEN:

                break;
            case QUESTION_SCREEN:

                this.btYes = (Button) findViewById(R.id.btYes);
                this.btNo = (Button) findViewById(R.id.btNo);
                this.lblQuestionAsked = (TextView) findViewById(R.id.lblQuestion);

                this.btYes.setOnClickListener(this);
                this.btNo.setOnClickListener(this);
                break;
            case MAP_SCREEN:
                this.btCorrect = (Button) findViewById(R.id.btCorrect);
                this.btIncorrect = (Button) findViewById(R.id.btIncorrect);
                this.txtAnswerMap = (EditText) findViewById(R.id.txtAnswerMap);
                location =(this.question_controller.getMapAnswer());
                createMap(R.id.map_screen_map, location);


                this.txtAnswerMap.setText(this.overallanswer);
                this.btCorrect.setOnClickListener(this);
                this.btIncorrect.setOnClickListener(this);
                break;
            default:
                this.btStart = (Button) findViewById(R.id.btStartGame);
                this.btSetting = (FloatingActionButton) findViewById(R.id.btSettings);
                this.btStart.setOnClickListener(this);
                this.btSetting.setOnClickListener(this);
        }

    }


    /**
     * nextQuestionRequest
     *      Logic for when a question is question, if it is finished then it will
     *      get the answer and display it on a map
     *
     * @param answer the answer that was given by the user
     */
    private void nextQuestionRequest(String answer) {
        channel.setAnswer(answer);
        waitForQuestion();

    }

    /**
     * waitForQuestion
     *      Blocking method that waits until a question is available, then displays
     */
    private void waitForQuestion() {
        while (!channel.canGetQuestion() && !question_controller.isFinished()) {
         //wait for answer
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        if(!question_controller.isFinished()) {
            setQuestion();
        }else{
            this.overallanswer = question_controller.getAnswer();
            this.screen = Screen.MAP_SCREEN;
            this.channel = null;
            this.displayMap= true;
            setScreen();
        }
    }




    /**
     * setQuestion
     *      Sets the question and the items to go with that question
     */
    private void setQuestion(){
        if(screen == Screen.QUESTION_SCREEN) {
                this.lblQuestionAsked.setText(channel.getQuestion());
                if (this.channel.getType() == QuestionType.MAP) {
                    this.displayMap = true;
                    location =((MapLocation)channel.getExtra());
                    createMap(R.id.question_map_screen, location);
                } else {
                    this.displayMap = false;
                    //createMap(R.layout.question_screen, R.id.question_map_screen, new MapLocation(200,200,20));
                }


                if (this.channel.getType() == QuestionType.IMAGE) {
                    //TODO future releases if expert needs to show an image
                } else {
                    //TODO future releases if expert needs to show an image
                }

            }else{

            }

    }

    /**
     * createMap
     *      Creates the map fragment for display
     */
     private void createMap(int fragment, MapLocation mapLocation){
         //createMap(R.layout.map_screen,R.id.map_screen_map, location);
         this.map = new GoogleMapsFragment();

         this.map.setLocation(mapLocation);

         FragmentManager manager = getSupportFragmentManager();
         FragmentTransaction transaction = manager.beginTransaction();

         transaction.add(fragment,map);
         transaction.commit();
     }


    //http://stackoverflow.com/questions/10903754/input-text-dialog-android
    //Acessed April 4th

    /**
     * makeDialog
     *      Creates pop-up dialog with title and message
     *      Reference: http://stackoverflow.com/questions/10903754/input-text-dialog-android
     *      Accessed April 4th, 2016
     * @param title: Title of dialog box
     * @param message: Message in dialog bo
     */
    private void makeDialog(String title, String message){
        this.question_controller = null;

        final EditText textbox = new EditText(this);

        AlertDialog.Builder dialogBox = new AlertDialog.Builder(this)

                .setTitle(title)
                .setMessage(message)
                .setView(textbox)
                .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        sendIssue(textbox.getText().toString());
                        screen = Screen.START_SCREEN;
                        setScreen();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        screen = Screen.START_SCREEN;
                        setScreen();

                    }
                });
        dialogBox.show();

    }


    /**
     * sendIssue
     *      Sends an email to the developers (ie Curtis) indicating an issue in the game
     *      Reference: http://stackoverflow.com/questions/8284706/send-email-via-gmail
     *      Accessed April 4th, 2016
     * @param body: body of the paragraph to send
     */
    private void sendIssue(String body){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

        Intent send = new Intent(Intent.ACTION_SENDTO);
        String uriText = "mailto:" + Uri.encode("milocj@mcmaster.ca") +
                "?subject=" + Uri.encode("Software issue for 21 Questions on "+ dateFormat.format(new Date())) +
                "&body=" + Uri.encode(body);
        Uri uri = Uri.parse(uriText);

        send.setData(uri);
        startActivity(Intent.createChooser(send, "Send mail..."));
    }



}