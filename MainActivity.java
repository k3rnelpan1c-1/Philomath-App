package com.example.suraj.philomath;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements Runnable {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Thread(this).start();
    }

    @Override
    public void run() {
        try{
            Thread.sleep(3000);

        }catch (Exception e)
        {

        }


        Intent intent=new Intent(this,LoginSignUp.class);
        startActivity(intent);
    }
}
