package com.example.jamood;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.content.Context;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener {
	
	static final String CLIENT_ID = "80c1f631";
	static String currentTAG = "sad";
	static final Integer LIMIT = 10;
	static final String TRACKS_URL = "http://api.jamendo.com/v3.0/tracks/?format=json&speed=medium+high+veryhigh&groupby=artist_id";
	static final String[] TAGS_TABS = {"happy", "sad", "funky"};
		    private MediaPlayer mediaPlayer;
		    private int playbackPosition=0;
		    
	static String currentSong = null;
	private Integer songIndex = 0;
	private Integer currentTab = 0;
	
	JSONArray results;
	static String[] playList = {};
	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	private String tracksURL(){
		String ret = TRACKS_URL+"&client_id="+CLIENT_ID + "&tags="+ currentTAG + "&limit="+LIMIT;
		return ret;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
		
//		reloadTag();
	}
	
	private void reloadTag() {
		DownloadTask task = new DownloadTask(this);
		task.execute(new String[] { tracksURL() });
	}
	
	public void onJSONReady( JSONArray r ){ //JSONArray results
		results = r;
		
		playCurrentSong();
	}

	public void playCurrentSong() {
		JSONObject oneObject = null;
		String audioURL = null, audioTitle = null;
		
		try {
			oneObject = results.getJSONObject(songIndex);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			audioURL = oneObject.getString("audio");
			audioTitle = oneObject.getString("name");
			currentSong = audioTitle;
			ActionBar ab = getActionBar();
			ab.setTitle(audioTitle);

			showToast(currentSong);

		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			playAudio(audioURL);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}


	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			
			
			ListFragment fragment = null;
//			
//			if (position == 0) {
				fragment = new TitlesFragment();
//			}
//			else {
//				DummySectionFragment fragment = new DummySectionFragment();
				Bundle args = new Bundle();
				args.putInt( DummySectionFragment.ARG_SECTION_NUMBER, position + 1 );
				
				fragment.setArguments(args);
//			}
			showToast("FragmentPagerAdapter::(Fragment)getItem");
			
			return fragment;
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return TAGS_TABS[0].toUpperCase(l);//getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return TAGS_TABS[1].toUpperCase(l);//getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return TAGS_TABS[2].toUpperCase(l);//getString(R.string.title_section3).toUpperCase(l);
			}
			return null;
		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main_dummy,
					container, false);
			TextView dummyTextView = (TextView) rootView
					.findViewById(R.id.section_label);
			dummyTextView.setText( "Refreshing..." );
			//currentSong != null ? currentSong :  Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER))
			
			
			return rootView;
		}
	}

	public static class TitlesFragment extends ListFragment {
	    boolean mDualPane;
	    int mCurCheckPosition = 0;

	    @Override
	    public void onActivityCreated(Bundle savedInstanceState) {
	        super.onActivityCreated(savedInstanceState);

	        // Populate list with our static array of titles.
	        setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_activated_1, playList));

//	        // Check to see if we have a frame in which to embed the details
//	        // fragment directly in the containing UI.
//	        View detailsFrame = getActivity().findViewById(R.id.details);
//	        mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;
//
//	        if (savedInstanceState != null) {
//	            // Restore last state for checked position.
//	            mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
//	        }
//
//	        if (mDualPane) {
//	            // In dual-pane mode, the list view highlights the selected item.
//	            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
//	            // Make sure our UI is in the correct state.
//	            showDetails(mCurCheckPosition);
//	        }
	    }

	    @Override
	    public void onSaveInstanceState(Bundle outState) {
	        super.onSaveInstanceState(outState);
	        outState.putInt("curChoice", mCurCheckPosition);
	    }

	    @Override
	    public void onListItemClick(ListView l, View v, int position, long id) {
	        showDetails(position);
	    }

	    /**
	     * Helper function to show the details of a selected item, either by
	     * displaying a fragment in-place in the current UI, or starting a
	     * whole new activity in which it is displayed.
	     */
	    
	    void showDetails(int index) {
	    	MainActivity ma = (MainActivity) getActivity();
	    	ma.songIndex = index;
	    	ma.playCurrentSong();
	    /*
	    	mCurCheckPosition = index;

	        if (mDualPane) {
	            // We can display everything in-place with fragments, so update
	            // the list to highlight the selected item and show the data.
	            getListView().setItemChecked(index, true);

	            // Check what fragment is currently shown, replace if needed.
	            DetailsFragment details = (DetailsFragment)
	                    getFragmentManager().findFragmentById(R.id.details);
	            if (details == null || details.getShownIndex() != index) {
	                // Make new fragment to show this selection.
	                details = DetailsFragment.newInstance(index);

	                // Execute a transaction, replacing any existing fragment
	                // with this one inside the frame.
	                FragmentTransaction ft = getFragmentManager().beginTransaction();
	                if (index == 0) {
	                    ft.replace(R.id.details, details);
	                } else {
	                    ft.replace(R.id.a_item, details);
	                }
	                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
	                ft.commit();
	            }

	        } else {
	            // Otherwise we need to launch a new activity to display
	            // the dialog fragment with selected text.
	            Intent intent = new Intent();
	            intent.setClass(getActivity(), DetailsActivity.class);
	            intent.putExtra("index", index);
	            startActivity(intent);
	        }
	        */
	    }
	}
	
	
	/* ***************************************************************************
	 * MEDIA PLAYER
	 */
	
	  private void playAudio(String url) throws Exception
	    {
	        killMediaPlayer();

	        mediaPlayer = new MediaPlayer();
	        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
	        mediaPlayer.setDataSource(url);
	        mediaPlayer.prepare();
	        mediaPlayer.start();
	    }
	  
	  private void killMediaPlayer() {
	        if(mediaPlayer!=null) {
	            try {
	                mediaPlayer.release();
	            }
	            catch(Exception e) {
	                e.printStackTrace();
	            }
	        }
	    }
	  
	  public void showToast(String str){
			Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
	  }
	    
		private class DownloadTask extends AsyncTask<String, Void, JSONArray> {
			private Context ctx;
			public DownloadTask(Context context){
	            super();
	            this.ctx=context;
	        }
			
			@Override
			protected JSONArray doInBackground(String... urls) {
				JSONArray results = null;
				for (String url : urls) {
					try {
						results = JSONLoader.loadTracksJSON( url );
					} catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//return results;
				}
				return results;
			}

			@Override
			protected void onPostExecute(JSONArray results) {
				ArrayList<String> stringArrayList = new ArrayList<String>();
				showToast("onPostExecute");
				for (int i = 0; i<results.length(); i++ ) {
					try {
						String value = results.getJSONObject(i).getString("name");
						stringArrayList.add( value );
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				playList = stringArrayList.toArray(new String[stringArrayList.size()]);
				onJSONReady(results);
				songIndex = 0;
				TitlesFragment fragment = (TitlesFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:"+R.id.pager+":"+currentTab);
				

				if(fragment != null)  // could be null if not instantiated yet
				{
					 fragment.setListAdapter(new ArrayAdapter<String>(this.ctx, android.R.layout.simple_list_item_activated_1, playList));
//					 View fragmentView;
//					 if( (fragmentView = fragment.getView()) != null ) 
//					 {
//					 
//					 }
				}
			}
		}


		@Override
		public void onTabReselected(Tab tab, android.app.FragmentTransaction ft) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onTabSelected(Tab tab, android.app.FragmentTransaction ft) {
			// TODO Auto-generated method stub
			showToast("onTabSelected");
			mViewPager.setCurrentItem( tab.getPosition() );
			currentTAG = TAGS_TABS[ tab.getPosition() ];
			currentTab = tab.getPosition();
			reloadTag();
		}

		@Override
		public void onTabUnselected(Tab tab, android.app.FragmentTransaction ft) {
			// TODO Auto-generated method stub
			
		}
}
