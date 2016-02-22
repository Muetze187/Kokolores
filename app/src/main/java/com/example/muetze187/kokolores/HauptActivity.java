package com.example.muetze187.kokolores;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
    TextView textView;
    Button btSubmit, btShowList, btDelete;
    ListView listView;
    ArrayAdapter<String> adapter;
    ArrayList<String> listItems = new ArrayList<>();
    SharedPreferences sharedPreferences;
    public static final String myPrefs = "kokolores";
    public static final String keyUsername = "userKey";
    public static final String keyPassword = "passKey";
    String name;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_haupt);

        etInput = (EditText) findViewById(R.id.etInput);
        btSubmit = (Button) findViewById(R.id.btSubmit);
        btShowList = (Button) findViewById(R.id.btShowList);
        btDelete = (Button) findViewById(R.id.btDelete);

        listView = (ListView) findViewById((R.id.list));
        textView = (TextView) findViewById(R.id.textView);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice,listItems);
        listView.setAdapter(adapter);

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

                etInput.setText(listItems.get(position));

            }
        });


    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.startActivity(new Intent(this, HauptActivity.class));
    }


    public void OnAdd(View view){
        String str_Input = etInput.getText().toString();
        if(str_Input.isEmpty()){
            Toast.makeText(getApplicationContext(), "Mehr Text bitte du Joggl!", Toast.LENGTH_LONG).show();
        }else {
            String type = "add";

            BackgroundWorker backgroundWorker = new BackgroundWorker(this);
            backgroundWorker.execute(type, str_Input);

            BackTask bt = new BackTask(this);
            bt.execute();

        }
    }

    public void OnLogout(View view){
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void OnShow(View view){
        BackTask bt = new BackTask(this);
        bt.execute();
    }

    public void onUpdate(){

    }

    public void OnDelete(View view){
        String str_Input = etInput.getText().toString();
        if(str_Input.isEmpty()){
            Toast.makeText(getApplicationContext(), "Mehr Text bitte du Joggl!", Toast.LENGTH_LONG).show();
        }else {
            String type = "delete";

            BackgroundWorker backgroundWorker = new BackgroundWorker(this);
            backgroundWorker.execute(type, str_Input);

            BackTask bt = new BackTask(this);
            bt.execute();
        }
    }








    private class BackTask extends AsyncTask<Void,Void,Void> {

        Context context;
        ArrayList<String> list;
        ArrayList<String> list2;
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
                    list.add("created: " + jsonObject.getString("created"));
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
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            listItems.clear();
            listItems.addAll(list);
            adapter.notifyDataSetChanged();

        }

    }



}
