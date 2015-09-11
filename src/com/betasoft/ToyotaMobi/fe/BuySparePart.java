package com.betasoft.ToyotaMobi.fe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.StringRequest;
import com.betasoft.ToyotaMobi.R;
import com.betasoft.ToyotaMobi.SplashActivity;
import com.betasoft.ToyotaMobi.fe.fragment.BuyPart;
import com.betasoft.ToyotaMobi.fe.fragment.Config;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

public class BuySparePart extends Activity {
	private ProgressDialog pDialog; // declaring progress dialog

	private static final int REQUEST_CODE_PAYMENT = 1;
	// intializing paypal for this activity by configuring payment client id
	private static PayPalConfiguration paypalConfig = new PayPalConfiguration()
			.environment(Config.PAYPAL_ENVIRONMENT).clientId(
					Config.PAYPAL_CLIENT_ID);
	String name, price, deliveryAddress, transactionID, responseData;
	private int paymentOpt;

	byte needService = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// getting name,p
		name = getIntent().getExtras().getString("itemName");
		price = getIntent().getExtras().getString("itemPrice");
		needService = getIntent().getExtras().getByte("needService");
		deliveryAddress = getIntent().getExtras().getString("deliveryAddress");

		this.paymentOpt = getIntent().getExtras().getInt("paymentOpt");

		if (isNetworkAvailable()) // checking network available
		{
			
			if(paymentOpt==R.string.mobilemoney){
				Intent intent = new Intent(getApplicationContext(), MobileMoneyPayment.class);
				Bundle bun = new Bundle();
				bun.putString("itemName", name);
				bun.putString("itemPrice", price);
				bun.putString("deliveryAddress", deliveryAddress);
				bun.putByte("needService", needService);
				bun.putInt("paymentOpt", paymentOpt); 
				intent.putExtras(bun);
				
				startActivity(intent);
				Toast.makeText(getApplicationContext(),
						"Mobile Money Selected . . .", Toast.LENGTH_LONG).show();
			}
			else{
				// calling paypal service and setting configuration
				Intent intent = new Intent(getApplicationContext(), PayPalService.class);
				intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, paypalConfig);
				startService(intent);
				float totPrice = Float.parseFloat(price) / Float.parseFloat(BuyPart.shillingToUSD); // converting
																	// shilling to
																	// USD
				price = Float.toString(totPrice);
				PayPalPayment thingsToBuy = prepareFinalCart(); // preparing things
																// to buy

				// paypal service starting
				Intent intent1 = new Intent(getApplicationContext(), PaymentActivity.class);

				intent1.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, 	paypalConfig);

				intent1.putExtra(PaymentActivity.EXTRA_PAYMENT, thingsToBuy);
				// getParent().getWindow().getContainer().closeAllPanels();
				startActivityForResult(intent1, REQUEST_CODE_PAYMENT);
			}
			
		} else {
			Toast.makeText(getApplicationContext(),
					"Sorry!! You are not Connected to Internet!", Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * Preparing final cart amount that needs to be sent to PayPal for payment
	 * */
	private PayPalPayment prepareFinalCart() {
		// System.out.println(itemPrice);

		BigDecimal priceDec = new BigDecimal(price);
		// sending price in USD,items
		PayPalPayment payment = new PayPalPayment(priceDec, "USD", name,
				Config.PAYMENT_INTENT);

		// Custom field like invoice_number etc.,
		payment.custom("This is text that will be associated with the payment that the app can use.");

		return payment;
	}

	// calling on result of Paypal Service
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_PAYMENT) {
			// checking for payment confirmation
			if (resultCode == Activity.RESULT_OK) {
				PaymentConfirmation confirm = data
						.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
				if (confirm != null) {
					// if payment successful, transactionid, items, total amount
					// in Shilling send to server
					try {
						Log.i("c==", confirm.toJSONObject().toString(4));
						Log.i("c==", confirm.getPayment().toJSONObject()
								.toString(4));
						String paymentId = confirm.toJSONObject()
								.getJSONObject("response").getString("id");
						transactionID = paymentId;
						String payment_client = confirm.getPayment()
								.toJSONObject().toString();

						Log.e("c==", "paymentId: " + paymentId
								+ ", payment_json: " + payment_client);
						Toast.makeText(getApplicationContext(),
								"Your Payment made successfully!!!",
								Toast.LENGTH_LONG).show();
						final String params = "http://www.toyotamobi.com/toyotamobi/admin/Webservices/manage_order?";
						MyAsyncTask sendDataToServer = new MyAsyncTask();
						sendDataToServer.execute(params);

						// Now verify the payment on the server side
						finish();
					} catch (JSONException e) {
						Log.e("c==",
								"an extremely unlikely failure occurred: ", e);
					}
				}
			}

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
						Log.d("verify",
								"verify payment: " + response.toString());

						try {
							JSONObject res = new JSONObject(response);
							boolean error = res.getBoolean("error");
							String message = res.getString("message");

							// user error boolean flag to check for errors

							Toast.makeText(getApplicationContext(), message,
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
						Toast.makeText(getApplicationContext(),
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

		// Setting timeout to volley request as verification request takes
		// sometime
		int socketTimeout = 60000;
		RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
		verifyReq.setRetryPolicy(policy);

	}

	private class MyAsyncTask extends AsyncTask<String, Integer, Double> {

		@Override
		protected Double doInBackground(String... params) {

			postText();

			return null;
		}

		protected void onPostExecute(Double result) {
			// after sending detail to server, app goes to MainPage
			Intent in = new Intent(BuySparePart.this, NavigationMain.class);
			startActivity(in);
		}

		protected void onProgressUpdate(Integer... progress) {
			// pb.setProgress(progress[0]);
		}

		// this will post our text data
		private void postText() {
			StringBuilder builder = new StringBuilder();

			try {
				// url where the data will be posted
				String postReceiverUrl = "http://www.toyotamobi.com/toyotamobi/admin/Webservices/manage_order?";
				Log.v("toyota", "postURL: " + postReceiverUrl);
				Log.v("TransID== ", transactionID);
				// HttpClient
				HttpClient httpClient = new DefaultHttpClient();

				// post header
				HttpPost httpPost = new HttpPost(postReceiverUrl);

				// add your data
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						6);
				nameValuePairs.add(new BasicNameValuePair("client_id",
						SplashActivity.usersDetailBean.userId));
				nameValuePairs.add(new BasicNameValuePair("sparepart_items",
						name));
				nameValuePairs.add(new BasicNameValuePair("payment_opt",
						"PAYPAL"));
				String shillingPrice = Float
						.toString((Float.parseFloat(price) * Float
								.parseFloat(BuyPart.shillingToUSD)));
				nameValuePairs.add(new BasicNameValuePair("total_price",
						shillingPrice));
				nameValuePairs.add(new BasicNameValuePair("delivery_address",
						deliveryAddress));
				nameValuePairs.add(new BasicNameValuePair("transaction_id",
						transactionID));
				if (needService == 1)
					nameValuePairs.add(new BasicNameValuePair("service", "1"));
				else
					nameValuePairs.add(new BasicNameValuePair("service", "0"));

				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				// execute HTTP post request
				HttpResponse response = httpClient.execute(httpPost);
				HttpEntity resEntity = response.getEntity();
				// StatusLine line = response.getStatusLine();
				InputStream content = resEntity.getContent();

				BufferedReader reader = new BufferedReader(
						new InputStreamReader(content));

				String line;

				while ((line = reader.readLine()) != null) {
					builder.append(line);
					Log.v("Build value", line);
				}

			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				Log.v("Build value", builder.toString());
				JSONObject jObj = new JSONObject(builder.toString());
				JSONArray nameObj = jObj.getJSONArray("TblOrder");

				String response = ((JSONObject) nameObj.get(0))
						.getString("200");
				Log.v("response== ", response);
				responseData = response;

			}

			catch (JSONException e) {
				Log.e("JSON Parser", "Error parsing data " + e.toString());
			}
		}

	}

	public boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
}
