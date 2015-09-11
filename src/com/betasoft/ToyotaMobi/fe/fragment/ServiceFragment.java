package com.betasoft.ToyotaMobi.fe.fragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.content.Intent;

import com.betasoft.ToyotaMobi.R;
import com.betasoft.ToyotaMobi.SplashActivity;

public class ServiceFragment extends Fragment{
	public static ServiceFragment instance;
	EditText dateET,timeET;
	Button bookBtn;
	TextView viewPrice,statusTV;
	View mainView;
	String responseData;
	public static ServiceFragment getInstance() {
		if (instance == null) {
			instance = new ServiceFragment();
		}
		return instance;
	}

@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
	
	mainView = inflater.inflate(R.layout.service_screen, null);
	dateET = (EditText)mainView.findViewById(R.id.service_date_edt);
	timeET = (EditText)mainView.findViewById(R.id.service_time_edt);
	bookBtn = (Button)mainView.findViewById(R.id.book_btn);
	statusTV = (TextView)mainView.findViewById(R.id.status_tv);
	viewPrice = (TextView)mainView.findViewById(R.id.view_price_tv);
	 final String params ="http://www.toyotamobi.com/toyotamobi/admin/Webservices/book_vehicle_services?";
	  
	 dateET.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
		// Process to get Current Date
		    final Calendar c = Calendar.getInstance();
	           int  mYear = c.get(Calendar.YEAR);
	            int mMonth = c.get(Calendar.MONTH);
	            int mDay = c.get(Calendar.DAY_OF_MONTH);
	 
	            // Launch Date Picker Dialog
	            DatePickerDialog dpd = new DatePickerDialog(getActivity(),
	                    new DatePickerDialog.OnDateSetListener() {
	 
	                        @Override
	                        public void onDateSet(DatePicker view, int year,
	                                int monthOfYear, int dayOfMonth) {
	                            // Display Selected date in textbox
	                            dateET.setText(dayOfMonth + "-"
	                                    + (monthOfYear + 1) + "-" + year);
	 
	                        }
	                    }, mYear, mMonth, mDay);
	            dpd.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
	            dpd.show();
	            statusTV.setVisibility(TextView.GONE);
			
        		}
	})  ;
	 timeET.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
			// Process to get Current Date
				
				final Calendar c = Calendar.getInstance();
		          int  mHour = c.get(Calendar.HOUR_OF_DAY);
		            int mMinute = c.get(Calendar.MINUTE);
		 
		            // Launch Time Picker Dialog
		            TimePickerDialog tpd = new TimePickerDialog(getActivity(),
		                    new TimePickerDialog.OnTimeSetListener() {
		 
		                        @Override
		                        public void onTimeSet(TimePicker view, int hourOfDay,
		                                int minute) {
		                            // Display Selected time in textbox
		                            timeET.setText(hourOfDay + ":" + minute);
		                        }
		                    }, mHour, mMinute, false);
		            tpd.show();
		            statusTV.setVisibility(TextView.GONE);
				
	        		}
		})  ;
	  
	bookBtn.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
		if(!(dateET.getText().toString().matches(""))  || !(timeET.getText().toString().matches("")))
		{
			boolean validDate = validateDate(dateET.getText().toString());
			boolean validTime = validateTime(timeET.getText().toString());
			if(validDate && validTime)
			{
				Calendar c = Calendar.getInstance();
				System.out.println("Current time => " + c.getTime());

				SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
				String formattedDate = df.format(c.getTime());
				Log.v("date=== ", formattedDate);
				if(dateET.getText().toString().compareTo(formattedDate)==1)
				{
				if(isNetworkAvailable())
				{
				MyAsyncTask sendDataToServer = new MyAsyncTask();
            	sendDataToServer.execute(params);
            	Toast.makeText(getActivity().getApplicationContext(),"Your Request Has been Sent",Toast.LENGTH_LONG).show();
            	statusTV.setText("Your Request Has been Sent");
            	statusTV.setVisibility(TextView.VISIBLE);
			}
			else
			{
				Toast.makeText(getActivity().getApplicationContext(),"Sorry!! You are not Connected to Internet!",Toast.LENGTH_LONG).show();
			}
				}
				else
				{
					Toast.makeText(getActivity().getApplicationContext(),"Please do not enter past date",Toast.LENGTH_LONG).show();
				}
			}
			else 
			{
				statusTV.setVisibility(TextView.GONE);
				Toast.makeText(getActivity().getApplicationContext(),"Please enter the valid Date or Time",Toast.LENGTH_LONG).show();
			}
		}
		else
		{
			statusTV.setVisibility(TextView.GONE);
			Toast.makeText(getActivity().getApplicationContext(),"All Fields are Mandatory",Toast.LENGTH_LONG).show();
		}
		}
	});
	viewPrice.setOnClickListener(new View.OnClickListener() {

		@Override
		public void onClick(View arg0) {
			Intent intent = new Intent(getActivity(), ViewPrice.class);
			startActivity(intent);


		}
	})  ;
	// TODO Auto-generated method stub
	return mainView;
}
public boolean validateDate(String date) {
	String regex = "^[0-3]?[0-9]-[0-3]?[0-9]-(?:[0-9]{2})?[0-9]{2}$";
	Pattern pattern = Pattern.compile(regex);
	Matcher matcher = pattern.matcher(date);
	if(matcher.matches())
	{
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    try {
        sdf.parse(date);
        return true;
    }
    catch(Exception ex) {
        return false;
    }
	}
	else
	{
		return false;
	}
}

public boolean validateTime(String time)
{
	String regex = "([01]?[0-9]|2[0-3]):[0-5][0-9]";
	Pattern pattern = Pattern.compile(regex);
	Matcher matcher = pattern.matcher(time);
	if(matcher.matches())
	{
	SimpleDateFormat formatter = new SimpleDateFormat("hh:mm");
	try {
	   formatter.parse(time);
	   return true;
	}
	catch(Exception e) {
	    // Show message;
	    return false;
	}
	}
	else
	{
	return false;	
	}
	
}

private class MyAsyncTask extends AsyncTask<String, Integer, Double> {

    @Override
    protected Double doInBackground(String... params) {
    
    	 postText(); 
     
   
  
        return null;
    }

    protected void onPostExecute(Double result) {
       // pb.setVisibility(View.GONE);
//    	if(responsefromserver.contains("1"))
//    	{
//    		Toast.makeText(getBaseContext(),
//                    "Registration Successfull!",
//    Toast.LENGTH_LONG).show();
//    	}
        
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
            String postReceiverUrl = "http://www.toyotamobi.com/toyotamobi/admin/Webservices/book_vehicle_services?";
            Log.v("toyota", "postURL: " + postReceiverUrl);
           // HttpClient
            HttpClient httpClient = new DefaultHttpClient();
             
            // post header
            HttpPost httpPost = new HttpPost(postReceiverUrl);
     
            // add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("user_id",SplashActivity.usersDetailBean.userId));
            nameValuePairs.add(new BasicNameValuePair("service_date_time",dateET.getText().toString()+"/"+timeET.getText().toString()));
            
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
     
            // execute HTTP post request
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity resEntity = response.getEntity();
//            StatusLine line = response.getStatusLine();
            InputStream content = resEntity.getContent();

            BufferedReader reader = new BufferedReader(new InputStreamReader(content));

            String line;
            
            while ((line = reader.readLine()) != null) 
            {
              builder.append(line);
              Log.v("Build value",line);
            }
            
//            if (resEntity != null) {
//                 
//                String responseStr = EntityUtils.toString(resEntity).trim();
//                Log.v("toyota", "Response: " +  responseStr);
//                responsefromserver=responseStr;
//                 
//                // you can add an if statement here and do other actions based on the response
//            }
             
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        try 
        {
        	 Log.v("Build value",builder.toString());
          JSONObject  jObj = new JSONObject(builder.toString());
            
//            for (int i = 0; i < jarray.length(); i++) 
//            {
//        JSONObject jsonElements = jarray.getJSONObject(i);

//        String id    = jObj.getString("id");
        JSONArray nameObj    = jObj.getJSONArray("TblService");
        
        String response = ((JSONObject) nameObj.get(0)).getString("200");
      	            Log.v("response== ",response);
      	            responseData = response;
        
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
          = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
}


}
