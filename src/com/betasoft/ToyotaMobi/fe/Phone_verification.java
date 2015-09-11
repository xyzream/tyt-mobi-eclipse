package com.betasoft.ToyotaMobi.fe;




import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.betasoft.ToyotaMobi.R;
import com.betasoft.ToyotaMobi.SplashActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Phone_verification extends Activity {
	Button finishBtn,resendBtn;
	EditText codeVeriftEt;
	String verifyCode;
	JSONObject jObj;
	WebView wv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.phone_verification);
		finishBtn=(Button)findViewById(R.id.finish);
		resendBtn=(Button)findViewById(R.id.resend);
		codeVeriftEt=(EditText)findViewById(R.id.edit_verification_code);
		wv=(WebView)findViewById(R.id.web);
	      wv.loadUrl("file:///android_asset/loader.gif");
		getActionBar().hide();
//		if(SplashActivity.session.getStatus())
//			Toast.makeText(getApplicationContext(),SplashActivity.usersDetailBean.authNumber,100).show();
		
//		SplashActivity.session.setStatus(false);
			
			resendBtn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(isNetworkAvailable())
					{
					ReSendSMS sendDataToServer = new ReSendSMS();
				 	sendDataToServer.execute();
				 	Toast.makeText(getApplicationContext(),"Verification Code Sent",100).show();
					}
					else
					{
						Toast.makeText(getApplicationContext(),"Sorry!! You are not Connected to Internet!",100).show();
					}
					}
			});
		finishBtn.setOnClickListener(new View.OnClickListener() 
		
		{
			@Override
			public void onClick(View v) {
//				String regex = "\\d+"; 
				// TODO Auto-generated method stub
		if(codeVeriftEt.getText().toString().isEmpty())
		{
			Toast.makeText(getApplicationContext(),"Please Enter Verification Code",100).show();
		}
		else if(codeVeriftEt.getText().toString().length()>4)
		{
			Toast.makeText(getApplicationContext(),"Please Enter 4 Digits Verification Code",100).show();
		}
		else
		{
			if(codeVeriftEt.getText().toString().equals(SplashActivity.usersDetailBean.authNumber))
			{
				
				 SplashActivity.session.createLoginSession(SplashActivity.usersDetailBean);
			 		SplashActivity.session.setStatus(true);
				Intent i=new Intent(Phone_verification.this,NavigationMain.class);
				startActivity(i);
				finish();
			}
			else
			{
				SplashActivity.session.setStatus(false);
				Toast.makeText(getApplicationContext(),"Please Enter Valid Code",100).show();
			}
			
		}
		}
		});
	}
	private class ReSendSMS extends AsyncTask<String, Integer, Double> {
		@Override
		protected void onPreExecute() {
			wv.setVisibility(WebView.VISIBLE);
			resendBtn.setEnabled(false);
		};
	    @Override
	    protected Double doInBackground(String... params) {
	    
	    	 postText(); 
	    	
	   
	  
	        return null;
	    }

	    protected void onPostExecute(Double result) {
	    	wv.setVisibility(WebView.GONE);
			resendBtn.setEnabled(true);
	       // pb.setVisibility(View.GONE);
//	    	adapter.notifyDataSetChanged();
//	       listChat.invalidate();
	                 
	    }

	    protected void onProgressUpdate(Integer... progress) {
	     //   pb.setProgress(progress[0]);
	    }

	    
	    // this will post our text data
	    private void postText(){
	    	 StringBuilder builder = new StringBuilder();
	    	try{
	            // url where the data will be posted
	            String postReceiverUrl = "http://www.toyotamobi.com/toyotamobi/admin/Webservices/resend_sms/?";
	            Log.v("toyota", "postURL: " + postReceiverUrl);
	             
	            // HttpClient
	            HttpClient httpClient = new DefaultHttpClient();
	             
	            // post header
	            HttpPost httpPost = new HttpPost(postReceiverUrl);
	     
	            // add your data
	            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	            nameValuePairs.add(new BasicNameValuePair("phone",SplashActivity.usersDetailBean.phoneNum));
	            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	            // execute HTTP post request
	            HttpResponse response = httpClient.execute(httpPost);
	            HttpEntity resEntity = response.getEntity();
	             
	            InputStream content = resEntity.getContent();

                BufferedReader reader = new BufferedReader(new InputStreamReader(content));

                String line;
                
                while ((line = reader.readLine()) != null) 
                {
                  builder.append(line);
                  Log.v("Build value",line);
                }
                
                
//                if (resEntity != null) {
//                     
//                    String responseStr = EntityUtils.toString(resEntity).trim();
//                    Log.v("toyota", "Response: " +  responseStr);
//                    responsefromserver=responseStr;
//                     
//                    // you can add an if statement here and do other actions based on the response
//                }
                 
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            try 
            {
            	 Log.v("Build value",builder.toString());
                jObj = new JSONObject(builder.toString());
                
//                for (int i = 0; i < jarray.length(); i++) 
//                {
//            JSONObject jsonElements = jarray.getJSONObject(i);

//            String id    = jObj.getString("id");
            JSONObject nameObj    = jObj.getJSONObject("Member");
            String authNum = nameObj.getString("auth_number");
           verifyCode = authNum; 
           SplashActivity.usersDetailBean.authNumber = verifyCode;
           SplashActivity.session.createLoginSession(SplashActivity.usersDetailBean);
		 
//            String contactNum  = jObj.getString("id");
//            String type = jObj.getString("id");
            

//        HashMap<String, String> hashAmbJobSearch = new HashMap<String, String>();

                    // adding each child node to HashMap key

//                    hashAmbJobSearch.put(android_J_P_ID, J_p_id);

                    // adding HashList to ArrayList

//                    ResultList_JobSearch.add(hashAmbJobSearch);
//                }
            } 

            catch (JSONException e) 
                {
                    Log.e("JSON Parser", "Error parsing data " + e.toString());
                }

            // return JSON String
           	}
	}
	public  boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
}
