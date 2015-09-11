package com.betasoft.ToyotaMobi.fe;

import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.betasoft.ToyotaMobi.JavaBeans.UserInfoBean;
import com.google.gson.Gson;

public class SessionManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "BraceTool";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    // User name (make variable public to access from outside)
    public static final String KEY_NAME = "name";

    // Email address (make variable public to access from outside)
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_ADMIN_DEVICE_ID = "865622027650956";
    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     * */
    public void createLoginSession(UserInfoBean ub){
        // Storing UserInfoBean object into shared preferences
        Gson gson = new Gson();
        String json = gson.toJson(ub);
        editor.putBoolean(IS_LOGIN, true);
        editor.putString("UserInfoBean", json);
        editor.commit();
    }
    public UserInfoBean getObjectTaskBean() {
        //getting UserInfoBean object from shared preferences
        Gson gson = new Gson();
        String json = pref.getString("UserInfoBean","");
        return gson.fromJson(json, UserInfoBean.class);

    }
    public void setStatus(boolean status)
    {
        editor.putBoolean("Verification",status);
        editor.commit();
    }
    public boolean getStatus()
    {
        return pref.getBoolean("Verification",false);
    }
    public void setBannerImageName(String name)
    {
        editor.putString("BannerImageName",name);
        editor.commit();
    }
    public String getBannerImageName()
    {
        return pref.getString("BannerImageName","");
    }
    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, NavigationMain.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }

    }



    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));

        // user password
        user.put(KEY_PASSWORD, pref.getString(KEY_PASSWORD, null));

        // admin device id
//        user.put("UserType", pref.getString("UserType", null));

        // return user
        return user;
    }
    /**
     * Clear session details
     * */
    public int getUserType()
    {
        return pref.getInt("UserType", 0);
    }

    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context,CustomerRegistration.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }

    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
}