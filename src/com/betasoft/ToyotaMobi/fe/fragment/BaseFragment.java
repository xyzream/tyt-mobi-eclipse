package com.betasoft.ToyotaMobi.fe.fragment;

import android.support.v4.app.Fragment;

public abstract class BaseFragment extends Fragment {

	abstract public void saveSettings();

	abstract public boolean isOk();
	
	abstract public String getMessage();
}
