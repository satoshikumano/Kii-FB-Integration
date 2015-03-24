package com.pgtest.example.kii.fb3_study;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.LoggingBehavior;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;


public class MainActivity extends ActionBarActivity implements  Session.StatusCallback {

    private Button button;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }

    private void updateView(final Session session) {
        if (session.isOpened()) {
            textView.setText("https://graph.facebook.com/me/friends?access_token=" + session
                    .getAccessToken());
            button.setText("LOGOUT");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Session s = Session.getActiveSession();
                    if (!session.isClosed()) {
                        session.closeAndClearTokenInformation();
                    }
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
