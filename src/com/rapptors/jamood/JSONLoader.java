package com.rapptors.jamood;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class JSONLoader {
	private static JSONArray results;

	public static JSONArray loadTracksJSON(String jsonURL) throws IllegalStateException, IOException, JSONException{
		DefaultHttpClient   httpclient = new DefaultHttpClient(new BasicHttpParams());
		HttpGet httpget = new HttpGet(jsonURL);
		// Depends on your web service
		//httpget.setHeader("Content-type", "application/json");

		InputStream inputStream = null;
		String result = null;
		Log.d("jamood", "jsonURL:"+jsonURL);
		HttpResponse response = httpclient.execute(httpget);           
		HttpEntity entity = response.getEntity();

		inputStream = entity.getContent();
		// json is UTF-8 by default i beleive
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
		StringBuilder sb = new StringBuilder();

		String line = null;
		while ((line = reader.readLine()) != null)
		{
		    sb.append(line + "\n");
		}
		result = sb.toString();
		
		JSONObject jObject = new JSONObject(result);
		return results = jObject.getJSONArray("results");
	}
	
	public static String getAudio(int i) throws JSONException {
		JSONObject oneObject = results.getJSONObject(i);
		String ret = oneObject.getString("audio");
		return ret;
	}
	

}
