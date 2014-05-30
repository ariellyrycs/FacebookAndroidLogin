package com.facebook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.facebook.widget.LoginButton;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

public class MainActivity extends Activity implements View.OnClickListener {
    //private static final String URL_PREFIX_FRIENDS = "https://graph.facebook.com/oauth/authorize?type=user_agent&client_id=1490337134512290&redirect_uri=https://www.facebook.com/connect/login_success.html&scope=email&access_token=";
    private static final String URL_PREFIX_FOR_LOGIN = "https://graph.facebook.com/me?fields=name,email,picture.width(800).height(800)&format=json&access_token=";
    static TextView textName;
    static TextView textEmail;
    static ImageView imageViewpic;
    private LoginButton buttonLogin;
    private Session.StatusCallback statusCallback = new SessionStatusCallback();
    private Button buttonLogout;
    static String urlTemp;
    static HttpClient client;
    private LinearLayout unlogged;
    static MainActivity context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeVars();

        Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        Session session = Session.getActiveSession();
        if (session == null) {
            if (savedInstanceState != null) {
                session = Session.restoreSession(this, null, statusCallback, savedInstanceState);
            }
            if (session == null) {
                session = new Session(this);
            }
            Session.setActiveSession(session);
            if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
                session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
            }
        }
        updateView();
    }

    private void initializeVars(){
        buttonLogin = (LoginButton) findViewById(R.id.buttonLogin);
        buttonLogin.setReadPermissions("email");
        buttonLogout = (Button) findViewById(R.id.buttonLogout);
        buttonLogin.setOnClickListener(this);
        buttonLogout.setOnClickListener(this);
        textName = (TextView) findViewById(R.id.name);
        textEmail = (TextView) findViewById(R.id.email);
        imageViewpic = (ImageView) findViewById(R.id.picture);
        unlogged = (LinearLayout) findViewById(R.id.unlogged);
        context = MainActivity.this;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonLogout:
                onClickLogout();
                break;
            case R.id.buttonLogin:
                onClickLogin();
                break;
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        Session.getActiveSession().addCallback(statusCallback);
    }

    @Override
    public void onStop() {
        super.onStop();
        Session.getActiveSession().removeCallback(statusCallback);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
        updateView();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Session session = Session.getActiveSession();
        Session.saveSession(session, outState);
    }



    private void onClickLogin() {
        Session session = Session.getActiveSession();
        if (!session.isOpened() && !session.isClosed()) {
            session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
        } else {
            Session.openActiveSession(this, true, statusCallback);
        }
    }

    private void onClickLogout() {
        Session session = Session.getActiveSession();
        if (!session.isClosed()) {
            session.closeAndClearTokenInformation();
        }
        updateView();
    }

    private class SessionStatusCallback implements Session.StatusCallback {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
        }
    }

    void updateView() {
        Session session = Session.getActiveSession();
        if (session.isOpened()) {
            urlTemp = URL_PREFIX_FOR_LOGIN + session.getAccessToken();
            client = new DefaultHttpClient();
            unlogged.setVisibility(View.INVISIBLE);
            new Read().execute("url");
        } else {
            unlogged.setVisibility(View.VISIBLE);
            imageViewpic.setImageBitmap(null);
            textName.setText("");
            textEmail.setText("");
        }
    }
}




