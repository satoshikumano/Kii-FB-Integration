package com.pgtest.example.kii.fb3_integration;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.kii.cloud.storage.Kii;
import com.kii.cloud.storage.KiiUser;
import com.kii.cloud.storage.callback.KiiSocialCallBack;
import com.kii.cloud.storage.social.KiiSocialConnect;
import com.kii.cloud.storage.social.connector.KiiSocialNetworkConnector;

public class MainActivity extends ActionBarActivity {

    private Button button;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Kii.initialize("9ab34d8b", "7a950d78956ed39f3b0815f0f001b43b", Kii.Site.JP);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.button);
        textView = (TextView) findViewById(R.id.textView);
        updateView();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
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
    }

    private void updateView() {
            // FB login succeeded. Login to Kii with obtained access token.
            Bundle options = new Bundle();
            String accessToken = ""; //TODO: obtain access token.
            options.putString("accessToken", accessToken);
            options.putParcelable("provider", KiiSocialNetworkConnector.Provider.FACEBOOK);
            KiiSocialNetworkConnector conn = (KiiSocialNetworkConnector) Kii.socialConnect(KiiSocialConnect.SocialNetwork.SOCIALNETWORK_CONNECTOR);
            conn.logIn(this, options, new KiiSocialCallBack() {
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

            button.setText("LOGOUT");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: implement FB Logout if necessary.
                    KiiUser.logOut();
                }
            });
    }

}
