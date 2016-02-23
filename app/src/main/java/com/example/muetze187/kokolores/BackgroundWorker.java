package com.example.muetze187.kokolores;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.tv.TvContract;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Muetze187 on 18.02.2016.
 */
public class BackgroundWorker extends AsyncTask<String,Void,String> {

    Context context;
    ProgressDialog progressDialog;
    String type;

    public BackgroundWorker(Context context){
        this.context = context;
    }

    ArrayList<String> list;

    @Override
    protected String doInBackground(String... params) {
        type = params[0];
        //String login_url = "http://muetze187.bplaced.net/loginSecure.php";
        String add_url = "http://muetze187.bplaced.net/add.php";
        //String show_url = "http://muetze187.bplaced.net/showList.php";
        String delete_url = "http://muetze187.bplaced.net/delete.php";
        String update_url = "http://muetze187.bplaced.net/update.php";


       if(type.equals("add")){
            try {
                String text = params[1];
                String name = params[2];

                URL url = new URL(add_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setReadTimeout(15000);
                httpURLConnection.setConnectTimeout(15000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("text", "UTF-8")+"="+URLEncoder.encode(text,"UTF-8")+"&"
                        +URLEncoder.encode("name", "UTF-8")+"="+URLEncoder.encode(name,"UTF-8");


                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));

                String result = null;
                String line;

                while ((line = bufferedReader.readLine()) != null){
                    result += line;

                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();


                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }else if(type.equals("delete")) {

            InputStream inputStream;
            String result = null;
            try{
                String id = params[1];
                String name = params[2];

                URL url = new URL(delete_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setReadTimeout(15000);
                httpURLConnection.setConnectTimeout(15000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("id", "UTF-8")+"="+URLEncoder.encode(id,"UTF-8")+"&"
                        +URLEncoder.encode("name", "UTF-8")+"="+URLEncoder.encode(name,"UTF-8");;


                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                result = "";
                String line ="";

                while ((line = bufferedReader.readLine()) != null){
                    result += line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }else if(type.equals("update")) {
           InputStream inputStream;
           String result = "";
           try{
               String text = params[1];
               String id = params[2];
               String name = params[3];

               URL url = new URL(update_url);
               HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
               httpURLConnection.setReadTimeout(15000);
               httpURLConnection.setConnectTimeout(15000);
               httpURLConnection.setRequestMethod("POST");
               httpURLConnection.setDoOutput(true);
               httpURLConnection.setDoInput(true);

               OutputStream outputStream = httpURLConnection.getOutputStream();
               BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
               String post_data = URLEncoder.encode("text", "UTF-8")+"="+URLEncoder.encode(text,"UTF-8")+"&"
                       +URLEncoder.encode("id", "UTF-8")+"="+URLEncoder.encode(id,"UTF-8")+"&"
                       +URLEncoder.encode("name", "UTF-8")+"="+URLEncoder.encode(name,"UTF-8");


               bufferedWriter.write(post_data);
               bufferedWriter.flush();
               bufferedWriter.close();
               outputStream.close();

               inputStream = httpURLConnection.getInputStream();
               BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));

               result = "";
               String line ="";

               while ((line = bufferedReader.readLine()) != null){
                   result += line;
               }
               bufferedReader.close();
               inputStream.close();
               httpURLConnection.disconnect();

               return result;

           } catch (MalformedURLException e) {
               e.printStackTrace();
           } catch (ProtocolException e) {
               e.printStackTrace();
           } catch (IOException e) {
               e.printStackTrace();
           }
       }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        list = new ArrayList<>();
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Processing");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

    }


    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        progressDialog.dismiss();
        HauptActivity.ivCheck.setVisibility(View.VISIBLE);

    }


    @Override
    protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);

    }




}
