package com.login.app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = ProfileActivity.class.getSimpleName();

    private ImageView facebookAccountProfilePicImageView;
    private TextView facebookAccountHolderNameTextView, facebookAccountUsernameTextView;
    private Button facebookAccountSignOutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initView();
        setGoogleAccountDetail();

        facebookAccountSignOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
    }

    private void initView()
    {
        facebookAccountProfilePicImageView = findViewById(R.id.facebook_account_profile_image);
        facebookAccountHolderNameTextView = findViewById(R.id.facebook_account_full_name);
        facebookAccountUsernameTextView = findViewById(R.id.facebook_account_username);
        facebookAccountSignOutButton=findViewById(R.id.sign_out_from_facebook_account);
    }

    private void setGoogleAccountDetail()
    {
        Bundle inBundle = getIntent().getExtras();
        assert inBundle != null;

        if (inBundle != null)
        {
            String name = inBundle.get("name").toString();
            String email = inBundle.get("email").toString();
            String imageUrl = inBundle.get("imageUrl").toString();

            Picasso.with(this).load(imageUrl).error(R.drawable.placeholder).into(facebookAccountProfilePicImageView, new Callback() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "onSuccess");
                }

                @Override
                public void onError() {
                    Toast.makeText(getApplicationContext(), "Image Loading Error", Toast.LENGTH_SHORT).show();
                }
            });

            facebookAccountHolderNameTextView.setText(name);
            facebookAccountUsernameTextView.setText(email);
        }
        else
        {
            Intent intent=new Intent(ProfileActivity.this,LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    /**
     * method to do facebook sign out
     * This code clears which account is connected to the app. To sign in again, the user must choose their account again.
     */
    private void signOut()
    {
        if(AccessToken.getCurrentAccessToken() != null)
        {
            LoginManager.getInstance().logOut();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}
