package com.betasoft.ToyotaMobi.fe.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.betasoft.ToyotaMobi.R;

public class Phone_verification extends Fragment implements OnClickListener {

	private static final String TAG = "Register";

	private View mainView;

	private TextView txtInfo;

	public static Phone_verification instance;
	Button finish;

	public static Phone_verification getInstance() {
		if (instance == null) {
			instance = new Phone_verification();
		}
		return instance;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	

			
			mainView = inflater.inflate(R.layout.phone_verification, null);
			TextView tv=(TextView)getActivity().getActionBar().getCustomView().findViewById(R.id.actiontitle);
		      tv.setText("Phone Verification");
		/*      ImageButton img=(ImageButton)getActivity().getActionBar().getCustomView().findViewById(R.id.search);
		         img.setBackgroundResource(android.R.color.transparent);*/
			finish=(Button)mainView.findViewById(R.id.finish);
			finish.setOnClickListener(this);
	
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
		FragmentTransaction tx1 = getFragmentManager().beginTransaction();
      tx1.replace(R.id.content_frame,Fragment.instantiate(getActivity(),"com.betasoft.ToyotaMobi.fe.fragment.MainMenu" ));
      TextView tv=(TextView)getActivity().getActionBar().getCustomView().findViewById(R.id.actiontitle);
      tv.setText("Main Menu");
  /*  ImageButton img=(ImageButton)getActivity().getActionBar().getCustomView().findViewById(R.id.search);
    img.setBackgroundResource(R.drawable.search_icon);*/
      tx1.addToBackStack("main");
      tx1.commit();		
	}

	
	
	
}


