package com.betasoft.ToyotaMobi.fe.fragment;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.betasoft.ToyotaMobi.R;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

public class SparePartFullDetail extends Fragment{
	public static ServiceFragment instance;
	View mainView;
	ImageView itemImage;
	Button buyBtn, backBtn;
	String itemName,itemNum,itemPrice,imagePath;
	TextView itemNameTV,itemPartNumTV,itemPriceTV;
	public static ServiceFragment getInstance() {
		if (instance == null) {
			instance = new ServiceFragment();
		}
		return instance;
	}
	private ProgressDialog pDialog;

	private static final int REQUEST_CODE_PAYMENT = 1;
	private static PayPalConfiguration paypalConfig = new PayPalConfiguration()
	.environment(Config.PAYPAL_ENVIRONMENT).clientId(
			Config.PAYPAL_CLIENT_ID);


@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	mainView = inflater.inflate(R.layout.spare_part_detail, null);
	itemName = getArguments().getString("itemName");
	itemNum = getArguments().getString("itemNum");
	itemPrice = getArguments().getString("itemPrice");
	imagePath = getArguments().getString("imagePath");
	itemNameTV = (TextView)mainView.findViewById(R.id.item_name_txt);
	itemPartNumTV = (TextView)mainView.findViewById(R.id.item_no_data);
	itemPriceTV = (TextView)mainView.findViewById(R.id.item_price_data);
	itemImage = (ImageView)mainView.findViewById(R.id.item_img);
	buyBtn = (Button)mainView.findViewById(R.id.buy_btn);
	backBtn = (Button)mainView.findViewById(R.id.back_btn);
	itemNameTV.setText(itemName);
	itemPartNumTV.setText(itemNum);
	itemPriceTV.setText(itemPrice);
	Intent intent = new Intent(getActivity().getApplicationContext(), PayPalService.class);
	intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, paypalConfig);
	getActivity().getApplicationContext().startService(intent);
	
	StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	 Calendar cal = Calendar.getInstance();
	StrictMode.setThreadPolicy(policy);
//	Toast.makeText(getActivity().getApplicationContext(),imagePath,100).show();
	if(isNetworkAvailable())
	{
	String savePath = "/storage/emulated/0/DCIM/Camera/"+cal.getTime().toString()+".jpg";
	try {
		Bitmap map = DownloadImage(new URL(imagePath),savePath);
	} catch (MalformedURLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}
else
{
	Toast.makeText(getActivity().getApplicationContext(),"Sorry!! You are not Connected to Internet!",100).show();
}
	backBtn.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			FragmentTransaction tx2 = getFragmentManager().beginTransaction();
			 tx2.replace(R.id.content_frame,Fragment.instantiate(getActivity(),"com.betasoft.ToyotaMobi.fe.fragment.SpareParts" ));
			 TextView tv1=(TextView)getActivity().getActionBar().getCustomView().findViewById(R.id.actiontitle);
		     tv1.setText("Vehicle Parts");
	/*	   ImageButton img1=(ImageButton)getActivity().getActionBar().getCustomView().findViewById(R.id.search);
		   img1.setBackgroundResource(R.drawable.search_icon);*/
			 
			 tx2.addToBackStack("vehicle");
			 tx2.commit();
		}
	});
	buyBtn.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			FragmentTransaction tx1 =getActivity().getFragmentManager().beginTransaction();
	           Fragment confirmFragment = new Search();
	        	Bundle bun = new Bundle();
	        	bun.putString("itemName",itemName);
	        	bun.putString("itemPrice",itemPrice);
	        	bun.putString("itemNumber",itemNum);
	        	confirmFragment.setArguments(bun);
	        	tx1.replace(R.id.content_frame,confirmFragment);
	            TextView tv=(TextView)getActivity().getActionBar().getCustomView().findViewById(R.id.actiontitle);
	            tv.setText("Confirm Part");
	            tx1.addToBackStack(null);
	         /* ImageButton img=(ImageButton)getActivity().getActionBar().getCustomView().findViewById(R.id.search);
	          img.setBackgroundResource(R.drawable.search_icon);*/
	            
	            
	            tx1.commit();	
			
			
			
//			PayPalPayment thingsToBuy = prepareFinalCart();
//
//			Intent intent = new Intent(getActivity().getApplicationContext(), PaymentActivity.class);
//
//			intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, paypalConfig);
//
//			intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingsToBuy);
//
//			startActivityForResult(intent, REQUEST_CODE_PAYMENT);
		}
	});
	return mainView;
}

private Bitmap DownloadImage(URL url, String fileSavePath)
{
	
	Bitmap bmImg = null;
    try {
    HttpURLConnection conn= (HttpURLConnection)url.openConnection();
    conn.setDoInput(true);
    conn.connect();
    InputStream is = conn.getInputStream();
    BufferedInputStream bis = new BufferedInputStream(is);
    /*
     * Read bytes to the Buffer until there is nothing more to read(-1).
     */
    ByteArrayBuffer baf = new ByteArrayBuffer(50);
    int current = 0;
    while ((current = bis.read()) != -1) {
            baf.append((byte) current);
    }

    FileOutputStream fos = new FileOutputStream(fileSavePath);
    fos.write(baf.toByteArray());
    fos.close();
    bmImg = BitmapFactory.decodeStream(is);
//    int nh = (int) ( bmImg.getHeight() * (512.0 / bmImg.getWidth()) );
//	 bmImg = Bitmap.createScaledBitmap(bmImg, 512, nh, true);
	 File imgFile = new  File(fileSavePath);
		if(imgFile.exists())
		{
		 Bitmap d = new BitmapDrawable(getActivity().getApplicationContext().getResources() , imgFile.getAbsolutePath()).getBitmap();
		 int nh = (int) ( d.getHeight() * (512.0 / d.getWidth()) );
		 Bitmap scaled = Bitmap.createScaledBitmap(d, 512, nh, true);
		 itemImage.setImageBitmap(scaled);
		 itemImage.invalidate();
		}

    } catch (IOException e) {
    // TODO Auto-generated catch block
    e.printStackTrace();
    return null;
    }

    return bmImg;
}

/**
 * Preparing final cart amount that needs to be sent to PayPal for payment
 * */
private PayPalPayment prepareFinalCart() {
System.out.println(itemPrice);

BigDecimal priceDec = new BigDecimal(itemPrice);

	PayPalPayment payment = new PayPalPayment(priceDec,"USD",itemName,Config.PAYMENT_INTENT);
			


	// Custom field like invoice_number etc.,
	payment.custom("This is text that will be associated with the payment that the app can use.");

	return payment;
}

@Override
public void onActivityResult(int requestCode, int resultCode, Intent data) {
	if (requestCode == REQUEST_CODE_PAYMENT) {
		if (resultCode == Activity.RESULT_OK) {
			PaymentConfirmation confirm = data
					.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
			
		}
		else if(resultCode == Activity.RESULT_CANCELED)
		{
			
			
		}
		else if(resultCode == PaymentActivity.RESULT_EXTRAS_INVALID)
		{
			
			
		}
		
//			if (confirm != null) {
//				try {
//					Log.e(TAG, confirm.toJSONObject().toString(4));
//					Log.e(TAG, confirm.getPayment().toJSONObject()
//							.toString(4));
//
//					String paymentId = confirm.toJSONObject()
//							.getJSONObject("response").getString("id");
//
//					String payment_client = confirm.getPayment()
//							.toJSONObject().toString();
//
//					Log.e(TAG, "paymentId: " + paymentId
//							+ ", payment_json: " + payment_client);
//
//					// Now verify the payment on the server side
//					verifyPaymentOnServer(paymentId, payment_client);
//
//				} catch (JSONException e) {
//					Log.e(TAG, "an extremely unlikely failure occurred: ",
//							e);
//				}
//			}
//		} 
		else if (resultCode == Activity.RESULT_CANCELED) {
			Log.e("verify", "The user canceled.");
		} else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
			Log.e("verify",
					"An invalid Payment or PayPalConfiguration was submitted.");
		}
	}
}

private void showpDialog() {
	if (!pDialog.isShowing())
		pDialog.show();
}

private void hidepDialog() {
	if (pDialog.isShowing())
		pDialog.dismiss();
}
/**
 * Verifying the mobile payment on the server to avoid fraudulent payment
 * */
private void verifyPaymentOnServer(final String paymentId,
		final String payment_client) {
	// Showing progress dialog before making request
	pDialog.setMessage("Verifying payment...");
	showpDialog();

	StringRequest verifyReq = new StringRequest(Method.POST,
			Config.URL_VERIFY_PAYMENT, new Response.Listener<String>() {

				@Override
				public void onResponse(String response) {
					Log.d("verify", "verify payment: " + response.toString());

					try {
						JSONObject res = new JSONObject(response);
						boolean error = res.getBoolean("error");
						String message = res.getString("message");

						// user error boolean flag to check for errors

						Toast.makeText(getActivity().getApplicationContext(), message,
								Toast.LENGTH_SHORT).show();

						
					} catch (JSONException e) {
						e.printStackTrace();
					}

					// hiding the progress dialog
					hidepDialog();

				}
			}, new Response.ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError error) {
					Log.e("verify", "Verify Error: " + error.getMessage());
					Toast.makeText(getActivity().getApplicationContext(),
							error.getMessage(), Toast.LENGTH_SHORT).show();
					// hiding the progress dialog
					hidepDialog();
				}
			}) {

		@Override
		protected Map<String, String> getParams() {

			Map<String, String> params = new HashMap<String, String>();
			params.put("paymentId", paymentId);
			params.put("paymentClientJson", payment_client);

			return params;
		}
	};

	// Setting timeout to volley request as verification request takes sometime
	int socketTimeout = 60000;
	RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
			DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
			DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
	verifyReq.setRetryPolicy(policy);

	
}

public  boolean isNetworkAvailable() {
    ConnectivityManager connectivityManager 
          = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
}

}
