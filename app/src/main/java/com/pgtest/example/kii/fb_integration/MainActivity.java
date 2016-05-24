package com.pgtest.example.kii.fb_integration;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.kii.cloud.storage.Kii;
import com.kii.cloud.storage.KiiUser;
import com.kii.cloud.storage.callback.KiiSocialCallBack;
import com.kii.cloud.storage.social.KiiSocialConnect;
import com.kii.cloud.storage.social.connector.KiiSocialNetworkConnector;

public class MainActivity extends FragmentActivity {

    private TextView textView;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_main);

        Kii.initialize(Constants.Kii_APP_ID, Constants.KII_APP_KEY, Constants.KII_SITE);

        textView = (TextView) findViewById(R.id.textView);

        LoginButton fbLoginButton = (LoginButton) findViewById(R.id.facebookLoginButton);
        fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.v("FB", "loginSuccess");
                Bundle options = new Bundle();
                String accessToken = loginResult.getAccessToken().getToken();
                options.putString("accessToken", accessToken);
                options.putParcelable("provider", KiiSocialNetworkConnector.Provider.FACEBOOK);
                KiiSocialNetworkConnector conn = (KiiSocialNetworkConnector) Kii.socialConnect(KiiSocialConnect.SocialNetwork.SOCIALNETWORK_CONNECTOR);
                conn.logIn(MainActivity.this, options, new KiiSocialCallBack() {
                    @Override
                    public void onLoginCompleted(KiiSocialConnect.SocialNetwork network, KiiUser user, Exception exception) {
                        if (exception != null) {
                            textView.setText("Failed to Login to Kii! " + exception
                                    .getMessage());
                            return;
                        }
                        textView.setText("Login to Kii! " + user.getID());
                    }
                });
            }

            @Override
            public void onCancel() {
                Log.v("FB", "Cancelled.");
                textView.setText("Facebook Login has been cancelled.");
            }

            @Override
            public void onError(FacebookException error) {
                Log.v("FB", "loginFailed");
                error.printStackTrace();
                textView.setText("Facebook Login has been failed: " + error.getMessage());
            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Kii.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Kii.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == KiiSocialNetworkConnector.REQUEST_CODE) {
            Kii.socialConnect(KiiSocialConnect.SocialNetwork.SOCIALNETWORK_CONNECTOR)
                    .respondAuthOnActivityResult(requestCode, resultCode, data);
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}
