package se3a04.twentyonequestions.Controller.Database;

import android.os.AsyncTask;
import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import se3a04.twentyonequestions.Controller.Database.Cryptography.MCrypt;

/**
 * Created by curtis on 25/03/16.
 */
public class DatabaseConnector extends AsyncTask<String, Void, String> {

final String url="http://ugweb.cas.mcmaster.ca/~milocj/3A04/database_controller.php";
private String result="";
private boolean dataRetrieved=false;

public DatabaseConnector(){


        }

        /**
         * States if the result is ready
         * @return if the result is ready to be read
         */
        public boolean isReady(){
                return !result.equals("");
        }

/**
 * gives the string result of the excution
 *
 * @return the result from excution
 */
public String getResult(){
        MCrypt mcrypt = new MCrypt();
        try {
                String s =   new String( mcrypt.decrypt( result ));
                return s;
                //return result;
        } catch (Exception e) {
                e.printStackTrace();
                return e.toString();
        }
}


protected String doInBackground(String...urls){

        dataRetrieved=false;

        MCrypt mcrypt = new MCrypt();


        try{
        String encrypted = MCrypt.bytesToHex(mcrypt.encrypt(urls[0]));
        HttpClient httpclient=new DefaultHttpClient();// connect to the database using the first item of the string
        HttpPost httppost=new HttpPost(url);
        ArrayList<NameValuePair>nameValuePairs=new ArrayList<NameValuePair>();//create an arraylist of required data
        nameValuePairs.add(new BasicNameValuePair("query",encrypted));//add the table
        //nameValuePairs.add(new BasicNameValuePair("query",urls[1]));//add the query
        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        result="";
        HttpResponse response=httpclient.execute(httppost);//execute the php script

        HttpEntity entity=response.getEntity();
        StatusLine sL=response.getStatusLine();
        InputStream is=entity.getContent();

        sL.getReasonPhrase();
        try{// get the jason data and return it
        BufferedReader reader=new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
        StringBuilder sb=new StringBuilder();
        String line=null;
        while((line=reader.readLine())!=null){
        sb.append(line+"\n");


        }
        is.close();


        result=sb.toString();
        return result;
        }catch(Exception e){
        Log.e("log_tag","Error converting result "+e.toString());
        //Toast.makeText(this, " Input reading fail", Toast.LENGTH_SHORT).show();

        }


        }catch(Exception e){
        Log.e("log_tag", "Error in http connection " + e.toString());


        }
        return null;
        }

//This Method is called when Network-Request finished
protected void onPostExecute(String serverData){

        }
        }