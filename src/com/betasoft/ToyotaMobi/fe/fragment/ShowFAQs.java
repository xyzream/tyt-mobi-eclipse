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
import com.betasoft.ToyotaMobi.JavaBeans.FAQsBean;
import com.betasoft.ToyotaMobi.fe.adapter.FAQsAdapter;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ListView;
import android.widget.TextView;

public class ShowFAQs extends Fragment{
	public static FAQ instance;
	private View mainView;
	ArrayList<FAQsBean> faqList;
	String receiveEntity;
	String category;
	WebView wv;
	ListView faqListView;
	FAQsAdapter adapter;
	public static FAQ getInstance() {
		if (instance == null) {
			instance = new FAQ();
		}
		return instance;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mainView = inflater.inflate(R.layout.activity_display_faq, null);
		faqList = new ArrayList<FAQsBean>();
		wv=(WebView)mainView.findViewById(R.id.web);
	     wv.loadUrl("file:///android_asset/loader.gif");
//	     getActivity().getActionBar().hide();
		TextView tv=(TextView)getActivity().getActionBar().getCustomView().findViewById(R.id.actiontitle);
//	      tv.setText("FAQ List");
	      category = getArguments().getString("Category");
	      
	      if(category.matches("1"))
	    	  tv.setText("Engine Problem");
	      else if(category.matches("2"))
	    	  tv.setText("Maintenance Problem");
	      else
	    	  tv.setText("Vehicle Maintenance Records");
//	      Toast.makeText(getActivity(),category,100).show();
	      faqListView = (ListView)mainView.findViewById(R.id.faq_list);
	      adapter = new FAQsAdapter(getActivity(),android.R.layout.simple_list_item_1,faqList);
	      faqListView.setAdapter(adapter);
	      MyServiceReceiveData myserrecdata=new  MyServiceReceiveData();
	  	myserrecdata.execute();
		return mainView;
	}
	private class  MyServiceReceiveData extends AsyncTask<String, String, String>{

		@Override
		protected void onPreExecute() {
			wv.setVisibility(WebView.VISIBLE);
		};
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String receiveDataUrl="http://www.toyotamobi.com/toyotamobi/admin/Webservices/FAQ?faq_category="+category;
			//Log.v("link== ", receiveDataUrl);
			HttpPost httppost=new HttpPost(receiveDataUrl);
			DefaultHttpClient dhtpc=new DefaultHttpClient();
			try {
				HttpResponse httpresponse=dhtpc.execute(httppost);
				HttpEntity httpentity=httpresponse.getEntity();
				if (httpentity!=null) {
					receiveEntity=EntityUtils.toString(httpentity);
					JSONObject jObj = new JSONObject(receiveEntity);
					JSONArray jsonArray = jObj.getJSONArray("TblFaq");
					for(int i=0; i<jsonArray.length(); i++)
					{
						JSONObject ob = jsonArray.getJSONObject(i);
						if(ob.getString("faq_question")!=null)
						{
//							Log.v("id== ",ob.getString("first_name"));
							 faqList.add(new FAQsBean(ob.getString("faq_question"),ob.getString("faq_answer")));
//									
						}
					}
//					JSONArray main_arr=new JSONArray(receiveEntity);
//					for(int i=0;i<main_arr.length();i++){
//						JSONObject main_obj=main_arr.optJSONObject(i);
//						if(main_obj!=null)
//						{
//						JSONArray tblChatArray=main_obj.optJSONArray("TblChat");
//						for(int ar=0; ar<tblChatArray.length(); ar++)
//						{
//						if(tblChatArray.getJSONObject(ar)!=null)
//						{
////						  usersList.add(new UserInfoBean(	tblChatArray.getJSONObject(ar).getString("sender_id"),"1",1,"1"));
//						  Log.v("Message== ", 	tblChatArray.getJSONObject(ar).getString("sender_id")+"\n\n\n");
//						}
//						}
//						}
//					}
					
					
				}
			} 
			catch (ClientProtocolException e) {
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
			faqListView.invalidate();
			 		}
		
	}
	
}
