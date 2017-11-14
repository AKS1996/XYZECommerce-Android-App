package com.xyzecommerce;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
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

public class SignUpPage extends AppCompatActivity {
    private EditText mInputUserName, mInputUserPhoneNumber, mInputFirstName, mInputUserPassword, mInputUserConfirmPassword;
    private String mUserName, mUserPhoneNumber, mUserPassword, mUserConfirmPassword, mFirstName;
    private Button mCreateAccount;

    private String Fb_id, Username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);

        Intent intent = getIntent();
        Fb_id = intent.getStringExtra(Utils.PROFILE_ID);
        Log.i("plus", Fb_id);
        Username = intent.getStringExtra(Utils.PROFILE_NAME);
        setAllViews();
        setAllStringValues();
        mCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });
//
    }

    private void setAllStringValues() {
        mUserName = mInputUserName.getText().toString().trim();
        mFirstName = mInputFirstName.getText().toString().trim();
        mUserPhoneNumber = mInputUserPhoneNumber.getText().toString().trim();
        mUserPassword = mInputUserPassword.getText().toString().trim();
        mUserConfirmPassword = mInputUserConfirmPassword.getText().toString().trim();
    }

    private void setAllViews() {

        mInputUserName = findViewById(R.id.user_name);
        mInputFirstName = findViewById(R.id.first_name);
        mInputUserName.setText(Username);
        mInputUserPhoneNumber = findViewById(R.id.user_phone_number);
        mInputUserPassword = findViewById(R.id.user_password);
        mInputUserConfirmPassword = findViewById(R.id.user_confirm_password);
        mCreateAccount = findViewById(R.id.create_account_button);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void signup() {
        if (!validate()) {
            return;
        }
        JSONObject post_dict = new JSONObject();
        try {
            setAllStringValues();
            post_dict.put("UNAME", mUserName);
            post_dict.put("FNAME", mFirstName);
            post_dict.put("SLID", Fb_id);
            post_dict.put("CELL", mUserPhoneNumber);
            post_dict.put("PWRD", mUserPassword);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (post_dict.length() > 0) {
            new SendJsonDataToServer().execute(String.valueOf(post_dict), "https://xyzecommerce.herokuapp.com/singup.php");
            savePrefrences();
        }


    }

    public void onBackPressed() {
        super.onBackPressed();
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    private boolean validate() {
        setAllStringValues();
        boolean valid = false;
        Resources resources = getResources();
        if (mUserName.isEmpty()) {
            mInputUserName.setError(resources.getString(R.string.err_msg_signup_name));
        } else if (mFirstName.isEmpty()) {
            mInputUserName.setError(resources.getString(R.string.err_msg_signup_name));
        } else if (mUserPhoneNumber.isEmpty() && mUserPhoneNumber.length() != 10) {
            mInputUserPhoneNumber.setError(resources.getString(R.string.err_msg_signup_phone_number));
        } else if (mUserPassword.isEmpty()) {
            mInputUserPassword.setError(resources.getString(R.string.err_msg_signup_password));
        } else if (!mUserConfirmPassword.equals(mUserPassword)) {
            mInputUserConfirmPassword.setError(resources.getString(R.string.err_msg_signup_confirm_password));
        } else {
            valid = true;
        }
        return valid;
    }

    public void nextActivity(String s) {

        if (s.contains("sucess")) {
            Intent i = new Intent(this, DetailsActivity.class);
            String UserName = mInputUserName.getText().toString().trim();
            String cell = mInputUserPhoneNumber.getText().toString().trim();
            SharedPreferences sharedPreferences = getSharedPreferences(Utils.SHARED_PREF, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Utils.PROFILE_NAME, UserName);
            editor.putString(Utils.CELL, cell);
            editor.apply();
            i.putExtra(Utils.PROFILE_NAME, UserName);
            i.putExtra(Utils.CELL, cell);
            startActivity(i);
            finish();
        } else {
            Toast.makeText(this, "Try Again", Toast.LENGTH_LONG).show();
        }
    }

    class SendJsonDataToServer extends AsyncTask<String, String, String> {
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
                writer.write(JsonDATA);
                Log.i("plus", JsonDATA);
                Log.i("plus", urlConnection.toString());

                writer.flush();

                writer.close();

                out.close();

                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
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

            switch (url_link) {
                case "https://xyzecommerce.herokuapp.com/singup.php":
                    nextActivity(s);
                    break;
                case "https://xyzecommerce.herokuapp.com/login.php":
                    break;
            }


        }


    }

    void savePrefrences() {
        SharedPreferences sharedPreferences = getSharedPreferences(Utils.SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Utils.LoggedIn, true);
        editor.apply();
    }
}

