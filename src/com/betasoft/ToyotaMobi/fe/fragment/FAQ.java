package com.betasoft.ToyotaMobi.fe.fragment;

import android.app.Fragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.RenderPriority;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.betasoft.ToyotaMobi.R;

public class FAQ extends Fragment implements OnClickListener {

	private static final String TAG = "Register";

	private View mainView;

	private TextView txtInfo;

	public static FAQ instance;
	Button nextbtn,engineBtn,maintenanceBtn,vehicleBtn;
WebView webView;
	public static FAQ getInstance() {
		if (instance == null) {
			instance = new FAQ();
		}
		return instance;
	}
	/*Button tv = (Button) getActivity().getActionBar().getCustomView()
            .findViewById(R.id.backbutton); 
*/
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		/*Button tv = (Button) getActivity().getActionBar().getCustomView()
	            .findViewById(R.id.backbutton); 
		tv.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});*/
		mainView = inflater.inflate(R.layout.webview, null);
		if(isNetworkAvailable())
		{
		webView = (WebView) mainView.findViewById(R.id.webView1);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(new WebViewClient());
		
		webView.getSettings().setRenderPriority(RenderPriority.HIGH);
		webView.getSettings().setAppCacheEnabled(true); 
		//webView.loadUrl("http://192.168.1.11/accordions/buttons.php");
		webView.loadUrl("http://toyotamobi.com/faq/buttons.php");
		
//	tv
//
//			
//			mainView = inflater.inflate(R.layout.faq_screen, null);
//			TextView tv=(TextView)getActivity().getActionBar().getCustomView().findViewById(R.id.actiontitle);
//		      tv.setText("FAQ");
//		/*      
//		      ImageButton img=(ImageButton)getActivity().getActionBar().getCustomView().findViewById(R.id.search);
//		         img.setBackgroundResource(android.R.color.transparent);*/
//			engineBtn=(Button)mainView.findViewById(R.id.engine);
//			maintenanceBtn=(Button)mainView.findViewById(R.id.Maintenance);
//			vehicleBtn=(Button)mainView.findViewById(R.id.Vehicle);
//		//	nextbtn.setOnClickListener(this);
//	engineBtn.setOnClickListener(new View.OnClickListener() {
//		
//		@Override
//		public void onClick(View v) 
//		{
//			Bundle b=new Bundle();
//			b.putString("Category","1");
////			Fragment f=new Fragment();
//			
////			f.setArguments(b);
//			Fragment fragment = new ShowFAQs();
//			fragment.setArguments(b);
//	        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
//	        fragmentTransaction.replace(R.id.content_frame, fragment);
//	        fragmentTransaction.addToBackStack(null);
//	        fragmentTransaction.commit();
//			
////		Toast.makeText(getActivity().getApplicationContext(),"Engine",100).show();
//		}
//	});
//maintenanceBtn.setOnClickListener(new View.OnClickListener() {
//		
//		@Override
//		public void onClick(View v) 
//		{
//			Bundle b=new Bundle();
//			b.putString("Category","2");
////			Fragment f=new Fragment();
//			
////			f.setArguments(b);
//			Fragment fragment = new ShowFAQs();
//			fragment.setArguments(b);
//	        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
//	        fragmentTransaction.replace(R.id.content_frame, fragment);
//	        fragmentTransaction.addToBackStack(null);
//	        fragmentTransaction.commit();
//			
////			Toast.makeText(getActivity().getApplicationContext(),"Engine",100).show();
//		}
//	});
//vehicleBtn.setOnClickListener(new View.OnClickListener() {
//	
//	@Override
//	public void onClick(View v) 
//	{
//		
//		Bundle b=new Bundle();
//		b.putString("Category","3");
////		Fragment f=new Fragment();
//		
////		f.setArguments(b);
//		Fragment fragment = new ShowFAQs();
//		fragment.setArguments(b);
//        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
//        fragmentTransaction.replace(R.id.content_frame, fragment);
//        fragmentTransaction.addToBackStack(null);
//        fragmentTransaction.commit();
////		Toast.makeText(getActivity().getApplicationContext(),"Engine",100).show();
//	}
//});
		
	}
	else
	{
		Toast.makeText(getActivity().getApplicationContext(),"Sorry!! You are not Connected to Internet!",Toast.LENGTH_LONG).show();
	}
		
		return mainView;
	}

@Override
public void onActivityCreated(Bundle savedInstanceState) {
	super.onActivityCreated(savedInstanceState);
	
}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onClick(View v) {
	//	FragmentTransaction tx1 = getFragmentManager().beginTransaction();
      //  tx1.replace(R.id.content_frame,Fragment.instantiate(getActivity(),"com.betasoft.ToyotaMobi.fe.fragment.MainMenu" ));
      //  tx1.commit();		
	}

	
	
	public  boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
}

