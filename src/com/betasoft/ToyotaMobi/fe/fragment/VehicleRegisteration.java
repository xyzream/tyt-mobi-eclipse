package com.betasoft.ToyotaMobi.fe.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.betasoft.ToyotaMobi.R;

public class VehicleRegisteration extends Fragment implements OnClickListener {

	private static final String TAG = "Register";

	private View mainView;

	private TextView txtInfo;

	public static VehicleRegisteration instance;
	Button nextbtn;

	public static VehicleRegisteration getInstance() {
		if (instance == null) {
			instance = new VehicleRegisteration();
		}
		return instance;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	

			
			mainView = inflater.inflate(R.layout.vehicle_registeration, null);
			
			
	
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
		/*FragmentTransaction tx1 = getFragmentManager().beginTransaction();
        tx1.replace(R.id.content_frame,Fragment.instantiate(getActivity(),"com.betasoft.ToyotaMobi.fe.fragment.MainMenu" ));
        tx1.commit();	*/	
	}

	
	
	
}
