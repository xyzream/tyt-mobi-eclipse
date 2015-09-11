package com.betasoft.ToyotaMobi.fe;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.betasoft.ToyotaMobi.R;
import com.betasoft.ToyotaMobi.SplashActivity;
import com.betasoft.ToyotaMobi.fe.fragment.MainMenu;
import com.betasoft.ToyotaMobi.fe.fragment.MyOrderList;
import com.betasoft.ToyotaMobi.fe.fragment.MyProfile;
import com.betasoft.ToyotaMobi.fe.fragment.TermsAndConditions;
import com.parse.ParseAnalytics;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.PushService;

public class NavigationMain extends Activity implements OnClickListener {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    ParsePush push;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    CustomDrawerAdapter adapter;
    Fragment fragment = null;
    List<DrawerItem> dataList;
    Button nextbtn;
    ParseInstallation installation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_drawer);

        // Initializing
//		SplashActivity.session.setStatus(true);
        dataList = new ArrayList<DrawerItem>();
        mTitle = mDrawerTitle = getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
                GravityCompat.START);
        if (isNetworkAvailable()) {
            //parse code starts
            ParseAnalytics.trackAppOpened(getIntent());
            PushService.setDefaultPushCallback(this, NavigationMain.class);
            installation = ParseInstallation.getCurrentInstallation();
            PushService.setDefaultPushCallback(this, NavigationMain.class);
//				  installation.put("userID",SplashActivity.usersDetailBean.userId);
//				  installation.saveInBackground();
            push = new ParsePush();
            push.setChannel("Everyone");
        } else {
            Toast.makeText(getApplicationContext(), "Sorry!! You are not Connected to Internet!", Toast.LENGTH_LONG).show();
        }
        //parse code ends


        // Add Drawer Item to dataList
        //	dataList.add(new DrawerItem(false)); // adding a spinner to the list

        //	dataList.add(new DrawerItem("My Favorites")); // adding a header to the list

        dataList.add(new DrawerItem("Home", R.drawable.ic_home));
        dataList.add(new DrawerItem("My Profile", R.drawable.ic_user));

        dataList.add(new DrawerItem("My Orders", R.drawable.ic_cart));
        dataList.add(new DrawerItem("Terms and Conditions", R.drawable.ic_action_labels));


        mDrawerLayout.closeDrawer(mDrawerList);


        adapter = new CustomDrawerAdapter(this, R.layout.custom_drawer_item,
                dataList);

        mDrawerList.setAdapter(adapter);

        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        getActionBar().setCustomView(R.layout.actionbar_layout);
        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);
        /*		try {
                    ViewConfiguration config = ViewConfiguration.get(this);
			        Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
			        if(menuKeyField != null) {
			            menuKeyField.setAccessible(true);
			            menuKeyField.setBoolean(config, false);
			        }
			    } catch (Exception ex) {
			        // Ignore
			    }*/
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.menu_icon, R.string.drawer_open, R.string.drawer_close) {

            public void onDrawerClosed(View view) {
                 invalidateOptionsMenu();

            }

            public void onDrawerOpened(View drawerView) {
					 invalidateOptionsMenu(); // creates call to

            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            SplashActivity.session.setStatus(true);

            //if (dataList.get(0).isSpinner()
            //		& dataList.get(1).getTitle() != null) {
            if (com.betasoft.ToyotaMobi.SplashActivity.userType == 2) {
//						Toast.makeText(getApplicationContext(),"Inside Navigation IF",100).show();
                if (isNetworkAvailable()) {
                    installation.put("userID", "2");
                    installation.saveInBackground();
                }
                Intent i = new Intent(NavigationMain.this, UsersListActivity.class);
                startActivity(i);
                finish();

            } else {
                if (isNetworkAvailable()) {
                    installation.put("userID", SplashActivity.usersDetailBean.userId);
                    installation.saveInBackground();
                }
                Fragment fm = new MainMenu();
                FragmentTransaction tx1 = getFragmentManager().beginTransaction();
                tx1.replace(R.id.content_frame, fm);
                TextView tx = (TextView) getActionBar().getCustomView().findViewById(R.id.actiontitle);
                tx.setText("Toyota Mobi");
                tx1.commit();
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection


        switch (item.getItemId()) {
            case R.id.action_feedback:
                SplashActivity.sendFeedback(this);
                return true;

            case R.id.action_rate:
                SplashActivity.rateApp(this);
                return true;

            default:
               // return super.onOptionsItemSelected(item);


        }
        if(mDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return false;

    }

    public void SelectItem(int possition) {

        Fragment fragment = null;
        Bundle args = new Bundle();
        //	Toast.makeText(getApplicationContext(),""+ args, Toast.LENGTH_LONG).show();


        fragment.setArguments(args);
        FragmentManager frgManager = getFragmentManager();
        frgManager.beginTransaction().replace(R.id.content_frame, fragment)
                .commit();

        mDrawerList.setItemChecked(possition, true);
        setTitle(dataList.get(possition).getItemName());
        mDrawerLayout.closeDrawer(mDrawerList);

    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        //getActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

   /* @Override
    public  boolean  onOptionsItemSelected(MenuItem item){
        if(mDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return false;
    }*/


    private class DrawerItemClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
				/*(	if (dataList.get(position).getTitle() == null) {
						SelectItem(position);
					}*/
            if (position == 0) {//mDrawerLayout.setBackgroundColor(getResources().getColor(R.color.color_primary_dark));
                Fragment fm = new MainMenu();
                FragmentTransaction tx = getFragmentManager().beginTransaction();
                tx.replace(R.id.content_frame, fm);
                TextView tv = (TextView) getActionBar().getCustomView().findViewById(R.id.actiontitle);
                tv.setText("Main Menu");
   			    /*     ImageButton img=(ImageButton)getActionBar().getCustomView().findViewById(R.id.search);
   			         img.setBackgroundResource(android.R.color.transparent);*/
                tx.commit();
                //    view.setBackgroundColor(getResources().getColor(R.color.red));
                mDrawerLayout.closeDrawers();
            }


            if (position == 1) {
                Fragment fm = new MyProfile();
                FragmentTransaction tx = getFragmentManager().beginTransaction();
                tx.replace(R.id.content_frame, fm);
                TextView tv = (TextView) getActionBar().getCustomView().findViewById(R.id.actiontitle);
                tv.setText("My Profile");
   			        /* ImageButton img=(ImageButton)getActionBar().getCustomView().findViewById(R.id.search);
   			         img.setBackgroundResource(R.drawable.edit_icon);*/
                tx.commit();
                //    view.setBackgroundColor(getResources().getColor(R.color.red));
                mDrawerLayout.closeDrawers();
                //   mDrawerLayout.setBackgroundColor(getResources().getColor(R.color.color_primary_dark));
            }

            if (position == 2) {
                Fragment fm = new MyOrderList();
                FragmentTransaction tx = getFragmentManager().beginTransaction();
                tx.replace(R.id.content_frame, fm);
                TextView tv = (TextView) getActionBar().getCustomView().findViewById(R.id.actiontitle);
                tv.setText("My Orders");
   			   /*      ImageButton img=(ImageButton)getActionBar().getCustomView().findViewById(R.id.search);
   			         img.setBackgroundResource(R.drawable.edit_icon);*/
                tx.commit();
                //    view.setBackgroundColor(getResources().getColor(R.color.red));
                mDrawerLayout.closeDrawers();
                //   mDrawerLayout.setBackgroundColor(getResources().getColor(R.color.color_primary_dark));
            }


            if (position == 3) {
                //mDrawerLayout.setBackgroundColor(getResources().getColor(R.color.color_primary_dark));
                Fragment fm = new TermsAndConditions();
                FragmentTransaction tx = getFragmentManager().beginTransaction();
                tx.replace(R.id.content_frame, fm);
                TextView tv = (TextView) getActionBar().getCustomView().findViewById(R.id.actiontitle);
                tv.setText("Terms And Conditions");
      			  /*     ImageButton img=(ImageButton)getActionBar().getCustomView().findViewById(R.id.search);
      			       img.setBackgroundResource(android.R.color.transparent);*/
                tx.commit();
                // view.setBackgroundColor(getResources().getColor(R.color.red));
                mDrawerLayout.closeDrawers();
            }


        }
    }


    @Override
    public void onClick(View v) {

    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
