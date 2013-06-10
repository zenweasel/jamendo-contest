package com.rapptors.jamood;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.LauncherActivity.ListItem;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TrackListAdapter extends ArrayAdapter<JSONObject> {

		  private final Context context;
//		  private final String[] values;
		  final JSONObject[] values;
		  public boolean markDefault = true;

		  static class ViewHolder {
			    public TextView text;
//			    public ImageView image;
		  }

		  
//		  public TrackListAdapter(Context context, String[] values) {
		  public TrackListAdapter(Context context, JSONObject[] values) {
		    super(context, android.R.layout.simple_list_item_activated_1, values);
		    this.context = context;
		    this.values = values;
		  }

		  @Override
		  public View getView(int position, View convertView, ViewGroup parent) {
		    LayoutInflater inflater = (LayoutInflater) context
		        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    
		    View rowView = convertView;
		    if (rowView == null) {
		      rowView = inflater.inflate(android.R.layout.simple_list_item_activated_1, parent, false);
		      ViewHolder viewHolder = new ViewHolder();
		      viewHolder.text = (TextView) rowView.findViewById(android.R.id.text1);
		      rowView.setTag(viewHolder);
		    }

		    ViewHolder holder = (ViewHolder) rowView.getTag();
		    JSONObject jsonObj = values[position];
		    try {
		    	holder.text.setText(jsonObj.getString("name"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    
		    if ( position == selectedItem )
		    {
		    	Log.d("jamood", "selctedItem: "+selectedItem);
//		    	rowView.setSelected(true);
//		    	rowView.setBackgroundColor(Color.BLUE);
//		    	rowView.setBackgroundResource( android.R.drawable.list_activated_holo );
		    }
		    else {
//		    	rowView.setSelected(false);
//		    	rowView.setBackgroundColor(Color.TRANSPARENT);
//		    	rowView.setBackgroundResource(null);
		    }
		    
		    
		    // Change the icon for Windows and iPhone
//		    String s = values[position].getString("name");
//		    if (s.startsWith("Windows7") || s.startsWith("iPhone")
//		        || s.startsWith("Solaris")) {
//		      imageView.setImageResource(R.drawable.no);
//		    } else {
//		      imageView.setImageResource(R.drawable.ok);
//		    }
		    

		    return rowView;
		  }
	
//		  public void markSelected(int position){
//			  View currView = view.findViewWithTag("item-"+position);
//			  currView.setBackgroundColor(Color.RED);
//		  }
		  
		  private int selectedItem = 0;

		  public void setSelectedItem(int position) {
		        selectedItem = position;
		  }
}
