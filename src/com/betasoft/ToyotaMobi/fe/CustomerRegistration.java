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

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.betasoft.ToyotaMobi.R;
import com.betasoft.ToyotaMobi.SplashActivity;
import com.betasoft.ToyotaMobi.JavaBeans.UserInfoBean;

public class CustomerRegistration extends Activity{
	Button button;
	ArrayList<UserInfoBean> usersList;
	EditText nameet,contactet;
	String name,contact,mac,imei;
	
	JSONObject jObj;

	public static UserInfoBean userBean;
	ProgressDialog progDialog;
	String params ="http://www.toyotamobi.com/toyotamobi/admin/Webservices/add_user?";
	String responsefromserver, macAddress,imeinumber;
	 public static final String MyPREFERENCES = "MyPrefs" ;
	 SharedPreferences sharedpreferences;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
setContentView(R.layout.customer_registeration);
usersList = new ArrayList<UserInfoBean>();
button=(Button)findViewById(R.id.nextbt);

nameet=(EditText)findViewById(R.id.name);
contactet=(EditText)findViewById(R.id.contactno);
//Toast.makeText(getApplicationContext(),""+!isNetworkConnected(), Toast.LENGTH_LONG).show();
//Toast.makeText(getApplicationContext(),"internet"+!isInternetAvailable(), Toast.LENGTH_LONG).show();
sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
button.setOnClickListener(new View.OnClickListener() {
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(isNetworkAvailable())
		{
	
		 if((nameet.getText().toString().matches("") ))
				 
         {
        	Toast.makeText(getBaseContext(), "Please enter name!",1000).show();
         }
		 else if((contactet.getText().toString().matches("")))
		 {
			
			 Toast.makeText(getBaseContext(), "Please enter contact number!",1000).show();
		 }
		 else if(contactet.getText().toString().length()!=12)
		 {
			 Toast.makeText(getBaseContext(), "Please enter valid 12 Digit number!",1000).show();
		 }
		
		 else 
		 {
			 
			 SharedPreferences.Editor editor = sharedpreferences.edit();
	            
	            editor.putString("contact", contactet.getText().toString());
	            editor.commit();
//	            if(isInternetAvailable()||isNetworkConnected() )
//	            {	
	            	MyAsyncTask sendDataToServer = new MyAsyncTask();
	            	sendDataToServer.execute(params);
	            	 
//	            	 new RequestTask()
//	                 .execute("http://www.toyotamobi.com/toyotamobi/admin/Webservices/add_user?phone=9050163470&first_name=Sukhdevkamboj");
//	        	Toast.makeText(getApplicationContext(),"Type== "+Integer.toString(userType),100).show();
////	            }
//	            else
//	            {
//	            	Toast.makeText(getApplicationContext(), "You are not Connected to Internet!", Toast.LENGTH_LONG).show();
//	            }
	           
		 }
		}
        else
        {
        	Toast.makeText(getApplicationContext(), "You are not Connected to Internet!", Toast.LENGTH_LONG).show();
        }
	}
});
getActionBar().hide();
WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
WifiInfo wInfo = wifiManager.getConnectionInfo();
 macAddress = wInfo.getMacAddress();
 TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
 imeinumber=telephonyManager.getDeviceId();


	}
		private class MyAsyncTask extends AsyncTask<String, Integer, Double> {
		ProgressDialog progressDialog;
		@Override
		protected void onPreExecute() {
			progressDialog=new ProgressDialog(CustomerRegistration.this);
			 progressDialog.setTitle("Please Wait");
	            progressDialog.setMessage("Registering your number...");
	            progressDialog.show();
		};
		
        @Override
        protected Double doInBackground(String... params) {
        
        	 postText(); 
         
       
      
            return null;
        }

        protected void onPostExecute(Double result) {
        	if(progressDialog.isShowing())
            {
                progressDialog.dismiss();
            }
           // pb.setVisibility(View.GONE);
//        	if(responsefromserver.contains("1"))
//        	{
//        		Toast.makeText(getBaseContext(),
//                        "Registration Successfull!",
//        Toast.LENGTH_LONG).show();
//        	}
            
            //Toast.makeText(getBaseContext(),"mac"+macAddress, 1000).show();
            //Toast.makeText(getBaseContext(),"imei"+imeinumber, 1000).show();
           
        }

        protected void onProgressUpdate(Integer... progress) {
         //   pb.setProgress(progress[0]);
        }

        
        // this will post our text data
        private void postText(){
        	   StringBuilder builder = new StringBuilder();
               
            try{
                // url where the data will be posted
                String postReceiverUrl = "http://www.toyotamobi.com/toyotamobi/admin/Webservices/add_user";
                Log.v("toyota", "postURL: " + postReceiverUrl);
               // HttpClient
                HttpClient httpClient = new DefaultHttpClient();
                 
                // post header
                HttpPost httpPost = new HttpPost(postReceiverUrl);
         
                // add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("first_name", nameet.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("phone", contactet.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("emei", imeinumber));
                nameValuePairs.add(new BasicNameValuePair("mac",macAddress));
                 
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
         
                // execute HTTP post request
                HttpResponse response = httpClient.execute(httpPost);
                HttpEntity resEntity = response.getEntity();
//                StatusLine line = response.getStatusLine();
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
            String name = nameObj.getString("first_name");
            String contactNu = nameObj.getString("phone");
            String type = nameObj.getString("user_type");
            String id = nameObj.getString("id");
            String chesisNum = nameObj.getString("chassis_no");
            String address = nameObj.getString("address");
            String email = nameObj.getString("email");
            
            String authNumber = "";
            
           SplashActivity.userType = Integer.parseInt(type);
            userBean = new UserInfoBean(name,contactNu,SplashActivity.userType,id);
            userBean.authNumber = authNumber;
            userBean.address = address;
            userBean.chesisNum = chesisNum;
            userBean.emailAdress = email;
   		 SplashActivity.session.createLoginSession(userBean);
 		 if(SplashActivity.userType == 3)
    	{
 			authNumber = nameObj.getString("auth_number");
        	  userBean.authNumber = authNumber;
        	  SplashActivity.usersDetailBean = userBean;
        		 SplashActivity.session.createLoginSession(userBean);
      		
//        	 SendSmsAsync sendDataToServer = new SendSmsAsync();
//         	sendDataToServer.execute("http://api.infobip.com/sms/1/text/single");
        	Intent i=new Intent(CustomerRegistration.this,Phone_verification.class);
        	startActivity(i);
        	finish();
    	}
        	else
        	{
        		SplashActivity.usersDetailBean = userBean;
              	 
        		Intent i=new Intent(CustomerRegistration.this,NavigationMain.class);
        		startActivity(i);
        		finish();
        	}

//            String contactNum  = jObj.getString("id");
//            String type = jObj.getString("id");
            Log.v("Name value",name);
            

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
	
//	
//	
//	private class RequestTask extends AsyncTask<String, String, String> {
//	    // make a request to the specified url
//	    @Override
//	    protected String doInBackground(String... uri) {
//	        HttpClient httpclient = new DefaultHttpClient();
//	        HttpResponse response;
//	        String responseString = null;
//	        try {
//	            // make a HTTP request
//	            response = httpclient.execute(new HttpGet("http://www.toyotamobi.com/toyotamobi/admin/Webservices/add_user?phone=9050163470&first_name=Sukhdevkamboj"));
//	            StatusLine statusLine = response.getStatusLine();
//	            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
//	                ByteArrayOutputStream out = new ByteArrayOutputStream();
//	                response.getEntity().writeTo(out);
//	                out.close();
//	                responseString = out.toString();
//	            } else {
//	                // close connection
//	                response.getEntity().getContent().close();
//	                throw new IOException(statusLine.getReasonPhrase());
//	            }
//	        } catch (Exception e) {
//	            Log.d("Test", "Couldn't make a successful request!");
//	        }
//	        return responseString;
//	    }
//
//	    @Override
//	    protected void onPostExecute(String response) {
//	        super.onPostExecute(response);
//
//	        try {
//	            // convert the String response to a JSON object
//	            JSONObject jsonResponse = new JSONObject(response);
//
//	            // fetch the array of movies in the response
//	            JSONArray jArray = jsonResponse.getJSONArray("Member");
//
//	            // add each movie's title to a list
////	            movieTitles = new ArrayList<String>();
//
//	            //newly added
////	            movieSynopsis = new ArrayList<String>();
////	            movieImgUrl= new ArrayList<String>();
//
//	            for (int i = 0; i < jArray.length(); i++) {
//	                JSONObject data = jArray.getJSONObject(i);
//	                int phnNum  = Integer.parseInt(data.getString("user_type"));
//	              usersList.add(new UserInfoBean(data.getString("first_name"),data.getString("phone"),phnNum ));
////	                movieTitles.add(movie.getString(""));
//
//
////	                movieSynopsis.add(movie.getString(#add the synopsis var name returned by the JSON));
////	                movieImgUrl.add(movie.getString(#add the urlvar name returned by the JSON));
//
//
//	            }
//	            // refresh the ListView
////	            refreshMoviesList(movieTitles);
//	        } catch (JSONException e) {
//	            Log.d("Test", "Couldn't successfully parse the JSON response!");
//	        }
//	        for(int i=0; i<usersList.size();i++)
//           	 Toast.makeText(getApplicationContext(),"name==  "+usersList.get(i).fullName,100).show();
//           	 
//	    }
//	}
	
//	private class SendSmsAsync extends AsyncTask<String, Integer, Double> {
//
//	    @Override
//	    protected Double doInBackground(String... params) {
//	    
//	    	 postText(); 
//	     
//	   
//	  
//	        return null;
//	    }
//
//	    protected void onPostExecute(Double result) {
//	       // pb.setVisibility(View.GONE);
//	       
//	                 
//	    }
//
//	    protected void onProgressUpdate(Integer... progress) {
//	     //   pb.setProgress(progress[0]);
//	    }
//
//	    
//	    // this will post our text data
//	    private void postText(){
//	        try{
//	           
//	        	
//	        	String postReceiverUrl = "http://api.infobip.com/sms/1/text/single";
//	            Log.v("toyota", "postURL: " + postReceiverUrl);
////	             String apiKey = createApplication("https://oneapi.infobip.com/2fa/1/applications");
//	            // HttpClient
////	             Log.v("Api Key=== ",apiKey);
//	             
//	             
//	            HttpClient httpClient = new DefaultHttpClient();
//	            HttpPost httpPost = new HttpPost(postReceiverUrl);
//	            httpPost.setHeader("Host", "api.infobip.com");
//	            String authorizationString = "Basic " + Base64.encodeToString(
//                        ("rajbss" + ":" + "betasoft@123").getBytes(),
//                        Base64.NO_WRAP); //Base64.NO_WRAP flag
//	            httpPost.setHeader("Authorization", authorizationString);
//	            
////	            httpPost.setHeader("Authorization","Basic cmFqYnNzOmJldGFzb2Z0QDEyMw=="); 
//	            httpPost.setHeader("content-type","application/json");     
//	            httpPost.setHeader("Accept", "application/json");
//	            List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>(3);
//	            nameValuePairs.add(new BasicNameValuePair("from","InfoSMS"));
//	            nameValuePairs.add(new BasicNameValuePair("to", "9468429332"));
//	            nameValuePairs.add(new BasicNameValuePair("text","Hello raj How Are you?"));
//	            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//	            Log.v("Link=== ",httpPost.getURI().toString());
//	            // execute HTTP post request
//	            HttpResponse response = httpClient.execute(httpPost);
//	            HttpEntity resEntity = response.getEntity();
////	            HttpEntity resEntity1 = ((HttpResponse) response1).getEntity();
//	            if (resEntity != null) {
//	                 
//	                String responseStr = EntityUtils.toString(resEntity).trim();
////	                String responseStr1 = EntityUtils.toString(resEntity1).trim();
//	                Log.v("toyota", "Response: " +  responseStr);
////	                responsefromserver=responseStr;
//	                 
//	                // you can add an if statement here and do other actions based on the response
//	            }
////	        	} catch (UnirestException e) {
////					// TODO Auto-generated catch block
////					e.printStackTrace();
////				}
//	        } 
//	        	catch (ClientProtocolException e) {
//	            e.printStackTrace();
//	        } catch (IOException e) {
//	            e.printStackTrace();
//	        }
//	    }
//	 public String createApplication(String url)
//	 {
//		String apiKey = null;
//		 StringBuilder builder = new StringBuilder();
//         
//		try{
//		 HttpClient httpClient = new DefaultHttpClient();
//         HttpPost httpPost = new HttpPost(url);
//         httpPost.setHeader("Host", "api.infobip.com");
////         String authorizationString = "Basic " + Base64.encodeToString(
////                 ("rajbss" + ":" + "betasoft@123").getBytes(),
////                 Base64.NO_WRAP); //Base64.NO_WRAP flag
//         httpPost.setHeader("Authorization", "cmFqYnNzOmJldGFzb2Z0QDEyMw==");
//         
////         httpPost.setHeader("Authorization","Basic cmFqYnNzOmJldGFzb2Z0QDEyMw=="); 
//         httpPost.setHeader(HTTP.CONTENT_TYPE,
//                 "application/json");     
//         httpPost.setHeader("Accept", "application/json");
//         List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
//         nameValuePairs.add(new BasicNameValuePair("name","ToyotaMobi"));
//        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//	     
//         // execute HTTP post request
//         HttpResponse response = httpClient.execute(httpPost);
//         HttpEntity resEntity = response.getEntity();
////         HttpEntity resEntity1 = ((HttpResponse) response1).getEntity();
//         if (resEntity != null) {
//        	 InputStream content = resEntity.getContent();
//
//             BufferedReader reader = new BufferedReader(new InputStreamReader(content));
//
//             String line;
//             
//             while ((line = reader.readLine()) != null) 
//             {
//               builder.append(line);
//               Log.v("Build value",line);
//             }
//
//             String responseStr = EntityUtils.toString(resEntity).trim();
////             String responseStr1 = EntityUtils.toString(resEntity1).trim();
//             Log.v("toyota", "Response: " +  builder.toString());
//             JSONObject jObj=new JSONObject(builder.toString());
//             
////             responsefromserver=responseStr;
//              apiKey = jObj.getString("applicationId");
//             // you can add an if statement here and do other actions based on the response
//         }
////  
//		}
//		catch (ClientProtocolException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//         return apiKey;
//	 }
//	}
		
		public  boolean isNetworkAvailable() {
		    ConnectivityManager connectivityManager 
		          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
		}
}
