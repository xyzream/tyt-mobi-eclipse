package com.betasoft.ToyotaMobi.fe.fragment;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.betasoft.ToyotaMobi.R;
import com.betasoft.ToyotaMobi.SplashActivity;
import com.betasoft.ToyotaMobi.JavaBeans.MyOrderBeanClass;
import com.betasoft.ToyotaMobi.fe.adapter.MyOrderCustomAdapter;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MyOrderList extends Fragment {
	ListView lv;
	private View mainView;
	ArrayList<MyOrderBeanClass> myOderList;
	MyOrderCustomAdapter adapter;
	String receiveEntity;
	RelativeLayout orderlayout;
	public ProgressDialog progressDialog;

	public static MyOrderList instance;
	public static MyOrderList getInstance() {
		if (instance == null) {
			instance = new MyOrderList();
		}
		return instance;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mainView = inflater.inflate(R.layout.my_order_list , null);
		myOderList = new ArrayList<MyOrderBeanClass>();
		lv=(ListView)mainView.findViewById(R.id.my_order_lv);
		orderlayout = (RelativeLayout)mainView.findViewById(R.id.my_order_layout);
		// creating the array list
//		myOderList=new ArrayList<MyOrderBeanClass>();
		adapter = new MyOrderCustomAdapter(getActivity(),myOderList);
		lv.setAdapter(adapter);
		if(isNetworkAvailable())
		{
		MyServiceReceiveData myserrecdata=new  MyServiceReceiveData();
	  	myserrecdata.execute();
	}
	else
	{
		Toast.makeText(getActivity().getApplicationContext(),"Sorry!! You are not Connected to Internet!",Toast.LENGTH_LONG).show();
	}
		return mainView;
	}
	
	public void setNoOrderLayout()
	{
		orderlayout.setVisibility(View.VISIBLE);
		TextView orderTV = (TextView)mainView.findViewById(R.id.no_order_tv);
		orderTV.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				FragmentTransaction tx2 = getFragmentManager().beginTransaction();
				 tx2.replace(R.id.content_frame,Fragment.instantiate(getActivity(),"com.betasoft.ToyotaMobi.fe.fragment.SpareParts" ));
				 TextView tv1=(TextView)getActivity().getActionBar().getCustomView().findViewById(R.id.actiontitle);
			     tv1.setText("Vehicle Parts");
			 /*  ImageButton img1=(ImageButton)getActivity().getActionBar().getCustomView().findViewById(R.id.search);
			   img1.setBackgroundResource(R.drawable.search_icon);*/
				 
				 tx2.addToBackStack("vehicle");
				 tx2.commit();
			}
		});
		
	}
	private class  MyServiceReceiveData extends AsyncTask<String, String, String>{

		@Override
		protected void onPreExecute() {
			progressDialog=new ProgressDialog(getActivity());
			 progressDialog.setTitle("Please Wait");
	            progressDialog.setMessage("Fetching Your Orders...");
	            progressDialog.show();
		};
	
	
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		String status = null;
		String receiveDataUrl="http://www.toyotamobi.com/toyotamobi/admin/Webservices/client_orders?client_id="+SplashActivity.usersDetailBean.userId;
//		String receiveDataUrl="http://www.toyotamobi.com/toyotamobi/admin/Webservices/client_orders?client_id=554";
		//Log.v("link== ", receiveDataUrl);
		HttpPost httppost=new HttpPost(receiveDataUrl);
		DefaultHttpClient dhtpc=new DefaultHttpClient();
		try {
			HttpResponse httpresponse=dhtpc.execute(httppost);
			HttpEntity httpentity=httpresponse.getEntity();
			if (httpentity!=null) {
				receiveEntity=EntityUtils.toString(httpentity);
				JSONObject jObj = new JSONObject(receiveEntity);
				JSONArray jsonArray = jObj.getJSONArray("TblOrder");
				for(int i=0; i<jsonArray.length(); i++)
				{
					JSONObject ob = jsonArray.getJSONObject(i);
					if(ob.getString("sparepart_items")!=null)
					{
						myOderList.add(new MyOrderBeanClass(ob.getString("sparepart_items"),ob.getString("total_price") , ob.getString("order_status"), ob.getString("created_time")));	
					}
					
				}
				status = "Success";
			}
		} 
		catch (ClientProtocolException e) {
			status = "Error";
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			status = "Error";
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			status = "Error";
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return status;
	}
	protected void onPostExecute(String receiveDataUrl) {
		// TODO Auto-generated method stub
		if(progressDialog.isShowing())
        {
            progressDialog.dismiss();
        }
		adapter.notifyDataSetChanged();
		lv.invalidate();
		if(receiveDataUrl.matches("Error"))
		{
			setNoOrderLayout();
		}
	}

}
	
	public  boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
}