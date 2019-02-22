package com.example.suraj.philomath;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsManager;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import static android.text.TextUtils.isEmpty;


public class Signup extends Fragment implements View.OnClickListener {


    int verified=0;
    private static final int MY_PERMISSIONS_REQUEST_INTERNET =1 ;
    EditText et1,et2,et3,et4;
    String num,name,email,passwd;
    EditText ot1,ot2,ot3,ot4;

    String otpn="";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_signup, container, false);
        et1=view.findViewById(R.id.input_name);
        et2=view.findViewById(R.id.input_contact);
        et3=view.findViewById(R.id.input_email);
        et4=view.findViewById(R.id.input_password);
        ot1=view.findViewById(R.id.otp1);
        ot2=view.findViewById(R.id.otp2);
        ot3=view.findViewById(R.id.otp3);
        ot4=view.findViewById(R.id.otp4);
        Button otp=view.findViewById(R.id.otp);

        Button button=view.findViewById(R.id.btn_signup);

        button.setOnClickListener(this);
        otp.setOnClickListener(this);
        return view;

    }
    public String OTP( )
    {
        //System.out.println("Generating OTP using random() : ");
        //System.out.print("You OTP is : ");

        // Using numeric values
        String numbers = "0123456789";

        // Using random method
        Random rndm_method = new Random();

        char[] otp = new char[4];
        String o="";

        for (int i = 0; i < 4; i++)
        {
            // Use of charAt() method : to get character value
            // Use of nextInt() as it is scanning the value as int
            otp[i] =numbers.charAt(rndm_method.nextInt(numbers.length()));
            o+=otp[i];
        }

        // Toast.makeText(getContext(),o,Toast.LENGTH_SHORT).show();
        return o;
    }

    public void onClick(View view) {


        switch (view.getId()) {


            case R.id.otp:
                // do your code

                String s1=et2.getText().toString();
                if(s1.length()==10)
                {

                    String otp2=OTP();
                    num=et2.getText().toString();
                    SmsManager manager = SmsManager.getDefault();
                    manager.sendTextMessage(s1,null,otp2,null,null);
                    otpn="";
                    et2.setEnabled(false);
                    otpn=otp2;
                    verified=1;

                }
                else
                {
                    Toast.makeText(getContext(),"Please Input Valid Number",Toast.LENGTH_SHORT).show();
                }



                break;

            case R.id.btn_signup:
                // do your code


                name=et1.getText().toString();
                email=et3.getText().toString();
                passwd=et4.getText().toString();
                String input="";
                input+=ot1.getText().toString()+ot2.getText().toString()+ot3.getText().toString()+ot4.getText().toString();
                //Toast.makeText(getContext(),input+" "+otpn,Toast.LENGTH_SHORT).show();
                if(!input.equals(otpn)||verified==0)
                {

                    et2.setEnabled(true);
                    verified=0;
                    Toast.makeText(getContext(),"wrong OTP inserted...Generate new otp",Toast.LENGTH_SHORT).show();
                    ot1.setText("");
                    ot2.setText("");
                    ot3.setText("");
                    ot4.setText("");

                }
                else  if(name.length()==0)
                {
                    Toast.makeText(getContext(),"Enter your Name",Toast.LENGTH_SHORT).show();
                }
                else if(email.length()==0)
                {
                    Toast.makeText(getContext(),"Enter your Email",Toast.LENGTH_SHORT).show();
                }
                else if(passwd.length()<6)
                {
                    Toast.makeText(getContext(),"Weak Password",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    new MyTask().execute(name,num,email,passwd);



                }

                break;

            default:
                break;
        }

    }

    @SuppressLint("StaticFieldLeak")
    class MyTask extends AsyncTask {
        ProgressDialog progressDialog;

        int repeated=-1;
        String recieved_message="";
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
        protected Object doInBackground(Object[] objects)  {
            try{
                URL url=new URL("http://192.168.137.1/cloudserver/savedata.php?name="+objects[0]+"&contact="+objects[1]
                        +"&email="+objects[2]+"&passwd="+objects[3]);







                BufferedReader in = new BufferedReader(
                        new InputStreamReader(url.openStream()));

                String inputLine;
                while ((inputLine = in.readLine()) != null)
                    recieved_message+=inputLine;
                in.close();
                if(recieved_message.equals("0"))
                {
                    repeated=0;
                }
                if(recieved_message.equals("1"))
                {
                    repeated=1;
                }


            }
            catch(Exception e)
            {
                exception=1;
                //progressDialog.dismiss();
                //  Toast.makeText(getContext(),"Sorry...Cannot Connect to Server..",Toast.LENGTH_SHORT).show();

            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            progressDialog.dismiss();
            if(exception==1||repeated==-1)
            {
                Toast.makeText(getContext(),"Connection Error....",Toast.LENGTH_SHORT).show();
            }else
            if (repeated == 0) {
                TabLayout tabLayout = getActivity().findViewById(R.id.tablayout);
                tabLayout.getTabAt(0).select();
                //  Toast.makeText(getContext(),encrypt_password,Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Email address already taken", Toast.LENGTH_SHORT).show();
                et3.setText("");
            }

        }
    }


}

