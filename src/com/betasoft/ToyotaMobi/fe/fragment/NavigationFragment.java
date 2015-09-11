package com.betasoft.ToyotaMobi.fe.fragment;

import java.util.ArrayList;

import com.betasoft.ToyotaMobi.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class NavigationFragment extends Fragment{
ListView drawerListView;
ArrayAdapter<String> adapter;
ArrayList<String> itemsList;
	@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
View rootView = inflater.inflate(R.layout.activity_common_list,container);
	drawerListView = (ListView)rootView.findViewById(R.id.common_list);
	itemsList = new ArrayList<String>();
	itemsList.add("Home");
	itemsList.add("My Profile");
	itemsList.add("My Orders");
	itemsList.add("Terms & Conditions");
	adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,itemsList);
	drawerListView.setAdapter(adapter);
return rootView;
}
}
