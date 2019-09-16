package com.login.app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final String PERMISSIONS = "public_profile, email";
    private CallbackManager callbackManager;
    private ProgressBar loadingProgressBar;
    private RelativeLayout facebookAccountSignInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /**
         * If you require Hashes Then call
         * CreateKeyHashes.printKeyHash(LoginActivity.this);
         */
        initObject();
        initView();
        facebookAccountSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                facebookAccountSignInButton.setVisibility(View.GONE);
                signIn();
            }
        });
    }

    private void initView()
    {
        facebookAccountSignInButton = findViewById(R.id.sign_in_using_facebook_account);
        loadingProgressBar = findViewById(R.id.loading_facebook);
    }

    private void initObject()
    {
        // Initialize the SDK before executing any other operations,
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
    }

    private void signIn()
    {
        LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList(PERMISSIONS));
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>()
                {
                    @Override
                    public void onSuccess(LoginResult loginResult)
                    {
                        AccessToken accessToken = loginResult.getAccessToken();
                        boolean isLoggedInSuccess = accessToken != null && !accessToken.isExpired();
                        if (isLoggedInSuccess)
                        {
                            getUserProfile(accessToken);
                        }
                        assert accessToken != null;
                        Log.d("response access_token", accessToken.toString());
                        Log.d(TAG, "User login successfully");
                    }

                    @Override
                    public void onCancel()
                    {
                        loadingProgressBar.setVisibility(View.GONE);
                        facebookAccountSignInButton.setVisibility(View.VISIBLE);
                        Log.d(TAG, "User cancel login");
                    }

                    @Override
                    public void onError(FacebookException exception)
                    {
                        loadingProgressBar.setVisibility(View.GONE);
                        facebookAccountSignInButton.setVisibility(View.VISIBLE);
                        Log.d(TAG, "Problem for login "+exception);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG,"onStart() call");
        ifAlreadyLoginGoToProfileActivity();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG,"onRestart() call");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG,"onPause() call");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"onResume call");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG,"onStop() call");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy() call");
    }

    private void getUserProfile(AccessToken currentAccessToken)
    {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback()
        {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response)
            {
                JSONObject json = response.getJSONObject();
                String facebookUserId, socialId, fullName, firstName, lastName, email, pictureUrl;

                try {
                    if (json != null) {
                        Log.d("response", json.toString());

                        try
                        {
                            email = json.getString("email");
                        }
                        catch (Exception e)
                        {
                            Toast.makeText(getApplicationContext(), "Sorry!!! Your email is not verified on facebook.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        facebookUserId = json.getString("id");
                        socialId = json.getString("id");
                        firstName = json.getString("first_name");
                        lastName = json.getString("last_name");
                        fullName = json.getString("name");
                        //pictureUrl="https://graph.facebook.com/"+facebookUserId+"/picture?type=large";
                        pictureUrl = object.getJSONObject("picture").getJSONObject("data").getString("url");


                        if (email == null) {
                            email = "your email is not verified on facebook";
                        }

                        goToProfileActivity(fullName, email, pictureUrl);
                        loadingProgressBar.setVisibility(View.GONE);
                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                    Log.d("response problem", "problem" + e.getMessage());
                }
            }
        });
        Bundle parameters = new Bundle();
        /**
         * If you used image with parse data then write this code
         * parameters.putString("fields", "id,name,first_name,last_name,link,email,picture.width(200)");
         */
        parameters.putString("fields", "id,name,first_name,last_name,link,email,picture.width(200)");

        /**
         * If you used image custom url then write this code
         * parameters.putString("fields", "id,name,first_name,last_name,link,email,picture");
         */
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void goToProfileActivity(String fullName,String email, String pictureUrl)
    {
        Intent main = new Intent(LoginActivity.this, ProfileActivity.class);
        main.putExtra("name",fullName);
        main.putExtra("email",email);
        main.putExtra("imageUrl",pictureUrl);
        startActivity(main);
    }

    public void ifAlreadyLoginGoToProfileActivity()
    {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        if (isLoggedIn)
        {
            loadingProgressBar.setVisibility(View.VISIBLE);
            facebookAccountSignInButton.setVisibility(View.GONE);
            getUserProfile(accessToken);
        }
    }
}