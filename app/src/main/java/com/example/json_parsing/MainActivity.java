package com.example.json_parsing;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;

import com.example.json_parsing.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    ArrayList<String> courseList; // Changed userList to courseList

    ArrayAdapter<String> listAdapter;

    Handler mainHandler = new Handler();

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initializeCourseList(); // Changed initializeUserlist to initializeCourseList
        binding.fetchDatabtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                new FetchData().start();

            }
        });
    }

    private void initializeCourseList() { // Changed initializeUserlist to initializeCourseList

        courseList = new ArrayList<>(); // Changed userList to courseList
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, courseList); // Changed userList to courseList
        binding.CourseList.setAdapter(listAdapter);
    }

    public class FetchData extends Thread {
        String data = "";

        @Override
        public void run() {

            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    progressDialog = new ProgressDialog(MainActivity.this);
                    progressDialog.setMessage("Fetching Data");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                }
            });

            try {
                URL url = new URL(" https://api.npoint.io/7aa27a901822c6920c6e");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    data = data + line;
                }

                if (!data.isEmpty()) {
                    JSONObject jsonObject = new JSONObject(data);
                    JSONArray courses = jsonObject.getJSONArray("courses"); // Changed users to courses
                    courseList.clear(); // Changed userList to courseList
                    for (int i = 0; i < courses.length(); i++) {
                        JSONObject course = courses.getJSONObject(i); // Changed user to course
                        String name = course.getString("name"); // Changed user to course
                        String duration = course.getString("duration"); // Changed user to course
                        courseList.add(name + ", Duration: " + duration);
                    }
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(progressDialog.isShowing())
                        progressDialog.dismiss();
                    listAdapter.notifyDataSetChanged();
                }
            });
        }
    }
}
