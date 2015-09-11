package com.betasoft.ToyotaMobi.fe.fragment;



import java.util.ArrayList;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.betasoft.ToyotaMobi.R;
import com.betasoft.ToyotaMobi.JavaBeans.SparePartsBean;

public class SpareParts extends Fragment implements OnClickListener {

	private static final String TAG = "Register";
	

	private View mainView;

	private TextView txtInfo;
	
	EditText partET ;
	String receiveEntity;
	ArrayList<SparePartsBean> sparePartsList;  //Raj


	public static SpareParts instance;
	ImageButton nextbtn;

	public static SpareParts getInstance() {
		if (instance == null) {
			instance = new SpareParts();
		}
		return instance;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mainView = inflater.inflate(R.layout.my_vehicle_screen, null);
		TextView tv=(TextView)getActivity().getActionBar().getCustomView().findViewById(R.id.actiontitle);
		tv.setText("Spare Parts");
		partET = (EditText)mainView.findViewById(R.id.part_name);
		nextbtn=(ImageButton)mainView.findViewById(R.id.searchicon);
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
				Fragment fm = new BuyPart();
		FragmentTransaction tx1 = getFragmentManager().beginTransaction();
		tx1.replace(R.id.content_frame,fm);
		TextView tv=(TextView)getActivity().getActionBar().getCustomView().findViewById(R.id.actiontitle);
		tv.setText(partET.getText().toString());
		tx1.addToBackStack(null);
	/*	ImageButton img=(ImageButton)getActivity().getActionBar().getCustomView().findViewById(R.id.search);
		img.setBackgroundResource(R.drawable.search_icon);*/
		tx1.commit();		
	}

}
