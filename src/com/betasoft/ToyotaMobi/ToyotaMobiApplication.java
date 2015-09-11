package com.betasoft.ToyotaMobi;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.betasoft.ToyotaMobi.app.LruBitmapCache;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

public class ToyotaMobiApplication extends Application {

	public static final String TAG = ToyotaMobiApplication.class.getSimpleName();

	private RequestQueue mRequestQueue;
	private ImageLoader mImageLoader;

	private static ToyotaMobiApplication mInstance;

	@Override
	public void onCreate() {
		super.onCreate();
		// using for intializing the app with www.parse.com
		Parse.initialize(getApplicationContext(),
				"dzAz2KHuqUsGVjaYPDAFiNZfb5IatbSlLevk1a4r",
				"tGxB2UY7db0ckqRNsqaOWY844FypekalcPrAVtw4");

		// Parse.initialize(getApplicationContext(),
		// "O6xFklRgGT6iuS8AOipy3bRYhQiA9WNHrpsliePR",
		// "cUIIXw2XgPDlkpAJwGoDpiBDqGEyItCXUI0n1uth");
		// PushService.setDefaultPushCallback(this, SplashActivity.class);
		// PushService.subscribe(this, "Everyone", SplashActivity.class);

		// saving all device tokens and installation ids into parse installation
		// class

		ParseACL defaultACL = new ParseACL();
		defaultACL.setPublicReadAccess(true);
		ParseACL.setDefaultACL(defaultACL, true);
		ParseInstallation.getCurrentInstallation().getInstallationId();
		ParseInstallation.getCurrentInstallation().saveInBackground();
		if (ParseUser.getCurrentUser() == null) {
			ParseUser.enableAutomaticUser();
		}

		mInstance = this;
	}

	public static synchronized ToyotaMobiApplication getInstance() {
		return mInstance;
	}

	public RequestQueue getRequestQueue() {
		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(getApplicationContext());
		}

		return mRequestQueue;
	}

	public ImageLoader getImageLoader() {
		getRequestQueue();
		if (mImageLoader == null) {
			mImageLoader = new ImageLoader(this.mRequestQueue,
					new LruBitmapCache());
		}
		return this.mImageLoader;
	}

	public <T> void addToRequestQueue(Request<T> req, String tag) {
		// set the default tag if tag is empty
		req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
		getRequestQueue().add(req);
	}

	public <T> void addToRequestQueue(Request<T> req) {
		req.setTag(TAG);
		getRequestQueue().add(req);
	}

	public void cancelPendingRequests(Object tag) {
		if (mRequestQueue != null) {
			mRequestQueue.cancelAll(tag);
		}
	}
}
