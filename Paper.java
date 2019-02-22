package com.example.suraj.philomath;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Paper extends AppCompatActivity {
    String table;
    int error=0;
    ArrayList<PaperModel> paperList;
    RecyclerView recyclerView;
    DownloadManager downloadManager;

    ArrayList<String> paper = new ArrayList<>();
    ArrayList<String> paper_link = new ArrayList<>();
    FloatingActionButton button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paper);

        Bundle extras = getIntent().getExtras();
        table = extras.getString("key");
        button=findViewById(R.id.fab);
        recyclerView = findViewById(R.id.rv_paper);
        new GetPaper().execute();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
                return;
            }
        }
        enable_button();

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 100 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)){
            enable_button();
        }else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},100);
            }
        }
    }


    private void enable_button() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialFilePicker()
                        .withActivity(Paper.this)
                        .withRequestCode(10)
                        .start();

            }
        });
    }


    ProgressDialog progress;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if(requestCode == 10 && resultCode == RESULT_OK){

            progress = new ProgressDialog(Paper.this);
            progress.setTitle("Uploading");
            progress.setMessage("Please wait...");
            progress.show();



            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {

                    File f  = new File(data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH));
                    //String content_type  = getMimeType(f.getPath());

                    String mimeType= URLConnection.guessContentTypeFromName(f.getName());
                    //  RequestBody file_body = RequestBody.create(MediaType.parse(mimeType),f);`


                    String file_path = f.getAbsolutePath();
                    OkHttpClient client = new OkHttpClient();
                    RequestBody file_body = RequestBody.create(MediaType.parse(mimeType),f);



                    RequestBody request_body = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("type",mimeType)
                            .addFormDataPart("uploaded_file",file_path.substring(file_path.lastIndexOf("/")+1), file_body)
                            .build();


                    Request request = new Request.Builder()
                            .url("http://192.168.137.1/cloudserver/save_file.php")
                            .post(request_body)
                            .build();

                    try {
                        Response response = client.newCall(request).execute();

                        if(!response.isSuccessful()){


                            //throw new IOException("Error : "+response);
                        }

                        progress.dismiss();



                    } catch (IOException e) {
                        //  e.printStackTrace();
                    }



                }


            });

            t.start();



        }
    }






    private class GetPaper extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Toast.makeText(Paper.this, "Json Data is downloading", Toast.LENGTH_LONG).show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            String s = "";
            try {
                String myString = getString(R.string.my_url);

                URL url = new URL(myString + table);

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
                        String name = jsonobject.getString("Paper");
                        paper.add(name);
                        String link = jsonobject.getString("Link");
                        paper_link.add(link);


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

                paperList = new ArrayList<>();


                for (int i = 0; i < paper.size(); i++) {

                    paperList.add(new PaperModel(paper.get(i), paper_link.get(i),images[i%4]));

                }



                PaperAdapter adapter = new PaperAdapter(Paper.this, paperList);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());

                RecyclerView.LayoutManager mLayoutManager = linearLayoutManager;

                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(adapter);




            } else {

                Toast.makeText(Paper.this, "Cannot Connect to Server", Toast.LENGTH_SHORT).show();

            }
        }
    }
    private class PaperAdapter  extends RecyclerView.Adapter<PaperAdapter.ViewHolder>{


        private Context mContext;
        private ArrayList<PaperModel> mList;
        public PaperAdapter(Context context, ArrayList<PaperModel> list){
            mContext = context;
            mList = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            // Inflate the custom layout
            View contactView = inflater.inflate(R.layout.cust_paper_layout, parent, false);

            // Return a new holder instance
            ViewHolder viewHolder = new ViewHolder(contactView);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final PaperModel contact = mList.get(position);

            // Set item views based on your views and data model
            TextView item_name = holder.item_name_paper;
            TextView item_link = holder.item_download_paper;
            ImageView item_img = holder.item_image_paper;

            item_name.setText(contact.getItem_paper());
            item_img.setImageResource(contact.getItem_image());
            item_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String pdf_url=contact.getItem_paper_link(); //YOUR URL TO PDF

                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(pdf_url));
                    startActivity(browserIntent);
                }
            });

            item_link.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    downloadManager=(DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
                    Uri uri=Uri.parse(contact.getItem_paper_link());
                    DownloadManager.Request request=new DownloadManager.Request(uri);
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    Long reference=downloadManager.enqueue(request);


                }
            });



        }


        @Override
        public int getItemCount() {
            return mList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder  {
            // Your holder should contain a member variable
            // for any view that will be set as you render a row
            public ImageView item_image_paper;
            public TextView item_name_paper;
            public TextView item_download_paper;

            // We also create a constructor that accepts the entire item row
            // and does the view lookups to find each subview
            public ViewHolder(View itemView) {
                // Stores the itemView in a public final member variable that can be used
                // to access the context from any ViewHolder instance.
                super(itemView);
//
                item_image_paper = itemView.findViewById(R.id.rv_item_img_paper);
                item_name_paper=itemView.findViewById(R.id.rv_item_name_paper);
                item_download_paper=itemView.findViewById(R.id.rv_item_download_paper);



            }


        }
    }

}
