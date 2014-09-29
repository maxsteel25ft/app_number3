package com.example.julio.timezonegoogle;
/*
* New trial on Sept 27, 2014
* Here I am presenting to options of asking for response:
* option a) as a JSON object,
* option b) as an XML file.
*
* both treatment options I will explore them here
*
*/

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;


public class MyActivity extends Activity {
	final static String DEFAULT_LAT = "40.75649";
	final static String DEFAULT_LON = "-73.98626";
	final static String API_KEY = "AIzaSyAO-q2NXYeZ7tgVwD2EzcuNrQ97ZdG5328";
	String strBaseUrl = "https://maps.googleapis.com/maps/api/timezone/json?";

	TextView textTimeZone;
	EditText inputLat, inputLon;
	Button buttonGetTimeZone;
	Button.OnClickListener buttonGetTimeZoneOnClickistener
			= new Button.OnClickListener() {
//		this is the JSON option
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String lat = inputLat.getText().toString();
			String lon = inputLon.getText().toString();
			String rqsurl = strBaseUrl
			+ "location=" + lat
			+ "," + lon
			+ "&timestamp=1331161200&sensor=false";
//			+ API_KEY;
			Log.d("***onClick","Llamando a Google TZ");
			Log.d("***onClick", rqsurl);
			String jsonResult = QueryGeonames(rqsurl);
			String parsedResult = ParseJSON(jsonResult);
			textTimeZone.setText(parsedResult);
		}
	};

	/**
	 * Called when the activity is first created.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my);

		textTimeZone = (TextView) findViewById(R.id.timezone);
		inputLat = (EditText) findViewById(R.id.lat);
		inputLat.setText(DEFAULT_LAT);
		inputLon = (EditText) findViewById(R.id.lon);
		inputLon.setText(DEFAULT_LON);
		buttonGetTimeZone = (Button) findViewById(R.id.getTimeZone);
		buttonGetTimeZone.setOnClickListener(buttonGetTimeZoneOnClickistener);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.my, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private String QueryGeonames(String q) {
		String qResult = null;
		HttpClient httpClient = new DefaultHttpClient(); // get request via HttpClient
		HttpGet httpGet = new HttpGet(q);
		Log.d("***Query",q);
		try {
			HttpEntity httpEntity = httpClient.execute(httpGet).getEntity();
			if (httpEntity != null) {
				InputStream inputStream = httpEntity.getContent();
				Reader in = new InputStreamReader(inputStream);
				BufferedReader bufferedreader = new BufferedReader(in); //Get the response
				StringBuilder stringBuilder = new StringBuilder();
				String stringReadLine = null;
				while ((stringReadLine = bufferedreader.readLine()) != null) {
					stringBuilder.append(stringReadLine + "\n");
				}
				qResult = stringBuilder.toString();
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return qResult;
	}


	private String ParseJSON(String json) {
		String jResult = null;
		try {
			JSONObject JsonObject = new JSONObject(json);
			jResult = "\n"
					+ "lat: " + JsonObject.getString("lat") + "\n"
					+ "lng: " + JsonObject.getString("lng") + "\n"
					+ "countryName: " + JsonObject.getString("countryName") + "\n"
					+ "countryCode: " + JsonObject.getString("countryCode") + "\n"
					+ "timezoneId: " + JsonObject.getString("timezoneId") + "\n"
					+ "rawOffset: " + JsonObject.getString("rawOffset") + "\n"
					+ "dstOffset: " + JsonObject.getString("dstOffset") + "\n"
					+ "gmtOffset: " + JsonObject.getString("gmtOffset") + "\n";
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jResult;
	}

/*	String get_xml_server_reponse(String server_url){
		URL xml_server = null;
		String xmltext = "";
		InputStream input;
		try {
			xml_server = new URL(server_url);
			try {
				input = xml_server.openConnection().getInputStream();
				final BufferedReader reader = new BufferedReader(new InputStreamReader(input));
				final StringBuilder sBuf = new StringBuilder();
				String line = null;
				try {
					while ((line = reader.readLine()) != null)
					{
						sBuf.append(line);
					}
				}
				catch (IOException e)
				{
					Log.e(e.getMessage(), "XML parser, stream2string 1");
				}
				finally {
					try {
						input.close();
					}
					catch (IOException e)
					{
						Log.e(e.getMessage(), "XML parser, stream2string 2");
					}
				}
				xmltext =  sBuf.toString();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		return  xmltext;
	}


	long get_time_zone_time_l(GeoPoint gp){
		String raw_offset = "";
		String dst_offset = "";
		double Longitude = gp.getLongitudeE6()/1E6;
		double Latitude = gp.getLatitudeE6()/1E6;
		long tsLong = System.currentTimeMillis()/1000;

		if (tsLong != 0)
		{
			// https://maps.googleapis.com/maps/api/timezone/xml?location=39.6034810,-119.6822510&timestamp=1331161200&sensor=false
			String request = "https://maps.googleapis.com/maps/api/timezone/xml?location="+Latitude+","+ Longitude+ "&timestamp="+tsLong +"&sensor=false";
			String xmltext = get_xml_server_reponse(request);
			if(xmltext.compareTo("")!= 0) {
				int startpos = xmltext.indexOf("<TimeZoneResponse");
				xmltext = xmltext.substring(startpos);
				XmlPullParser parser;
				try {
					parser = XmlPullParserFactory.newInstance().newPullParser();
					parser.setInput(new StringReader(xmltext));
					int eventType = parser.getEventType();
					String tagName = "";
					while(eventType != XmlPullParser.END_DOCUMENT) {
						switch(eventType) {
							case XmlPullParser.START_TAG:
								tagName = parser.getName();
								break;
							case XmlPullParser.TEXT :
								if  (tagName.equalsIgnoreCase("raw_offset"))
									if(raw_offset.compareTo("")== 0)
										raw_offset = parser.getText();
								if  (tagName.equalsIgnoreCase("dst_offset"))
									if(dst_offset.compareTo("")== 0)
										dst_offset = parser.getText();
								break;
						}
						try {
							eventType = parser.next();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				} catch (XmlPullParserException e) {
					e.printStackTrace();
					erg += e.toString();
				}
			}
			int ro = 0;
			if(raw_offset.compareTo("")!= 0)
			{
				float rof = str_to_float(raw_offset);
				ro = (int)rof;
			}
			int dof = 0;
			if(dst_offset.compareTo("")!= 0)
			{
				float doff = str_to_float(dst_offset);
				dof = (int)doff;
			}
			tsLong = (tsLong + ro + dof) * 1000;
		}
		return tsLong;
	}*/
}
