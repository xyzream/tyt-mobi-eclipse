package com.betasoft.ToyotaMobi.fe.fragment;

import com.betasoft.ToyotaMobi.R;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.content.Intent;

public class TermsAndConditions extends Fragment implements OnClickListener {

	private static final String TAG = "Register";

	private View mainView;

	private TextView txtInfo, tandc;
   // TextView tandc;

	public static CustomerRegistration instance;
	ImageButton nextbtn;

	public static CustomerRegistration getInstance() {
		if (instance == null) {
			instance = new CustomerRegistration();
		}
		return instance;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

	//
			
			mainView = inflater.inflate(R.layout.terms, null);
	//	   nextbtn=(ImageButton)mainView.findViewById(R.id.searchicon);
	//		nextbtn.setOnClickListener(this);

	
		return mainView;
	}

@Override
public void onActivityCreated(Bundle savedInstanceState) {
	super.onActivityCreated(savedInstanceState);
    tandc = (TextView)mainView.findViewById(R.id.tandc_tv);

    tandc.setOnClickListener(new View.OnClickListener() {

        @Override
        public void onClick(View arg0) {
            Intent intent = new Intent(getActivity(), TermsConds.class);
            startActivity(intent);


        }
    })  ;
}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		FragmentTransaction tx1 = getFragmentManager().beginTransaction();
        tx1.replace(R.id.content_frame,Fragment.instantiate(getActivity(),"com.betasoft.ToyotaMobi.fe.fragment.Search" ));
        tx1.commit();		
	}

	
	
	
}
