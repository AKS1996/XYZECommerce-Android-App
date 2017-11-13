package com.xyzecommerce;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
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
import java.util.ArrayList;

public class ViewStatments extends AppCompatActivity {
    ListView msentMoney,mreceivedmoney;
    ArrayList<TransactionDetails> arrayList;
    ArrayList<TransactionDetails> arrayList2;
    CustomAdapter customAdapter1;
    CustomAdapter customAdapter2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_statments);
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("SLID",getSharedPreferences(Utils.SHARED_PREF, Context.MODE_PRIVATE).getString(Utils.PROFILE_ID,"0000"));


        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (jsonObject.length() > 0) {
            new SendJsonDataToServer().execute(String.valueOf(jsonObject),"https://xyzecommerce.herokuapp.com/viewstatement.php");
        }

        msentMoney=(ListView)findViewById(R.id.send_money_list);
        mreceivedmoney=(ListView)findViewById(R.id.received_money_list);
         arrayList=new ArrayList();
         customAdapter1=new CustomAdapter(this,R.layout.custom_list_view,arrayList);
        msentMoney.setAdapter(customAdapter1);
        arrayList2=new ArrayList();
         customAdapter2=new CustomAdapter(this,R.layout.custom_list_view,arrayList2);
        mreceivedmoney.setAdapter(customAdapter2);

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

            try {
                JSONObject jsonObject=new JSONObject(s);
                JSONArray jsonArray=jsonObject.getJSONArray("SENTMONEY");
                for(int i=0;i<jsonArray.length();i++){
                    TransactionDetails t=new TransactionDetails(jsonArray.getJSONObject(i).getInt("AMOUNT"),
                            jsonArray.getJSONObject(i).getString("TO"));
                    customAdapter1.add(t);
                }
                JSONArray jsonArray2=jsonObject.getJSONArray("RECEIVEDMONEY");
                for(int i=0;i<jsonArray2.length();i++){
                    TransactionDetails t=new TransactionDetails(jsonArray2.getJSONObject(i).getInt("AMOUNT"),
                            jsonArray2.getJSONObject(i).getString("FROM"));
                    customAdapter2.add(t);
                }

            } catch (JSONException e) {
                Toast.makeText(getBaseContext(),"err",Toast.LENGTH_LONG).show();

            }

        }

    }
}
