package se3a04.twentyonequestions.Controller.Experts;

import android.graphics.Bitmap;
import android.util.Log;

import java.lang.reflect.Array;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeoutException;

import se3a04.twentyonequestions.Controller.Expert;
import se3a04.twentyonequestions.MessagePassing.MapLocation;

/**
 * LocationExpert
 *      This expert guesses the unknown location using an algorithmic analysis of
 *      geographical locations
 */
public class LocationExpert extends Expert {

    /**
     * Row
     *      This class holds a record of rows from a database holding locations of major cities
     *      in the US and Canada
     */
    private class Row{
        /**
         * Fields
         *      name: name of city
         *      population: population of city
         *      lat: latitude of city
         *      lon: longitude of city
         */
        private String name;
        private int population;
        private double lat, lon;

        /**
         * Constructor
         */
        public Row(String line){
            String[] spl = line.split(",");
            this.name = spl[0];
            this.population = Integer.parseInt(spl[2]);
            this.lat = Double.parseDouble(spl[3]);
            this.lon = Double.parseDouble(spl[4]);
        }

        /**
         * compareTo
         * @param other: row to comapre to
         * @return: int indicating order for sorting
         */
//        public int compareTo(Row other){
//            double diff = this.getLon() - other.getLon();
//            if(diff == 0) return 0;
//            if(diff > 0) return 1;
//            if (diff < 0) return -1;
//        }

        /**
         * Accessors
         */
        public String getName(){return this.name;}
        public int getPopulation(){return this.population;}
        public double getLat(){return this.lat;}
        public double getLon(){return this.lon;}
    }

    private final String DATA_TABLE ="Locations";
    private final String QUESTIONS_TABLE = "LocationExpertTable";
    private String[] children = {"1"};
    private String current = "1";
    private String state_prov;
    private ArrayList<Row> data_rows;
    private boolean done = false;
    private boolean state_found = false;
    private Row current_row;


    /**
     * add
     *      The add method adds the question and answer to the lists
     * @param question: String holding the question
     * @param answer: String holding the answer to question
     */
    @Override
    public void add(String question, String answer){
        questions.add(question);
        answers.add(answer);

    }

    /**
     * getQuestion method returns the next question to ask the user
     * @return: String holding the question
     * @throws: TimeOutException if the server cannot be reached in time
     */
    @Override
    public String getQuestion() throws TimeoutException {
        if(!state_found){
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

            // If the child is 0, we have the province/state


            String query = getQuestionQuery("Question,LeftChild,RightChild", "ID=" + "'" + nextChild + "'");

            String raw = this.excuteQuery(query).trim().replace("\n", "");
            Log.e("raw", raw);
            if(!raw.equals("") || raw == "0") {
                String[] raw_parsed = raw.split(",");
                try {
                    current = nextChild;
                    this.children = new String[]{raw_parsed[1], raw_parsed[2]};

                }catch (ArrayIndexOutOfBoundsException a){
                    this.children = new String[]{null,null};
                    done = true;
                }

                if(this.children[0].equals("0")){
                    state_found = true;
                    Log.d("[PROVINCE BEFORE]", current);
                    state_prov = getStateProv(current);
                    Log.d("[PROVINCE AFTER]", state_prov);
                    constructRowsList();
                    return getLocalQuestion();
                }
                Log.e("question", raw_parsed[0]);
                return raw_parsed[0];
            }else{
                done = true;
                return null;
            }
        }else{
            // Code in here is when expert knows the state
            String recent_answer = answers.get(this.answers.size() - 1);
            ArrayList<Row> copy = (ArrayList<Row>) data_rows.clone();
            if(recent_answer.equals("yes")){
                for(Row r : data_rows){
                    if(r.getLon() < current_row.getLon()) copy.remove(r);
                }
            }else{
                for(Row r : data_rows){
                    if(r.getLon() >= current_row.getLon()) copy.remove(r);
                }
            }data_rows = copy;
            return getLocalQuestion();
        }
    }

    private String getLocalQuestion(){
        if(data_rows.size() == 0){
            done = true;
            Log.e("[LocationExpert]", "Critical error");
        }
        if(data_rows.size() == 1) {
            done = true;
        }
        int index = 0;
        double lon_avg = 0;
        for(Row r : data_rows) lon_avg += r.getLon();
        lon_avg = lon_avg / data_rows.size();
        while(data_rows.get(index++).getLon() < lon_avg);
        for(Row r : data_rows) Log.d("[NAME]", r.getName());
        try{
            current_row = data_rows.get(index);
        }catch (IndexOutOfBoundsException ioobe){
            current_row = data_rows.get(index - 1);
        }
        return "Are you in " + current_row.getName() + " or to the east?";
    }

    @Override
    public MapLocation getMap() {
        return new MapLocation(current_row.getLat(), current_row.getLon(), 10);
    }

    /**
     * IGNORE
     * @returns
     */
    @Override
    public Bitmap getImage() {
        return null;
    }

    /**
     * Determines if the expert is finished asking questions
     * @return if the expert is done asking questions
     */
    @Override
    public boolean hasMoreQuestions() { return !done; }

    /**
     * Gives the final guess of the expert
     * @return the answer of the expert
     * @throws TimeoutException if the server could not be reached
     */
    @Override
    public String getGuess() throws TimeoutException {
        if(hasMoreQuestions()) {
            int count = 1;

            while (this.answers.size() - count > 0) {
                if (this.answers.get(answers.size() - count).equals("yes")) {
                    return excuteQuery(this.getQuestionQuery("Guess", "Question=" + "'" + this.questions.get(questions.size() - count) + "'"));
                }
                count++;
            }

            return "no result found\n";
        }else{
            return data_rows.get(0).getName() + ", " + state_prov;
        }

    }

    private String getStateProv(String node) throws TimeoutException{
        String raw_return = excuteQuery(this.getQuestionQuery("Guess", "Id=" + node.replaceAll("</br>", "")));
        return raw_return.split("</br>")[0];
    }



    private String getQuestionQuery(String attribute, String condition){
        String query= "Select " + attribute +"\n";
        query += "From " + QUESTIONS_TABLE + "\n";
        query += "Where " + condition + ";";
        return query;
    }

    private void constructRowsList(){
        try{
            String q = "select * " +
                    "from " + DATA_TABLE +
                    " where state = \"" + state_prov +
                    "\" order by longitude asc";
            String raw_query = excuteQuery(q);
            Log.d("[RAW_QUERY]", raw_query);
            String[] lines = raw_query.split("</br>");
            lines = Arrays.copyOf(lines, lines.length - 1);
            data_rows = new ArrayList<Row>();
            for(int i = 0; i < lines.length; i++)  data_rows.add(new Row(lines[i]));
        }catch(TimeoutException te){
            Log.e("DB timeout", "The database has timed out.");
        }
    }
}