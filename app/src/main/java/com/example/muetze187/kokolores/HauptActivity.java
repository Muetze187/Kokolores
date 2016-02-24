package com.example.muetze187.kokolores;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

public class HauptActivity extends AppCompatActivity {

    EditText etInput;
    TextView textView, tvInfo, tvInfo2;
    Button btSubmit, btShowList, btDelete, btUpdate;
    ListView listView;
    static ImageView ivCheck;
    ArrayAdapter<String> adapter;
    ArrayList<String> listItems = new ArrayList<>();
    ArrayList<String> listCreated = new ArrayList<>();
    ArrayList<String> listName = new ArrayList<>();
    ArrayList<String> listID = new ArrayList<>();
    SharedPreferences sharedPreferences;
    public static final String myPrefs = "kokolores";
    public static final String keyUsername = "userKey";
    public static final String keyPassword = "passKey";
    String name;
    String id;
    String idText, createdText, createdByText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_haupt);

        etInput = (EditText) findViewById(R.id.etInput);
        btSubmit = (Button) findViewById(R.id.btSubmit);
        btShowList = (Button) findViewById(R.id.btShowList);
        btDelete = (Button) findViewById(R.id.btDelete);
        btDelete.setEnabled(false);
        btUpdate = (Button) findViewById(R.id.btUpdate);
        btUpdate.setEnabled(false);
        tvInfo = (TextView) findViewById(R.id.tvInfo);
        tvInfo2 = (TextView) findViewById(R.id.tvInfo2);
        ivCheck = (ImageView) findViewById(R.id.ivCheck);
        ivCheck.setImageResource(R.drawable.greencheck);
        ivCheck.setVisibility(View.INVISIBLE);

        listView = (ListView) findViewById((R.id.list));
        textView = (TextView) findViewById(R.id.textView);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice,listItems);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
        listView.setStackFromBottom(false);
        listView.setAdapter(adapter);

        int[] colors = {0, 0xFFFF0000, 0};
        listView.setDivider(new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, colors));
        listView.setDividerHeight(1);

        Bundle extras = getIntent().getExtras();
        name = extras.getString("nameUser");
        id = extras.getString("idUser");


        textView.setText("Hello " + name+"!" + " Get some awesome stuff uploaded!");

        if(MainActivity.getCheckboxChecked() == true){
            sharedPreferences = getSharedPreferences(myPrefs, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(keyUsername,MainActivity.getUsername());
            editor.putString(keyPassword,MainActivity.getPassword());
            editor.commit();

        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //
                // ivCheck.setImageResource(R.drawable.greencheck);
                ivCheck.setVisibility(View.INVISIBLE);
                idText = listID.get(position);
                createdText = listCreated.get(position);
                createdByText = listName.get(position);
                btDelete.setEnabled(true);
                btUpdate.setEnabled(true);
                etInput.setText(listItems.get(position));
                tvInfo.setText(createdText);
                tvInfo2.setText(createdByText);

            }
        });


    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    public void OnAdd(View view){
        ivCheck.setVisibility(View.INVISIBLE);
        String str_Input = etInput.getText().toString();
        if(str_Input.isEmpty()){
            Toast.makeText(getApplicationContext(), "Mehr Text bitte du Joggl!", Toast.LENGTH_LONG).show();
        }else {
            String type = "add";

            if(isNetworkAvailable()) {
                BackgroundWorker backgroundWorker = new BackgroundWorker(this);
                backgroundWorker.execute(type, str_Input, name);

                BackTask bt = new BackTask(this);
                bt.execute();


            }else{
                Toast.makeText(this, "internet connection lost",Toast.LENGTH_SHORT).show();
            }

        }
    }


    public void OnShow(View view){
        ivCheck.setVisibility(View.INVISIBLE);
        if(isNetworkAvailable()) {
            BackTask bt = new BackTask(this);
            bt.execute();
        }else{
            Toast.makeText(this, "internet connection lost",Toast.LENGTH_SHORT).show();
        }
    }

    public void OnUpdate(View view){
        ivCheck.setVisibility(View.INVISIBLE);
        String str_Input = etInput.getText().toString();
        if(str_Input.isEmpty()){
            Toast.makeText(getApplicationContext(), "Mehr Text bitte du Joggl!", Toast.LENGTH_LONG).show();
        }else {
            String type = "update";

            if(isNetworkAvailable()) {
                BackgroundWorker backgroundWorker = new BackgroundWorker(this);
                backgroundWorker.execute(type, str_Input, idText, name);

                BackTask bt = new BackTask(this);
                bt.execute();

            }else{
                Toast.makeText(this, "internet connection lost",Toast.LENGTH_SHORT).show();
            }

        }
    }

    public void OnDelete(View view){
        ivCheck.setVisibility(View.INVISIBLE);
        String str_Input = etInput.getText().toString();
        if(str_Input.isEmpty()){
            Toast.makeText(getApplicationContext(), "Mehr Text bitte du Joggl!", Toast.LENGTH_LONG).show();
        }else {
            String type = "delete";

            if(isNetworkAvailable()){
                BackgroundWorker backgroundWorker = new BackgroundWorker(this);
                backgroundWorker.execute(type, idText, name);

                BackTask bt = new BackTask(this);
                bt.execute();

                tvInfo.setText("");
                tvInfo2.setText("");
            }else{
                Toast.makeText(this, "internet connection lost",Toast.LENGTH_SHORT).show();
            }



        }
    }

    // Check all connectivities whether available or not
    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        // if no network is available networkInfo will be null
        // otherwise check if we are connected
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }



    private class BackTask extends AsyncTask<Void,Void,Void> {

        Context context;
        ArrayList<String> list;
        ArrayList<String> list2;
        ArrayList<String> list3;
        ArrayList<String> list4;
        String show_url = "http://muetze187.bplaced.net/showList.php";
        private ProgressDialog progressDialog;

        public BackTask(Context context){
            this.context = context;
        }


        @Override
        protected Void doInBackground(Void... params) {
            InputStream inputStream;
            String result = "";
            try{
                URL url = new URL(show_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setReadTimeout(15000);
                httpURLConnection.setConnectTimeout(15000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);

                inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));

                String line ="";

                while ((line = bufferedReader.readLine()) != null){
                    result += line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();


                JSONArray jsonArray = new JSONArray(result);
                for(int i = 0; i < jsonArray.length();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    list.add(jsonObject.getString("text"));
                    list2.add("created at: " + jsonObject.getString("created"));
                    list3.add("created by: " + jsonObject.getString("created_by"));
                    list4.add(jsonObject.getString("id"));
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
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
            list = new ArrayList<>();
            list2 = new ArrayList<>();
            list3 = new ArrayList<>();
            list4 = new ArrayList<>();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            listName.clear();
            listCreated.clear();
            listItems.clear();
            listID.clear();
            listID.addAll(list4);
            listName.addAll(list3);
            listCreated.addAll(list2);
            listItems.addAll(list);
            adapter.notifyDataSetChanged();

        }

    }



}
