package se3a04.twentyonequestions.Interface;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import se3a04.twentyonequestions.MessagePassing.MessageChannel;
import se3a04.twentyonequestions.Controller.QuestionController;
import se3a04.twentyonequestions.MessagePassing.QuestionType;
import se3a04.twentyonequestions.R;


public class GUI_Controller extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {

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


    //Google maps thing
    private GoogleMap mMap;
    private SupportMapFragment map_fragment;
    private int latitude = 0;
    private int longitude = 0;
    private int zoom = 30;
    private boolean displayMap = false;

    /**
     * Constructor for creating the view of the start screen
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setScreen();


    }


    /**
     * Logic for on click listeners
     *
     * @param v The view that was clicked
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
     * Sets the listeners of the application based on the view
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
                this.btMaybe = (Button) findViewById(R.id.btMaybe);
                this.lblQuestionAsked = (TextView) findViewById(R.id.lblQuestion);

                this.btYes.setOnClickListener(this);
                this.btNo.setOnClickListener(this);
                this.btMaybe.setOnClickListener(this);
                setUpMaps(R.id.questions_map_frag);
                break;
            case MAP_SCREEN:
                this.btCorrect = (Button) findViewById(R.id.btCorrect);
                this.btIncorrect = (Button) findViewById(R.id.btIncorrect);
                this.txtAnswerMap = (EditText) findViewById(R.id.txtAnswerMap);
                setUpMaps(R.id.map_screen_frag);

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
     * Logic for when a question is question, if it is finished then it will get the answer and
     * display it on a map
     *
     * @param answer the answer that was given by the user
     */
    private void nextQuestionRequest(String answer) {
        if (question_controller.isFinished()) {

            this.overallanswer = question_controller.getAnswer();
            this.screen = Screen.MAP_SCREEN;
            this.question_controller = null;
            this.channel = null;
            this.displayMap= true;
            setScreen();
        } else {
            channel.setAnswer(answer);
            waitForQuestion();
        }
    }

    /**
     * Waits untill the question becomes avalible.
     * Then displats the question
     */
    private void waitForQuestion() {
        while (!channel.canGetQuestion()) {
         //wait for answer
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        setQuestion();
    }


    /**
     * Sets the question and the items to go with that question
     *
     */
    private void setQuestion(){
        if(screen == Screen.QUESTION_SCREEN) {
            this.lblQuestionAsked.setText(channel.getQuestion());
            if(this.channel.getType() == QuestionType.MAP){
                //TODO set map up for questions
            }else{
                (findViewById(R.id.questions_map_frag)).setVisibility(View.INVISIBLE);
            }
        }else{}
    }


    private void setUpMaps(int fragementview) {

        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        map_fragment = (SupportMapFragment) fm.findFragmentById(fragementview);
        if (map_fragment == null) {
            map_fragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(fragementview, map_fragment)
                    .commit();
            map_fragment.getMapAsync(this);
        }
    }

    /**
     * Displays a map at the location specified
     *
     * @param latitude  the latitude of the location
     * @param longitude the longitude of the location
     * @param zoom      the desired zoom
     */
    private void displayMap(int latitude, int longitude, int zoom) {
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude)).zoom(10).build();
        mMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

        // create marker
        MarkerOptions marker = new MarkerOptions().position(new LatLng(
                latitude, longitude));
        // ROSE color icon
        marker.icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
        // adding marker
        mMap.addMarker(marker);

        // check if map is created successfully or not
        if (mMap == null) {
            Toast.makeText(getApplicationContext(),
                    "Unable to show location", Toast.LENGTH_SHORT)
                    .show();
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (this.displayMap) {
            this.displayMap(this.latitude, this.longitude, this.zoom);

        }

    }


}