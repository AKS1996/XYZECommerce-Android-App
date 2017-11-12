package com.xyzecommerce;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.google.android.gms.plus.PlusOneDummyView.TAG;

public class ChangeDetails extends AppCompatActivity {

    private EditText mUserName,mPhoneNumber;
    private Button saveButton;
    Bundle data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_details);
//        Intent dataintent=getIntent();
//        data=dataintent.getExtras();
//        Toast.makeText(this,data.getString(Constants.PROFILE_NAME),Toast.LENGTH_LONG).show();
//        Toast.makeText(this,data.getString(Constants.CELL),Toast.LENGTH_LONG).show();

        saveButton=(Button)findViewById(R.id.save_button);
        mUserName=(EditText)findViewById(R.id.change_username_text);
        mPhoneNumber=(EditText)findViewById(R.id.change_phonenumber_text);
//        mUserName.setText(data.getString(Constants.PROFILE_NAME));
//        mPhoneNumber.setText(data.getString(Constants.CELL));
        mPhoneNumber.setText(getSharedPreferences("hello", Context.MODE_PRIVATE).getString(Constants.CELL,"0000"));
        mUserName.setText(getSharedPreferences("hello", Context.MODE_PRIVATE).getString(Constants.PROFILE_NAME,"0000"));
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDetails();
            }
        });


    }

    @Override
    protected void onStart(){
        super.onStart();

    }

    private void updateDetails() {
        if(mUserName.getText().toString().trim().length()>0 && mPhoneNumber.getText().toString().trim().length()==10){
            JSONObject post_dict=new JSONObject();
            try {
                post_dict.put("UNAME" , mUserName.getText().toString().trim());
                post_dict.put("CELL", mPhoneNumber.getText().toString().trim());
                post_dict.put("SLID",getSharedPreferences("hello",Context.MODE_PRIVATE).getString(Constants.PROFILE_ID,"o00"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (post_dict.length() > 0) {
                //TODO for update server
             new SendJsonDataToServer2().execute(String.valueOf(post_dict),"https://xyzecommerce.herokuapp.com/account.php");
//            #call to async class
            }

        }else if(mUserName.getText().toString().trim().length()>0){
            mPhoneNumber.setError("Enter valid Number");

        }else {
            mUserName.setError("Enter Username");
        }
    }
    class SendJsonDataToServer2 extends AsyncTask<String,String,String> {
        private String url_link;

        @Override
        protected String doInBackground(String... params) {
            String JsonResponse = null;
            String JsonDATA = params[0];
            url_link = params[1];
            OutputStream out = null;
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(url_link);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                //urlConnection.addRequestProperty("Content-Type","multipart/form-data");

                out = new BufferedOutputStream(urlConnection.getOutputStream());

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
                // List<NameValuePair> paras=new ArrayList<NameValuePair>();
                //  paras.add(new BasicNameValuePair("SLID","2043761745859427"));
//            Uri.Builder builder= new Uri.Builder()
//                    .appendQueryParameter("SLID","2043761745859427");
//            String query=builder.build().getEncodedQuery();
                writer.write(JsonDATA);
                //   Log.i("QUERY",query);
                Log.i("plus", JsonDATA);
                // Log.i("plus",query);
                Log.i("plus", urlConnection.toString());

                writer.flush();

                writer.close();

                out.close();

                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
//input stream
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    Log.i(TAG, JsonResponse + "empty");
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String inputLine;
                while ((inputLine = reader.readLine()) != null)
                    buffer.append(inputLine);
                if (buffer.length() == 0) {
                    // Stream was empty. No point in parsing.
                    Log.i(TAG, JsonResponse + "empty");
                    return null;
                }
                JsonResponse = buffer.toString();
//response data
                Log.i(TAG, JsonResponse);
                //send to post execute
                return JsonResponse;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(TAG, "Error closing stream", e);
                    }
                }
            }
            return null;
        }


        @Override
        protected void onPostExecute(String s) {
            if (s.contains("sucess") ) {
                SharedPreferences sharedPreferences=getSharedPreferences("hello", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString(Constants.PROFILE_NAME,mUserName.getText().toString().trim());
                editor.putString(Constants.CELL,mPhoneNumber.getText().toString().trim());
                editor.apply();

            }else {
                Toast.makeText(getBaseContext(),"derror",Toast.LENGTH_LONG).show();
            }

        }


    }
}
