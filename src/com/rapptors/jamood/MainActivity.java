package com.rapptors.jamood;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.rapptors.jamood.R;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener, OnCompletionListener, OnClickListener {
	
	static String currentTAG = "calm";
	static final Integer LIMIT = 10;
	static final String TRACKS_URL = "http://api.jamendo.com/v3.0/tracks/?format=json&groupby=artist_id";
	static final String[] TAGS_TABS = {"calm", "easy", "yoga", "happy", "sad", "space", "funky", "disco", "triphop","hiphop", "rock", "sexy","romantic"};
		    private MediaPlayer mediaPlayer;
		    private int playbackPosition=0;
		    
		    
    Map<String, String[]> jsonMap1 = new HashMap<String, String[]>(  );
    
	static String currentSong = null;
	private String currentArt;
	
	private Integer songIndex = 0;
	private Integer currentTab = 0;
	
	
	JSONArray results;
	static JSONObject[] playList = {};
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
		String CLIENT_ID = ((JamoodApplication) this.getApplication()).CLIENT_ID;
		String ret = TRACKS_URL+"&client_id="+CLIENT_ID + "&tags="+ currentTAG + "&limit="+LIMIT;
		//&speed=medium+high+veryhigh
		return ret;
	}
	
	private void __initMaps(){
		String[] calmTags = { "slow","melodic", "piano"};
		jsonMap1.put("calm", calmTags);
		String[] easyTags = { "easylistening", "chillout" };
		jsonMap1.put("easy", easyTags);
		
		String[] spaceTags = {  "relaxing", "health", "wellness", "newage", "ambient" };
		jsonMap1.put("space", spaceTags);
		
		String[] yogaTags = {  "meditation", "wellness", "slow", "ethno", "xylophone", "meditative" };
		jsonMap1.put("yoga", yogaTags);
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		__initMaps();
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
		
//		int savedTag = savedInstanceState.getInt("current_tag", 0);
//		Log.d("jamood", "savetag: "+savedTag);
//		mViewPager.setCurrentItem( savedTag );
		__initPlayerButtons();
	}
	
    private void __initPlayerButtons(){
        ImageButton btnPlay = (ImageButton) findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(this);
        
        ImageButton btnNext = (ImageButton)findViewById(R.id.btnNext);
        btnNext.setOnClickListener(this);
        
        ImageButton btnPrev = (ImageButton) findViewById(R.id.btnPrev);
        btnPrev.setOnClickListener(this);
        
        ((ImageView) findViewById(R.id.albumArt)).setOnClickListener(this);
    }
	
	private void reloadTag() {
		DownloadTask task = new DownloadTask(this);
		task.execute(new String[] { tracksURL() });
	}
	
	public void onJSONReady( JSONArray r ){ //JSONArray results
		results = r;
		
		ArrayList<JSONObject> stringArrayList = new ArrayList<JSONObject>();

		for (int i = 0; i<results.length(); i++ ) {
			try {
				JSONObject value = results.getJSONObject(i);//.getString("name");
				stringArrayList.add( value );
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		playList = stringArrayList.toArray(new JSONObject[stringArrayList.size()]);
		songIndex = 0;
		TitlesFragment fragment = (TitlesFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:"+R.id.pager+":"+currentTab);
		if(fragment != null)  // could be null if not instantiated yet
		{
			 fragment.setListAdapter(new TrackListAdapter(this, playList));
//			 ((TrackListAdapter) fragment.getListView().getAdapter()).setSelectedItem(0);
//			 fragment.getListView().setItemChecked(songIndex, true);
			 
		}
		
		playCurrentSong();
	}

	public void playCurrentSong() {
		JSONObject oneObject = null;
		String audioURL = null, audioTitle = null, albumArt=null;

		try {
			// Extract Song URL
			oneObject = results.getJSONObject(songIndex);
			audioURL = oneObject.getString("audio");
			audioTitle = oneObject.getString("name");
			albumArt = oneObject.getString("album_image");
			
			currentSong = audioTitle;
			ActionBar ab = getActionBar();
			ab.setTitle(audioTitle);
			
			// Update List UI
			TitlesFragment fragment = (TitlesFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:"+R.id.pager+":"+currentTab);
			if(fragment != null)  // could be null if not instantiated yet
			{
//				ListView lv = (ListView) findViewById(android.R.id.list);
				ListView lv = fragment.getListView();
				lv.setItemChecked(songIndex, true);
				lv.setVisibility(View.VISIBLE);
			}

			ImageView artView = (ImageView) findViewById(R.id.albumArt);
			if (albumArt!=null){
				new DownloadImageTask(artView,  (ImageView) findViewById(R.id.albumArtLarge)).execute(albumArt);
				artView.setVisibility(View.VISIBLE);
				currentArt = albumArt;
			}
			else {
				albumArt = null;
				artView.setVisibility(View.INVISIBLE);
				((ImageView)findViewById(R.id.albumArt)).setImageResource( R.drawable.pattern_water );
			}
			
			// Play Song
			playAudio(audioURL);
			
			// Update Player UI
			updateButtonPlayState();

		} catch (JSONException e1) {
			showToast(getString(R.string.JSONException));
			e1.printStackTrace();
		}
		catch (Exception e) {
			showToast(getString(R.string.MediaPlayerException));
			e.printStackTrace();
		}
	}
	/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	*/

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

			fragment = new TitlesFragment();

			Bundle args = new Bundle();
			args.putInt( "section_number", position + 1 );	
			fragment.setArguments(args);

			return fragment;
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return TAGS_TABS.length;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			return TAGS_TABS[position].toUpperCase(l);
		}
	}

	public static class TitlesFragment extends ListFragment {
	    boolean mDualPane;
	    int mCurCheckPosition = 0;

	    @Override
	    public void onActivityCreated(Bundle savedInstanceState) {
	        super.onActivityCreated(savedInstanceState);

	        // Populate list with our static array of titles.
	        setListAdapter(new TrackListAdapter(getActivity(), playList));
	        ListView lv = getListView();
	        //lv.getChildAt(0).setSelected(true);
	  
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
	        
	        /* TODO List CONTENTS FOOTER */
//	        View footer = getLayoutInflater(savedInstanceState).inflate(R.layout.player, null);
//	        ListView ls = getListView(); //(ListView) View.findViewById(android.R.id.list);
//	        ls.addFooterView(footer);

	    }
/*    
	    @Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_titles_layout, container, false);
//			TextView dummyTextView = (TextView) rootView.findViewById(R.id.section_label);
//			dummyTextView.setText( "Refreshing..." );
			//currentSong != null ? currentSong :  Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER))
			
			
			return rootView;
		}
*/
	    @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) { 
            View view = inflater.inflate(R.layout.main, null);
            return view;
        }

	    @Override
	    public void onSaveInstanceState(Bundle outState) {
	        super.onSaveInstanceState(outState);
	        outState.putInt("curChoice", mCurCheckPosition);
	    }

	    @Override
	    public void onListItemClick(ListView l, View v, int position, long id) {
//	        v.setSelected(true);
	    	TrackListAdapter ad = (TrackListAdapter) l.getAdapter();

//	    	ad.setSelectedItem(position);
//	    	ad.notifyDataSetChanged();
//	    	l.setItemChecked(-1, true);
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
	        mediaPlayer.setOnCompletionListener(this);
	        mediaPlayer.setDataSource(url);
	        mediaPlayer.prepare();
	        mediaPlayer.start();
			updateButtonPlayState();
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
	    
	  
	  /** ***************************************************************************
	   * Download Task
	   * @author test
	   *
	   */
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
				onJSONReady(results);
			}
		}


		@Override
		public void onTabReselected(Tab tab, android.app.FragmentTransaction ft) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onTabSelected(Tab tab, android.app.FragmentTransaction ft) {
			mViewPager.setCurrentItem( tab.getPosition() );
			String tagValue = TAGS_TABS[ tab.getPosition() ];
			String[] tagMaps = jsonMap1.get(tagValue);
			

			if (tagMaps != null && tagMaps.length>0) {
				Random rgenerator = new Random();
				int randTag = rgenerator.nextInt(tagMaps.length);
				currentTAG = tagMaps[randTag];
			}
			else {
				currentTAG = tagValue;
			}
			
			TitlesFragment fragment = (TitlesFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:"+R.id.pager+":"+currentTab);
			if(fragment != null)  // could be null if not instantiated yet
			{
				ListView lv = fragment.getListView();
				lv.setVisibility(View.INVISIBLE);
				__setArtVisible(false);
			}
			
			currentTab = tab.getPosition();			
			reloadTag();
		}

		@Override
		public void onTabUnselected(Tab tab, android.app.FragmentTransaction ft) {
			// TODO Auto-generated method stub
			
		}

		
		@Override
		public void onCompletion(MediaPlayer mp) {
			nextSong();
			playCurrentSong();
		}

		private void nextSong() {
			if (songIndex+1 < playList.length) {
				songIndex++;
			}
			else {
				songIndex = 0;
			}
		}
		
		private void prevSong() {
			if (songIndex-1 > 0 ) {
				songIndex--;
			}
			else {
				songIndex = playList.length-1;
			}
		}

		@Override
		public void onClick(View v) {
			if(v.getId() == R.id.btnPlay){
				if(!mediaPlayer.isPlaying()){
					mediaPlayer.start();
				}else {
					mediaPlayer.pause();
				}
				updateButtonPlayState();
			}
			else if(v.getId() == R.id.btnNext) {
				nextSong();
				mediaPlayer.stop();
				playCurrentSong();
			}
			else if(v.getId() == R.id.btnPrev) {
				prevSong();
				mediaPlayer.stop();
				playCurrentSong();
			}
			else if (v.getId() == R.id.albumArt) {
				
				
				TitlesFragment fragment = (TitlesFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:"+R.id.pager+":"+currentTab);
				if(fragment != null)  // could be null if not instantiated yet
				{
					ListView lv = fragment.getListView();
					ImageView large = (ImageView)findViewById(R.id.albumArtLarge);
					
					if (large.getVisibility() != View.VISIBLE) {
						//Drawable d = ((ImageView)findViewById(R.id.albumArt)).getDrawable();						
						//large.setBackground(d);
						
//						new DownloadImageTask((ImageView) findViewById(R.id.albumArtLarge)).execute(currentArt);
						
						__setArtVisible(true);
					}
					else {
						
						__setArtVisible(false);
						
					}
				}
			}
		}
		
		private void __setArtVisible(boolean visible){
			TitlesFragment fragment = (TitlesFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:"+R.id.pager+":"+currentTab);
			if(fragment != null)  // could be null if not instantiated yet
			{
				ListView lv = fragment.getListView();
				ImageView large = (ImageView)findViewById(R.id.albumArtLarge);
				if (visible) {
					lv.setVisibility(View.GONE);
					large.setVisibility(View.VISIBLE);
				}
				else {
					lv.setVisibility(View.VISIBLE);
					large.setVisibility(View.GONE);
				}
			}
		}

		private void updateButtonPlayState() {
			ImageButton btnPlay = (ImageButton) findViewById(R.id.btnPlay);
			if(!mediaPlayer.isPlaying()){
				btnPlay.setImageResource(R.drawable.btn_play);
			}else {
				btnPlay.setImageResource(R.drawable.btn_pause);
			}
		}
		
		@Override
		public void onSaveInstanceState(Bundle savedInstanceState) {
		  super.onSaveInstanceState(savedInstanceState);
		  // Save UI state changes to the savedInstanceState.
		  // This bundle will be passed to onCreate if the process is
		  // killed and restarted.
		  savedInstanceState.putInt("current_tab", currentTab);

		}

		@Override
		public void onRestoreInstanceState(Bundle savedInstanceState) {
		  super.onRestoreInstanceState(savedInstanceState);
		  // Restore UI state from the savedInstanceState.
		  // This bundle has also been passed to onCreate.

		  int myInt = savedInstanceState.getInt("current_tab");
		  currentTab = myInt;
		}
}
