package com.example.muetze187.kokolores;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.SharedElementCallback;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
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
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText etUsername, etPassword;
    CheckBox cbRemember;
    static boolean  cbRememberChecked;
    public static final String myPrefs = "kokolores";
    public static final String keyUsername = "userKey";
    public static final String keyPassword = "passKey";

    static String username;
    static String password;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);

        cbRemember = (CheckBox) findViewById(R.id.cbRemember);

        sharedPreferences = getSharedPreferences(myPrefs, Context.MODE_PRIVATE);
        etUsername.setText(sharedPreferences.getString(keyUsername,null));
        etPassword.setText(sharedPreferences.getString(keyPassword, null));

    }


    public void OnLogin(View view){
        username = etUsername.getText().toString();
        password = etPassword.getText().toString();
        String type = "login";
        cbRememberChecked = cbRemember.isChecked();
        if(isNetworkAvailable()){
            BackTask bt = new BackTask(this);
            bt.execute(type, username, password);

        }else{
            Toast.makeText(this, "check internet connection",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
    }

    public static String getUsername(){
        return username;
    }

    public static String getPassword(){
        return password;
    }

    public static boolean getCheckboxChecked(){
        return cbRememberChecked;
    }

    // Check all connectivities whether available or not
    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        // if no network is available networkInfo will be null
        // otherwise check if we are connected
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }


    private class BackTask extends AsyncTask<String,Void,String> {

        Context context;

        String show_url = "http://muetze187.bplaced.net/loginSecure.php";
        private ProgressDialog progressDialog;

        public BackTask(Context context){
            this.context = context;
        }


        @Override
        protected String doInBackground(String... params) {
            InputStream inputStream;
            String result = null; //STRINGBUILDER!!
            try {
                URL url = new URL(show_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setReadTimeout(15000);
                httpURLConnection.setConnectTimeout(15000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);




                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    String post_data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8") + "&"
                            + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");

                    bufferedWriter.write(post_data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();


                    inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                    StringBuilder sb = new StringBuilder();
                    String line = "";

                    while ((line = bufferedReader.readLine()) != null) {
                        //result += line;
                        sb.append(line + "\n");
                    }
                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();

                    result = sb.toString();

                    return result;





            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setCancelable(false);
            progressDialog.setTitle("Processing");
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();

            if(!aVoid.contains("[]")){

                try {
                    JSONObject root = new JSONObject(aVoid);
                    JSONObject userData = root.getJSONObject("userData");
                    String id = userData.getString("id");
                    String name = userData.getString("name");
                    String username = userData.getString("username");
                    String passowrd = userData.getString("password");

                    //Toast.makeText(context, id + " " + name + " "+ username + " " +passowrd  , Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(context, HauptActivity.class);
                    intent.putExtra("nameUser", name );
                    intent.putExtra("idUser", id);
                    context.startActivity(intent);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(context, "wrong username/password"  , Toast.LENGTH_LONG).show();
            }

        }

    }

}
