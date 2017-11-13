package com.xyzecommerce;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.CallbackManager;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

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
import java.util.concurrent.Callable;

public class LoginActivity extends AppCompatActivity {

    private CallbackManager mFacebookCallbackManager;
    private LoginButton mFacebookSignInButton;
    private TextInputLayout mInputLayoutEmail,mInputLayoutPassword;
    private EditText mInputEmail,mInputPassword;
    private Button mLoginButton;
    private TextView mForgotPassword;
    public static boolean facebookloggedIn;
    private String FB_ID,UserName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        mFacebookCallbackManager = CallbackManager.Factory.create();
        // This MUST be placed after the above two lines.
        setContentView(R.layout.activity_login);

        setAllView();

        mFacebookSignInButton = findViewById(R.id.facebook_login_button);
        mFacebookSignInButton.registerCallback(mFacebookCallbackManager,
                new FacebookCallback<LoginResult>() {
                    ProfileTracker profileTracker;

                    @Override
                    public void onSuccess(final LoginResult loginResult) {
                        //TODO: Use the Profile class to get information about the current user.
                        facebookloggedIn=true;
                        profileTracker=new ProfileTracker() {
                            @Override
                            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                                FB_ID=currentProfile.getId();
                                UserName=currentProfile.getFirstName();
                                SharedPreferences sharedPreferences=getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor=sharedPreferences.edit();
                                editor.putString(Constants.PROFILE_ID,FB_ID);
                                editor.apply();

                                JSONObject jsonObject=new JSONObject();
                                try {
                                    jsonObject.put("SLID",currentProfile.getId());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                if (jsonObject.length() > 0)
                                    new SendJsonDataToServer1().execute(String.valueOf(jsonObject),"https://xyzecommerce.herokuapp.com/login.php");
                                profileTracker.stopTracking();

                            }
                        };

                        handleSignInResult(new Callable<Void>() {
                            @Override
                            public Void call() throws Exception {
                                LoginManager.getInstance().logOut();
                                return null;
                            }
                        });
                    }

                    @Override
                    public void onCancel() {
                        handleSignInResult(null);
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d(LoginActivity.class.getCanonicalName(), error.getMessage());
                        Toast.makeText(getBaseContext(),error.getMessage(),Toast.LENGTH_SHORT).show();
                        handleSignInResult(error);
                    }
                }
        );

        View.OnFocusChangeListener focusChangeListener=new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                mInputLayoutEmail.setErrorEnabled(false);
                mInputLayoutPassword.setErrorEnabled(false);
            }
        };
        mInputEmail.setOnFocusChangeListener(focusChangeListener);
        mInputPassword.setOnFocusChangeListener(focusChangeListener);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }



    private void setAllView() {
        mInputLayoutPassword=findViewById(R.id.login_password_input_layout);
        mInputLayoutEmail=findViewById(R.id.login_email_input_layout);
        mInputEmail=findViewById(R.id.input_email);
        mInputPassword=findViewById(R.id.input_password);
        mLoginButton=findViewById(R.id.login_button);
        findViewById(R.id.signUp_link).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),SignUpPage.class);
                intent.putExtra(Constants.PROFILE_NAME,"");
                intent.putExtra(Constants.PROFILE_ID,mInputEmail.getText()+"");
                startActivity(intent);
                finish();
            }
        });
        mForgotPassword=findViewById(R.id.forgot_password);

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //  data.
        // (...)

        mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
    private void handleSignInResult(Object o) {
        if(o ==null) {
            Toast.makeText(this, "Logged in failed ", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validate() {
        boolean valid=true;
        String email=mInputEmail.getText().toString().trim();
        String password=mInputPassword.getText().toString().trim();
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mInputLayoutEmail.setError("enter a valid email address");
            mInputEmail.requestFocus();
            valid = false;
        } else if (password.isEmpty() || password.length() < 4 ) {
            mInputLayoutPassword.setError("between 4 and 10 alphanumeric characters");
            valid = false;
            mInputLayoutEmail.setErrorEnabled(false);
        } else {
            mInputLayoutEmail.setErrorEnabled(false);
            mInputLayoutPassword.setErrorEnabled(false);
        }

        return valid;
    }
    private void login() {
        if(!validate()){
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        /** TODO on login or login failed implement authentication*/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Constants.IS_USER_lOGGEDIN=true;
                progressDialog.dismiss();
            }
        },3000);

    }
    public void next_Intent(String s){
        if(s.equals("null")){
            Intent intent=new Intent(this,SignUpPage.class);
            intent.putExtra(Constants.PROFILE_NAME,UserName);
            intent.putExtra(Constants.PROFILE_ID,FB_ID);
            startActivity(intent);
            finish();
        }else{
            try {
                JSONObject jsonObject=new JSONObject(s);
                String name=jsonObject.getString("UNAME");
                String cell=jsonObject.getString("CELL");
                Intent intent=new Intent(this,DetailsActivity.class);
                SharedPreferences sharedPreferences=getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString(Constants.PROFILE_NAME,name);
                editor.putString(Constants.CELL,cell);
                editor.apply();
                startActivity(intent);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
    class SendJsonDataToServer1 extends AsyncTask<String,String,String> {
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
                    return "null";
                }
                JsonResponse = buffer.toString();
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

            switch (url_link) {
                case "https://xyzecommerce.herokuapp.com/singup.php":

                    break;
                case "https://xyzecommerce.herokuapp.com/login.php":
                    next_Intent(s);
                    break;
            }
        }
    }
}
