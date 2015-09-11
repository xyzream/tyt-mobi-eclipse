package com.betasoft.ToyotaMobi.fe;

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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.Toast;

import com.betasoft.ToyotaMobi.R;
import com.betasoft.ToyotaMobi.SplashActivity;
import com.betasoft.ToyotaMobi.JavaBeans.UserInfoBean;
import com.betasoft.ToyotaMobi.fe.adapter.UsersListAdapter;

public class UsersListActivity extends Activity {
	ListView userListView;
	UsersListAdapter adapter;
	String userID;
	String receiveEntity;
	WebView wv;
	ArrayList<UserInfoBean> usersList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_list);
		getActionBar().hide();
		if (isNetworkAvailable()) {
			wv = (WebView) findViewById(R.id.web);
			wv.loadUrl("file:///android_asset/loader.gif");
			usersList = new ArrayList<UserInfoBean>();
			userListView = (ListView) findViewById(R.id.user_list);
			adapter = new UsersListAdapter(UsersListActivity.this,
					android.R.layout.simple_list_item_1, usersList);
			userListView.setAdapter(adapter);
			MyServiceReceiveData myserrecdata = new MyServiceReceiveData();
			myserrecdata.execute();
			registerForContextMenu(userListView);
			userListView
					.setOnItemClickListener(new AdapterView.OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							Intent intent = new Intent(UsersListActivity.this,
									ChatMain.class);
							Bundle b = new Bundle();
							b.putString("userName",
									usersList.get(arg2).fullName);
							b.putString("senderID", SplashActivity.mechanicId);
							b.putString("receiverID",
									usersList.get(arg2).userId.toString());
							intent.putExtras(b);
							startActivity(intent);
						}

					});

		} else {
			Toast.makeText(getApplicationContext(),
					"Sorry!! You are not Connected to Internet!", 100).show();
		}
	}

	private class MyServiceReceiveData extends
			AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			wv.setVisibility(WebView.VISIBLE);
		};

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String receiveDataUrl = "http://www.toyotamobi.com/toyotamobi/admin/Webservices/sender_side_client";
			Log.v("link== ", receiveDataUrl);
			HttpPost httppost = new HttpPost(receiveDataUrl);
			DefaultHttpClient dhtpc = new DefaultHttpClient();
			try {
				HttpResponse httpresponse = dhtpc.execute(httppost);
				HttpEntity httpentity = httpresponse.getEntity();
				if (httpentity != null) {
					receiveEntity = EntityUtils.toString(httpentity);
					JSONObject jObj = new JSONObject(receiveEntity);
					JSONArray jsonArray = jObj.getJSONArray("TblChat");
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject ob = jsonArray.getJSONObject(i)
								.getJSONObject("Member");
						if (ob.getString("first_name") != null) {
							Log.v("id== ", ob.getString("first_name"));
							usersList.add(new UserInfoBean(ob
									.getString("first_name"), ob
									.getString("phone"), 3, jsonArray
									.getJSONObject(i).getString("sender_id")));
							//
						}
					}

					// JSONArray main_arr=new JSONArray(receiveEntity);
					// for(int i=0;i<main_arr.length();i++){
					// JSONObject main_obj=main_arr.optJSONObject(i);
					// if(main_obj!=null)
					// {
					// JSONArray tblChatArray=main_obj.optJSONArray("TblChat");
					// for(int ar=0; ar<tblChatArray.length(); ar++)
					// {
					// if(tblChatArray.getJSONObject(ar)!=null)
					// {
					// // usersList.add(new UserInfoBean(
					// tblChatArray.getJSONObject(ar).getString("sender_id"),"1",1,"1"));
					// Log.v("Message== ",
					// tblChatArray.getJSONObject(ar).getString("sender_id")+"\n\n\n");
					// }
					// }
					// }
					// }

				}
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(String receiveDataUrl) {
			// TODO Auto-generated method stub
			wv.setVisibility(WebView.GONE);
			adapter.notifyDataSetChanged();
			userListView.invalidate();
		}

	}

	private class DeleteChatConversation extends
			AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			wv.setVisibility(WebView.VISIBLE);
		};

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String receiveDataUrl = "http://www.toyotamobi.com/toyotamobi/admin/Webservices/delete_chat_history?client_id="
					+ userID;
			HttpPost httppost = new HttpPost(receiveDataUrl);
			DefaultHttpClient dhtpc = new DefaultHttpClient();
			try {
				HttpResponse httpresponse = dhtpc.execute(httppost);
				HttpEntity httpentity = httpresponse.getEntity();
				if (httpentity != null) {
					receiveEntity = EntityUtils.toString(httpentity);
					JSONObject jObj = new JSONObject(receiveEntity);
					JSONArray jsonArray = jObj.getJSONArray("TblChat");
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject ob = jsonArray.getJSONObject(i);
						if (ob.getString("200") != null) {
							Log.v("result==", ob.getString("200"));
							// faqList.add(new
							// FAQsBean(ob.getString("faq_question"),ob.getString("faq_answer")));
							//
						}
					}
					// JSONArray main_arr=new JSONArray(receiveEntity);
					// for(int i=0;i<main_arr.length();i++){
					// JSONObject main_obj=main_arr.optJSONObject(i);
					// if(main_obj!=null)
					// {
					// JSONArray tblChatArray=main_obj.optJSONArray("TblChat");
					// for(int ar=0; ar<tblChatArray.length(); ar++)
					// {
					// if(tblChatArray.getJSONObject(ar)!=null)
					// {
					// // usersList.add(new UserInfoBean(
					// tblChatArray.getJSONObject(ar).getString("sender_id"),"1",1,"1"));
					// Log.v("Message== ",
					// tblChatArray.getJSONObject(ar).getString("sender_id")+"\n\n\n");
					// }
					// }
					// }
					// }

				}
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(String receiveDataUrl) {
			// TODO Auto-generated method stub
			wv.setVisibility(WebView.GONE);
			adapter.notifyDataSetChanged();
			userListView.invalidate();
		}

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("Are you Sure to Delete Chat Conversation?");
		menu.add(0, v.getId(), 0, "Yes");// groupId, itemId, order, title
		menu.add(0, v.getId(), 0, "No");
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getTitle() == "Yes") {
			AdapterContextMenuInfo menuinfo = (AdapterContextMenuInfo) item
					.getMenuInfo();
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
					.getMenuInfo();
			int pos = info.position;// what item was selected is ListView
			userID = usersList.get(pos).userId.toString();
			DeleteChatConversation myserrecdata = new DeleteChatConversation();
			myserrecdata.execute();
			usersList.remove(pos);
			adapter.notifyDataSetChanged();
			userListView.invalidate();
			// Toast.makeText(getApplicationContext(),"calling code",Toast.LENGTH_LONG).show();
		} else {
			return false;
		}
		return true;
	}

	public boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
}
