package com.betasoft.ToyotaMobi.fe.fragment;


import com.betasoft.ToyotaMobi.R;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class CustomerRegistration extends Fragment implements OnClickListener {

	private static final String TAG = "Register";

	private View mainView;

	private TextView txtInfo;

	public static CustomerRegistration instance;
	Button nextbtn;

	public static CustomerRegistration getInstance() {
		if (instance == null) {
			instance = new CustomerRegistration();
		}
		return instance;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	

			
			mainView = inflater.inflate(R.layout.customer_registeration, null);
			TextView tv=(TextView)getActivity().getActionBar().getCustomView().findViewById(R.id.actiontitle);
		      tv.setText("Registration");
		  /*    ImageButton img=(ImageButton)getActivity().getActionBar().getCustomView().findViewById(R.id.search);
		         img.setBackgroundResource(android.R.color.transparent);*/
			nextbtn=(Button)mainView.findViewById(R.id.nextbt);
			nextbtn.setOnClickListener(this);
	
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
        tx1.replace(R.id.content_frame,Fragment.instantiate(getActivity(),"com.betasoft.ToyotaMobi.fe.fragment.Phone_verification" ));
        TextView tv=(TextView)getActivity().getActionBar().getCustomView().findViewById(R.id.actiontitle);
	         tv.setText("Phone Verification");
	       /*ImageButton img=(ImageButton)getActivity().getActionBar().getCustomView().findViewById(R.id.search);
	       img.setBackgroundResource(R.drawable.search_icon);*/
        tx1.commit();		
	}

	
	
	
}
