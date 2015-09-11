package com.betasoft.ToyotaMobi.fe.fragment;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
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

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.betasoft.ToyotaMobi.R;
import com.betasoft.ToyotaMobi.SplashActivity;
import com.joooonho.SelectableRoundedImageView;

public class MyProfile extends Fragment implements OnClickListener {

	private static final String TAG = "Register";

	private View mainView;
	SelectableRoundedImageView profileIV;
	private TextView txtInfo;
	TextView userNameTV;
	boolean btnStatus = false;
	String imagePath;
	String responseData;
	EditText contactET,emailET,addressET,chesisET;
	String imageName;
	public static MyProfile instance;
	Button nextbtn,backBtn,editBtn;
	
	// Raj Code Start
	Button saveBtn;
	// Raj Code End
	
	public static MyProfile getInstance() {
		if (instance == null) {
			instance = new MyProfile();
		}
		return instance;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {



		mainView = inflater.inflate(R.layout.my_profile, null);
		TextView tv=(TextView)getActivity().getActionBar().getCustomView().findViewById(R.id.actiontitle);
		tv.setText("My Profile");
		userNameTV = (TextView)mainView.findViewById(R.id.user_name);
		contactET = (EditText)mainView.findViewById(R.id.contactno);
		emailET = (EditText)mainView.findViewById(R.id.email);
		addressET = (EditText)mainView.findViewById(R.id.etaddress);
		chesisET = (EditText)mainView.findViewById(R.id.etchesisno);
		saveBtn = (Button)mainView.findViewById(R.id.save_btn);
		
		backBtn = (Button)mainView.findViewById(R.id.back_btn);
		backBtn.setText("Cancel");
		profileIV = (SelectableRoundedImageView)mainView.findViewById(R.id.profileimage);
		userNameTV.setText(SplashActivity.usersDetailBean.fullName);
		contactET.setText(SplashActivity.usersDetailBean.phoneNum);
		
		saveBtn.setText("Edit");
		
		//Raj Code Start
		// Making the EditTexts contactET,emailET,addressET,chesisET  clickable, focussable
	
		contactET.setEnabled(false);
		
	
		emailET.setEnabled(false);
	
		addressET.setEnabled(false);

		chesisET.setEnabled(false);
		//Raj Code End

		if(SplashActivity.usersDetailBean.address!=null)
			addressET.setText(SplashActivity.usersDetailBean.address);
		if(SplashActivity.usersDetailBean.emailAdress!=null)
			emailET.setText(SplashActivity.usersDetailBean.emailAdress);

		if(SplashActivity.usersDetailBean.chesisNum!=null)
			chesisET.setText(SplashActivity.usersDetailBean.chesisNum);
		String pPath = SplashActivity.usersDetailBean.profilePath; 
		if(pPath!=null)
		{
			
			int rotate =0;
			ExifInterface exif;
			try {
				exif = new ExifInterface(pPath);

				int orientation = exif.getAttributeInt(
						ExifInterface.TAG_ORIENTATION,
						ExifInterface.ORIENTATION_NORMAL);

				switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_270:
					rotate = 270;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					rotate = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_90:
					rotate = 90;
					break;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Bitmap bitmap;
			BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
			                 File file = new File(SplashActivity.usersDetailBean.profilePath);
			                 if(file.exists())
			                 {
			bitmap = BitmapFactory.decodeFile(pPath,
					bitmapOptions); 
			Matrix matrix = new Matrix();
			matrix.postRotate(rotate);
			int nh = (int) ( bitmap.getHeight() * (512.0 / bitmap.getWidth()) );
			Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
			Bitmap cropped = Bitmap.createBitmap(scaled,0,0, 512,nh, matrix, true);

			profileIV.setImageBitmap(cropped);
			                
			}

		}

		final String params ="http://www.toyotamobi.com/toyotamobi/admin/Webservices/client_profile?";
		/*ImageButton img=(ImageButton)getActivity().getActionBar().getCustomView().findViewById(R.id.search);
		img.setBackgroundResource(android.R.color.transparent);*/
		//nextbtn=(Button)mainView.findViewById(R.id.nextbt);
		//	nextbtn.setOnClickListener(this);
		profileIV.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				 if (btnStatus){
		                selectImage();
		                } else
		                {
		                    Toast.makeText(getActivity().getApplicationContext(),"Please click on Edit button to edit profile Pic",Toast.LENGTH_LONG).show();
		                }
			}
		});

		//Raj Code Start
		

		//Raj Code End

		saveBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(!btnStatus)
				{
					btnStatus = true;
					saveBtn.setText("Save");
					contactET.setEnabled(true);
					
					
					emailET.setEnabled(true);
				
					addressET.setEnabled(true);

					chesisET.setEnabled(true);
				}
				else
				{
					btnStatus = false;
					
					if((!contactET.getText().toString().isEmpty())||(!emailET.getText().toString().isEmpty())||(!addressET.getText().toString().isEmpty())||(!chesisET.getText().toString().isEmpty()))
					{

						String EMAIL_PATTERN =  "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
						        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
						Pattern pattern = Pattern.compile(EMAIL_PATTERN);
						Matcher matcher = pattern.matcher(emailET.getText().toString());
					     if(matcher.matches())
					     {
					    	 if(isNetworkAvailable())
					    	 {
						MyAsyncTask sendDataToServer = new MyAsyncTask();
						sendDataToServer.execute(params);
						Toast.makeText(getActivity().getApplicationContext(),"Profile Updated Successfully..",Toast.LENGTH_LONG).show();
						contactET.setEnabled(false);
						
						
						emailET.setEnabled(false);					
						addressET.setEnabled(false);
						chesisET.setEnabled(false);
						saveBtn.setText("Edit");
					     }
							else
							{
								Toast.makeText(getActivity().getApplicationContext(),"Sorry!! You are not Connected to Internet!",Toast.LENGTH_LONG).show();
							}
					     }
					     else
					     {
					    	 Toast.makeText(getActivity().getApplicationContext(),"Please enter valid EmailID",Toast.LENGTH_LONG).show();
					     }
					}
					else
					{
						Toast.makeText(getActivity().getApplicationContext(),"All Fields are Mandatory",Toast.LENGTH_LONG).show();

					}
					
				}
				

			}
		});
		
		
		backBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(btnStatus)
				{
					btnStatus = false;
					saveBtn.setText("Edit");
					contactET.setEnabled(false);
					
					
					emailET.setEnabled(false);
				
					addressET.setEnabled(false);

					chesisET.setEnabled(false);
				}
			}
		});

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

	private void selectImage() {

		final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Add Photo!");
		builder.setItems(options, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				if (options[item].equals("Take Photo"))
				{
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					//	                    Calendar cal = Calendar.getInstance();
					File f = new File(android.os.Environment.getExternalStorageDirectory(),"temp.jpg");
					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
					startActivityForResult(intent, 1);
				}
				else if (options[item].equals("Choose from Gallery"))
				{
					Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					startActivityForResult(intent, 2);

				}
				else if (options[item].equals("Cancel")) {
					dialog.dismiss();
				}
			}
		});
		builder.show();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == -1) {
			if (requestCode == 1) {
				File f = new File(android.os.Environment.getExternalStorageDirectory().toString());
				for (File temp : f.listFiles()) {
					if (temp.getName().equals("temp.jpg")) {
						f = temp;
						break;
					}
				}
				try {
					Bitmap bitmap;
					BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
					imagePath = f.getAbsolutePath().toString();
					int rotate = 0;
					ExifInterface exif = new ExifInterface(imagePath);
					int orientation = exif.getAttributeInt(
							ExifInterface.TAG_ORIENTATION,
							ExifInterface.ORIENTATION_NORMAL);

					switch (orientation) {
					case ExifInterface.ORIENTATION_ROTATE_270:
						rotate = 270;
						break;
					case ExifInterface.ORIENTATION_ROTATE_180:
						rotate = 180;
						break;
					case ExifInterface.ORIENTATION_ROTATE_90:
						rotate = 90;
						break;
					}



					bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),
							bitmapOptions); 
					Matrix matrix = new Matrix();
					matrix.postRotate(rotate);
					int nh = (int) ( bitmap.getHeight() * (512.0 / bitmap.getWidth()) );
					Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
					Bitmap cropped = Bitmap.createBitmap(scaled,0,0, 512,nh, matrix, true);

					profileIV.setImageBitmap(cropped);

					String path = android.os.Environment
							.getExternalStorageDirectory()
							+ File.separator
							+ "Phoenix" + File.separator + "default";
					f.delete();
					OutputStream outFile = null;
					imageName = String.valueOf(System.currentTimeMillis()) + ".jpg";
					File file = new File(path, imageName);
					try {
						outFile = new FileOutputStream(file);
						bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
						outFile.flush();
						outFile.close();

					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (requestCode == 2) {

				Uri selectedImage = data.getData();
				String[] filePath = { MediaStore.Images.Media.DATA };
				Cursor c = getActivity().getContentResolver().query(selectedImage,filePath, null, null, null);
				c.moveToFirst();
				int columnIndex = c.getColumnIndex(filePath[0]);
				String picturePath = c.getString(columnIndex);
				int rotate = 0;
				ExifInterface exif;
				try {
					exif = new ExifInterface(picturePath);

					int orientation = exif.getAttributeInt(
							ExifInterface.TAG_ORIENTATION,
							ExifInterface.ORIENTATION_NORMAL);

					switch (orientation) {
					case ExifInterface.ORIENTATION_ROTATE_270:
						rotate = 270;
						break;
					case ExifInterface.ORIENTATION_ROTATE_180:
						rotate = 180;
						break;
					case ExifInterface.ORIENTATION_ROTATE_90:
						rotate = 90;
						break;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String values[] = picturePath.split("/");
				imagePath = picturePath;
				for(int k=0; k<values.length; k++)
				{
					if(values[k].contains(".jpg"))
						imageName = values[k];
				}

				c.close();
				//Toast.makeText(getActivity().getApplicationContext(),imageName,100).show();

				Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
				Matrix matrix = new Matrix();
				matrix.postRotate(rotate);
				int nh = (int) ( thumbnail.getHeight() * (512.0 / thumbnail.getWidth()) );
				Bitmap scaled = Bitmap.createScaledBitmap(thumbnail, 512, nh, true);
				Bitmap cropped = Bitmap.createBitmap(scaled,0,0, 512,nh, matrix, true);

				profileIV.setImageBitmap(cropped);

				//	                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
				//	                Matrix matrix = new Matrix();
				//                    matrix.postRotate(orientation);
				//	                Log.w("path of image from gallery......******************.........", picturePath+"");
				//	                int nh = (int) ( thumbnail.getHeight() * (512.0 / thumbnail.getWidth()) );
				//					Bitmap scaled = Bitmap.createScaledBitmap(thumbnail, 512, nh, true);
				//	                profileIV.setImageBitmap(scaled);
			}
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
			//	        	if(responsefromserver.contains("1"))
			//	        	{
			//	        		Toast.makeText(getBaseContext(),
			//	                        "Registration Successfull!",
			//	        Toast.LENGTH_LONG).show();
			//	        	}

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
				String postReceiverUrl = "http://www.toyotamobi.com/toyotamobi/admin/Webservices/client_profile?";
				//Log.v("toyota", "postURL: " + postReceiverUrl);
				// HttpClient
				HttpClient httpClient = new DefaultHttpClient();

				// post header
				HttpPost httpPost = new HttpPost(postReceiverUrl);

				// add your data
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
				nameValuePairs.add(new BasicNameValuePair("first_name",userNameTV.getText().toString()));
				nameValuePairs.add(new BasicNameValuePair("phone", contactET.getText().toString()));
				nameValuePairs.add(new BasicNameValuePair("email", emailET.getText().toString()));
				nameValuePairs.add(new BasicNameValuePair("user_id",SplashActivity.usersDetailBean.userId));
				nameValuePairs.add(new BasicNameValuePair("chassis_no",chesisET.getText().toString()));
				nameValuePairs.add(new BasicNameValuePair("address",addressET.getText().toString()));
				nameValuePairs.add(new BasicNameValuePair("image",imageName));

				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				// execute HTTP post request
				HttpResponse response = httpClient.execute(httpPost);
				HttpEntity resEntity = response.getEntity();
				//	                StatusLine line = response.getStatusLine();
				InputStream content = resEntity.getContent();

				BufferedReader reader = new BufferedReader(new InputStreamReader(content));

				String line;

				while ((line = reader.readLine()) != null) 
				{
					builder.append(line);
					Log.v("Build value",line);
				}
				SplashActivity.usersDetailBean.address = addressET.getText().toString();
				SplashActivity.usersDetailBean.phoneNum = contactET.getText().toString();
				SplashActivity.usersDetailBean.chesisNum = chesisET.getText().toString();
				SplashActivity.usersDetailBean.emailAdress = emailET.getText().toString();
				SplashActivity.usersDetailBean.profilePath = imagePath;
				SplashActivity.session.createLoginSession(SplashActivity.usersDetailBean);

				//	                if (resEntity != null) {
				//	                     
				//	                    String responseStr = EntityUtils.toString(resEntity).trim();
				//	                    Log.v("toyota", "Response: " +  responseStr);
				//	                    responsefromserver=responseStr;
				//	                     
				//	                    // you can add an if statement here and do other actions based on the response
				//	                }

			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			try 
			{
				//Log.v("Build value",builder.toString());
				JSONObject  jObj = new JSONObject(builder.toString());

				//	                for (int i = 0; i < jarray.length(); i++) 
				//	                {
				//	            JSONObject jsonElements = jarray.getJSONObject(i);

				//	            String id    = jObj.getString("id");
				JSONArray nameObj    = jObj.getJSONArray("Member");

				String response = ((JSONObject) nameObj.get(0)).getString("200");
				Log.v("response== ",response);
				responseData = response;

			} 

			catch (JSONException e) 
			{
				//Log.e("JSON Parser", "Error parsing data " + e.toString());
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


