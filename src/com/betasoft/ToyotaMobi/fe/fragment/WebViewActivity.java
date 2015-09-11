package com.betasoft.ToyotaMobi.fe.fragment;

import com.betasoft.ToyotaMobi.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewActivity extends Activity {

	private WebView webView;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview);
		
		///////////////////////////////////////////////////////////////
//		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo info = cm.getActiveNetworkInfo();
//        if (info != null) {
//            if (!info.isConnected()) {
//
//                Toast.makeText(this,
//                        "Please check your wireless connection and try again.",
//                        Toast.LENGTH_LONG).show();
//
//            }
//
//            // if positive, fetch the articles in background
//            else{}
//                // Do your task...
//        } else {
//            Toast.makeText(this,
//                    "Please check your wireless connection and try again.",
//                    Toast.LENGTH_SHORT).show();
//        }
		////////////////////////////////////////////////////////////////////
		
		
		// If your minSdkVersion is above 11 use:
		getActionBar().setDisplayHomeAsUpEnabled(true);

		webView = (WebView) findViewById(R.id.webView1);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(new WebViewClient());
		
		webView.getSettings().setRenderPriority(RenderPriority.HIGH);
		webView.getSettings().setAppCacheEnabled(true); 
		//webView.loadUrl("http://192.168.1.11/accordions/buttons.php");
		webView.loadUrl("http://toyotamobi.com/faq/buttons.php");
		//mWebView.loadUrl("http://beta.html5test.com/");
		//String customHtml = "<html><body><h1>Hello, WebView</h1></body></html>";
		//webView.loadData(customHtml, "text/html", "UTF-8");
		//webView.goBack();//to go back with webview

	}
	
//	@Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        // Check if the key event was the Back button and if there's history
//        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
//            webView.goBack();
//            return true;
//        }
//
//        return super.onKeyDown(keyCode, event);
//    }
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)//to go back with main button
	{
	    if(event.getAction() == KeyEvent.ACTION_DOWN)
	    {
	        switch(keyCode)
	        {
	        case KeyEvent.KEYCODE_BACK:
	            if(webView.canGoBack() == true)
	            {
	                webView.goBack();
	            }
	            else
	            {
	                finish();
	            }
	            return true;
	        }
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
//	@Override
//	public void onBackPressed() {
//	    if (webView.canGoBack()) {
//	        webView.goBack();
//	        return;
//	    }
//
//	    // Otherwise defer to system default behavior.
//	    super.onBackPressed();
//	}

}