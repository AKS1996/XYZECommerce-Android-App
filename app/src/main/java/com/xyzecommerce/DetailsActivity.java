package com.xyzecommerce;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.Manifest;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
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

public class DetailsActivity extends AppCompatActivity {

    private Button mTransferMoney,mViewStatement,mCheckBalance;
    private ImageButton mCallButton;
    private EditText mPhonenumber;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;

    Bundle dataIntentExtras;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent dataIntent =getIntent();
        dataIntentExtras=dataIntent.getExtras();

        setContentView(R.layout.activity_details);
        TextView slid_dateils=(TextView)findViewById(R.id.slid);
        slid_dateils.setText("Your ID  "+getSharedPreferences("hello", Context.MODE_PRIVATE).getString(Constants.PROFILE_ID,"000"));
        mCheckBalance=(Button)findViewById(R.id.check_balance);
        mViewStatement=(Button)findViewById(R.id.view_statement);
        mCheckBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jsonObject=new JSONObject();
                try {
                    jsonObject.put("SLID",getSharedPreferences("hello", Context.MODE_PRIVATE).getString(Constants.PROFILE_ID,"0000"));


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (jsonObject.length() > 0) {
                    new SendJsonDataToServer().execute(String.valueOf(jsonObject),"https://xyzecommerce.herokuapp.com/viewstatement.php");
                }
            }
        });
        mViewStatement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getBaseContext(),ViewStatments.class);
                startActivity(i);
            }
        });
        mTransferMoney=(Button)findViewById(R.id.transfer_balance);
        mTransferMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getBaseContext(),TransferActivity.class);
                startActivity(i);
            }
        });
        mCallButton=(ImageButton)findViewById(R.id.callButton);
        mCallButton.setEnabled(false);
        mPhonenumber=(EditText)findViewById(R.id.call_phone_number);

        mPhonenumber.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().trim().length()==10)
                {
                    mCallButton.setEnabled(true);
                }
                else
                {
                    mCallButton.setEnabled(false);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                String phoneNumber = String.format("tel: %s",
                        mPhonenumber.getText().toString());

                callIntent.setData(Uri.parse(phoneNumber));

                if (ActivityCompat.checkSelfPermission(getBaseContext(),
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity) getBaseContext(),
                            new String[]{Manifest.permission.CALL_PHONE},
                            MY_PERMISSIONS_REQUEST_CALL_PHONE);
                    return;
                }
                startActivity(callIntent);
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                if (permissions[0].equalsIgnoreCase(Manifest.permission.CALL_PHONE)
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this,"Permissions Needed",Toast.LENGTH_LONG).show();
                }
                return;
                }
            }
        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu;
        // this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.details_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Logging Out")
                    .setMessage("Are you sure you want to Logout?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                            LoginManager.getInstance().logOut();
//                            LoginActivity..facebookloggedIn=false;
                            finish();
                        }

                    })
                    .setNegativeButton("No", null)
                    .show();
            return true;
        }else if(id==R.id.action_change_details){

            Intent changeDetailsIntent=new Intent(this,ChangeDetails.class);
//            changeDetailsIntent.putExtra(Constants.PROFILE_NAME,dataIntentExtras.getString(Constants.PROFILE_NAME));
//            changeDetailsIntent.putExtra(Constants.CELL,dataIntentExtras.getString(Constants.CELL));
//
//            Toast.makeText(this,dataIntentExtras.getString(Constants.CELL),Toast.LENGTH_LONG).show();
//            Toast.makeText(this,dataIntentExtras.getString(Constants.PROFILE_NAME),Toast.LENGTH_LONG).show();
            startActivity(changeDetailsIntent);

        }

        return super.onOptionsItemSelected(item);
    }
    class SendJsonDataToServer extends AsyncTask<String,String,String> {
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
                    //Log.i(TAG, JsonResponse + "empty");
                    return "null";
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String inputLine;
                while ((inputLine = reader.readLine()) != null)
                    buffer.append(inputLine + "\n");
                if (buffer.length() == 0) {
                    // Stream was empty. No point in parsing.
                    // Log.i(TAG, JsonResponse + "empty");
                    return "null";
                }
                JsonResponse = buffer.toString();
//response data
                // Log.i(TAG, JsonResponse);
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
                        //Log.e(TAG, "Error closing stream", e);
                    }
                }
            }
            return null;
        }


        @Override
        protected void onPostExecute(String s) {
            Log.i("plus",s);
            int gross=200;

            try {
                JSONObject jsonObject=new JSONObject(s);
                JSONArray jsonArray=jsonObject.getJSONArray("SENTMONEY");

                for(int i=0;i<jsonArray.length();i++){
                    gross-=jsonArray.getJSONObject(i).getInt("AMOUNT");
                }

            } catch (JSONException e) {
                Toast.makeText(getBaseContext(),"err",Toast.LENGTH_LONG).show();

            }
            try {
                JSONObject jsonObject2=new JSONObject(s);
                JSONArray jsonArray=jsonObject2.getJSONArray("RECEIVEDMONEY");

                for(int i=0;i<jsonArray.length();i++){
                    gross-=jsonArray.getJSONObject(i).getInt("AMOUNT");
                }

            } catch (JSONException e) {

            }
            Toast.makeText(getBaseContext(),"BAL "+gross,Toast.LENGTH_LONG).show();
        }

        }

    }

