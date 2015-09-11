package com.betasoft.ToyotaMobi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.betasoft.ToyotaMobi.JavaBeans.UserInfoBean;
import com.betasoft.ToyotaMobi.fe.MainActivity;
import com.betasoft.ToyotaMobi.fe.NavigationMain;
import com.betasoft.ToyotaMobi.fe.Phone_verification;
import com.betasoft.ToyotaMobi.fe.SessionManager;
import com.parse.ParseAnalytics;
import com.parse.ParsePush;

public class SplashActivity extends Activity {
    ImageView splashImageView;
    public static int userType = 0; //defines type of user as mechanic or user
    public static String mechanicId = "2";  //mechanic type is used as mechanic id for all mechanic;
    boolean splashloading;
    WebView wv;                            // for loading progress bar
    public static UserInfoBean usersDetailBean;
    RelativeLayout relative;
    public static SessionManager session; //creating session for user
    ParsePush push;

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        PACKAGE_NAME =getApplicationContext().getPackageName();
        ParseAnalytics.trackAppOpened(getIntent());
        push = new ParsePush();
        push.setChannel("Everyone");
        splashImageView = (ImageView) findViewById(R.id.imgview);
        wv = (WebView) findViewById(R.id.web);
        wv.loadUrl("file:///android_asset/loader.gif");
        session = new SessionManager(this);
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            public void run() {
                splashloading = false;
                if (session.isLoggedIn()) // checking for user login
                {
                    usersDetailBean = session.getObjectTaskBean();
                    userType = usersDetailBean.userType;
                    if (session.getStatus()) // if status true directly goes to main page of app
                    {
                        Intent i = new Intent(SplashActivity.this, NavigationMain.class);
                        startActivity(i);
                        finish();
                    } else {            // otherwise goes for phone verification
                        Intent i = new Intent(SplashActivity.this, Phone_verification.class);
                        startActivity(i);
                        finish();

                    }
                } else

                { //if user not login then it take user to app guide
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }

            }

        }, 2000);
    }

    public static String PACKAGE_NAME;

    public static void rateApp(final Context ctx) {
        final String appPackageName = PACKAGE_NAME;
        Log.d("Package ",appPackageName);
        try {
            ctx.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            ctx.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    public  static void sendFeedback(Context ctx){
        String[] TO = {"support@toyotamobi.com","rnyakahuma@gmail.com"};
        //String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        //emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Users Of Toyota Mobi App Feedback");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "We love hearing from you . . .");

        ctx.startActivity(emailIntent);
    }

}

