package com.example.suraj.philomath;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class welcome extends AppCompatActivity {
    int error = 0;

    ArrayList<String> exams = new ArrayList<>();
    ArrayList<String> exam_link = new ArrayList<>();
    RecyclerView recyclerView;

    ArrayList<FoodModel> foodsList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        recyclerView = findViewById(R.id.rv);
        new GetExams().execute();
    }

    private class GetExams extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //  Toast.makeText(MainActivity.this, "Json Data is downloading", Toast.LENGTH_LONG).show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            String s = "";
            try {
                String myString = "http://192.168.137.1/cloudserver/Recycle.php?table=exams";

                URL url = new URL(myString );

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(url.openStream()));

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    s += inputLine;
                }
                in.close();

            } catch (Exception e) {

                error = 1;
            }

            if (!s.isEmpty()) {
                try {
                    JSONArray jsonarray = new JSONArray(s);
                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject jsonobject = jsonarray.getJSONObject(i);
                        String name = jsonobject.getString("Exam_Name");
                        exams.add(name);
                        String link = jsonobject.getString("Folder_link");
                        exam_link.add(link);


                    }


                } catch (final JSONException e) {

                    error = 1;
                }

            } else {
                error = 1;

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (error == 0) {
                int[] images = {R.drawable.book, R.drawable.download, R.drawable.exam1, R.drawable.images};

                foodsList = new ArrayList<>();


                for (int i = 0; i < exams.size(); i++) {

                    foodsList.add(new FoodModel(exams.get(i), exam_link.get(i),images[i%4]));

                }



                ExamAdapter adapter = new ExamAdapter(welcome.this, foodsList);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());

                RecyclerView.LayoutManager mLayoutManager = linearLayoutManager;

                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(adapter);




            } else {

                Toast.makeText(welcome.this, "Cannot Connect to Server", Toast.LENGTH_SHORT).show();

            }
        }
    }




}

