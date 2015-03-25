package com.pgtest.example.kii.fb3_integration;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.LoggingBehavior;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.kii.cloud.storage.Kii;
import com.kii.cloud.storage.KiiUser;
import com.kii.cloud.storage.callback.KiiSocialCallBack;
import com.kii.cloud.storage.social.KiiFacebookConnect;
import com.kii.cloud.storage.social.KiiSocialConnect;

public class MainActivity extends ActionBarActivity implements  Session.StatusCallback {

    private Button button;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Kii.initialize("9ab34d8b", "7a950d78956ed39f3b0815f0f001b43b", Kii.Site.JP);
        setContentView(R.layout.activity_main);

        Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);

        Session session = Session.getActiveSession();
        if (session == null) {
            if (savedInstanceState != null) {
                session = Session.restoreSession(this, null, this, savedInstanceState);
            }
            if (session == null) {
                session = new Session(this);
            }
            Session.setActiveSession(session);
            if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
                session.openForRead(new Session.OpenRequest(this).setCallback(this));
            }
        }
        button = (Button) findViewById(R.id.button);
        textView = (TextView) findViewById(R.id.textView);
        updateView(session);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Session.getActiveSession().addCallback(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Session.getActiveSession().removeCallback(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Session session = Session.getActiveSession();
        Session.saveSession(session, outState);

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
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
        if (requestCode == KiiFacebookConnect.REQUEST_CODE) {
            Kii.socialConnect(KiiSocialConnect.SocialNetwork.FACEBOOK)
                    .respondAuthOnActivityResult(requestCode, resultCode, data);
        }
    }

    private void updateView(final Session session) {
        if (session.isOpened()) {

            // FB login succeeded. Login to Kii with obtained access token.
            KiiFacebookConnect kfb = (KiiFacebookConnect) Kii.socialConnect(KiiSocialConnect
                    .SocialNetwork.FACEBOOK);
            kfb.initialize("210344862421225", null, null);
            Bundle options = new Bundle();
            String accessToken = session.getAccessToken();
            options.putString("access_token", accessToken);

            Kii.socialConnect(KiiSocialConnect.SocialNetwork.FACEBOOK).logIn(this, options,
                    new KiiSocialCallBack() {
                        @Override
                        public void onLoginCompleted(KiiSocialConnect.SocialNetwork network, KiiUser
                                user, Exception exception) {
                            if (exception != null) {
                                textView.setText("Failed to Login to Kii! " + exception
                                        .getMessage());
                            }
                            textView.setText("Login to Kii! " + user.getDisplayname());
                        }
                    });

            button.setText("LOGOUT");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Session s = Session.getActiveSession();
                    if (!session.isClosed()) {
                        session.closeAndClearTokenInformation();
                    }
                    KiiUser.logOut();
                }
            });
        } else {
            textView.setText("Login to FB");
            button.setText("LOGIN");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Session session = Session.getActiveSession();
                    if (!session.isOpened() && !session.isClosed()) {
                        session.openForRead(new Session.OpenRequest(MainActivity.this)
                                .setCallback(MainActivity.this));
                    } else {
                        Session.openActiveSession(MainActivity.this, true, MainActivity.this);
                    }
                }
            });
        }
    }

    // Session.StatusCallback
    @Override
    public void call(Session session, SessionState sessionState, Exception e) {
        updateView(session);
    }
}
