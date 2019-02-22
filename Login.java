package com.example.suraj.philomath;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


public class Login extends Fragment implements View.OnClickListener {
    EditText et1,et2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_login, container, false);

        et1=view.findViewById(R.id.input_email);
        et2=view.findViewById(R.id.input_password);
        Button button=view.findViewById(R.id.btn_login);
        button.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        String s1= et1.getText().toString();
        String s2=et2.getText().toString();
        if(s1.length()==0)
        {
            Toast.makeText(getContext(),"Please Input Email",Toast.LENGTH_SHORT).show();
        }
        else if(s2.length()==0)
        {
            Toast.makeText(getContext(),"Please Input Password",Toast.LENGTH_SHORT).show();

        }
        else {



            new myTask().execute(s1, s2);
        }
    }





    public class myTask extends AsyncTask
    {
        ProgressDialog progressDialog;
        int exception=0;
        @Override
        protected void onPreExecute() {
            progressDialog=new ProgressDialog(getContext());
            progressDialog.setMessage("Please Wait.....");
            progressDialog.setCancelable(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            String res="";
            try{
                URL url=new URL("http://192.168.137.1/cloudserver/login.php?email="+objects[0]+"&passwd="+objects[1]);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(url.openStream()));




                String inputLine;
                while ((inputLine = in.readLine()) != null)
                    res+=inputLine;
                in.close();
            }
            catch (Exception e)
            {
                exception=1;
            }
            return res;


        }

        @Override
        protected void onPostExecute(Object o) {
            progressDialog.dismiss();
            if(exception==1)
            {
                Toast.makeText(getContext(),"Server Error....",Toast.LENGTH_SHORT).show();
            }
            else
            {
                if(o.toString().equals("0"))
                {
                    Toast.makeText(getContext(),"Email or Password Incorrect",Toast.LENGTH_SHORT).show();
                    et2.setText("");
                }
                else
                {
                    Intent intent=new Intent(getContext(),welcome.class);

                    startActivity(intent);

                }

            }

        }
    }
}
