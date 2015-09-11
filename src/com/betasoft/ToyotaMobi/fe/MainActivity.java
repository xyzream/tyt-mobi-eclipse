package com.betasoft.ToyotaMobi.fe;

import java.util.ArrayList;

import com.betasoft.ToyotaMobi.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {
	private ViewPager viewPager;
	private MyViewPagerAdapter myViewPagerAdapter;
	private ArrayList<String> listOfItems;
	
	private LinearLayout dotsLayout;
	private int dotsCount;
	private TextView[] dots;
	String page1,page2,page3,page4;
	Button b,skip;
	Intent i;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_guide);
		getActionBar().hide();
		initViews();
		setViewPagerItemsWithAdapter();
		setUiPageViewController();
	}
@Override
public void onConfigurationChanged(Configuration newConfig) {
	// TODO Auto-generated method stub
	super.onConfigurationChanged(newConfig);
	initViews();
	setViewPagerItemsWithAdapter();
	setUiPageViewController();
	
}
	private void setUiPageViewController() {
		
		dotsCount = myViewPagerAdapter.getCount();
		dots = new TextView[dotsCount];
		
		for (int i = 0; i < dotsCount; i++) {
			dots[i] = new TextView(this);
			dots[i].setText(Html.fromHtml("&#8226;"));
			dots[i].setTextSize(30);
			dots[i].setTextColor(getResources().getColor(android.R.color.darker_gray));
			dotsLayout.addView(dots[i]);			
		}
		
		dots[0].setTextColor(getResources().getColor(android.R.color.white));
	}

	private void setViewPagerItemsWithAdapter() {
		myViewPagerAdapter = new MyViewPagerAdapter(listOfItems);
		viewPager.setAdapter(myViewPagerAdapter);
		viewPager.setCurrentItem(0);
		viewPager.setOnPageChangeListener(viewPagerPageChangeListener);
	}
	
	//	page change listener
	OnPageChangeListener viewPagerPageChangeListener = new OnPageChangeListener() {
		
		@Override
		public void onPageSelected(int position) {
			for (int i = 0; i < dotsCount; i++) {
				dots[i].setTextColor(getResources().getColor(android.R.color.darker_gray));
			}
			dots[position].setTextColor(getResources().getColor(android.R.color.white));
			/*if(position>0)
			{
				 b=(Button)findViewById(R.id.appguide);
				b.setVisibility(View.GONE);
			}
			else
			{
				b.setVisibility(View.VISIBLE);
			}*/
			if(position==3)
			{
				skip=(Button)findViewById(R.id.skip);
				 skip.setText("Finish");
			}
			else
			{
				skip=(Button)findViewById(R.id.skip);
				skip.setText("Skip");
			}
		}
		
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			
		}
		
		@Override
		public void onPageScrollStateChanged(int arg0) {
			
		}
	};

	private void initViews() {
		dotsLayout = (LinearLayout)findViewById(R.id.viewPagerCountDots);
		skip=(Button)findViewById(R.id.skip);
		skip.setOnClickListener(this);
		
		page1="Lorem Ipsum is simply dummy text of the printing and typesetting industry.Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.";
		page2="Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of the word in classical literature, discovered the undoubtable source. Lorem Ipsum comes from sections 1.10.32 and 1.10.33 of de Finibus Bonorum et Malorum (The Extremes of Good and Evil) by Cicero, written in 45 BC. This book is a treatise on the theory of ethics, very popular during the Renaissance. The first line of Lorem Ipsum, Lorem ipsum dolor sit amet.., comes from a line in section 1.10.32.0";
		page3="It is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout. The point of using Lorem Ipsum is that it has a more-or-less normal distribution of letters, as opposed to using 'Content here, content here', making it look like readable English. Many desktop publishing packages and web page editors now use Lorem Ipsum as their default model text, and a search for 'lorem ipsum' will uncover many web sites still in their infancy. ";
		page4="There are many variations of passages of Lorem Ipsum available, but the majority have suffered alteration in some form, by injected humour, or randomised words which don't look even slightly believable. If you are going to use a passage of Lorem Ipsum, you need to be sure there isn't anything embarrassing hidden in the middle of text. All the Lorem Ipsum generators on the Internet tend to repeat predefined chunks as necessary, making this the first true generator on the Internet.";
		viewPager = (ViewPager)findViewById(R.id.viewPager);
		
		listOfItems = new ArrayList<String>();
		listOfItems.add(page1);
		listOfItems.add(page2);
		listOfItems.add(page3);
	/*	listOfItems.add(page4);
		*/
	}
	
	//	adapter
	public class MyViewPagerAdapter extends PagerAdapter{
		
		private LayoutInflater layoutInflater;
		private ArrayList<String> items;

		public MyViewPagerAdapter(ArrayList<String> listOfItems) {
			this.items = listOfItems;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			
			layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = layoutInflater.inflate(R.layout.view_pager_item, container,false);
			
			ImageView tView = (ImageView)view.findViewById(R.id.PageNumber);
			
			if(position ==0)
				tView.setBackgroundResource(R.drawable.app_guide_1a);
			else if(position == 1)
				tView.setBackgroundResource(R.drawable.app_guide_2);
			else if(position == 2)
				tView.setBackgroundResource(R.drawable.app_guide_32);
			/*else if(position == 3)
				tView.setBackgroundResource(R.drawable.app_guide_4);
			
			*/
			((ViewPager) container).addView(view);
			
			return view;
		}

		@Override
		public int getCount() {
			return items.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object obj) {
			return view == ((View)obj);
		}
		
				
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			View view = (View)object;
	        ((ViewPager) container).removeView(view);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

			if(v.getId()==R.id.skip)
				// TODO Auto-generated method stub
			 i=new Intent(MainActivity.this,CustomerRegistration.class);
				startActivity(i);
				finish();
			}
	
    }
