package com.impwalker.wonderfulworld;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivityFragment extends Fragment {
  static final String TAG_NAME = "name";
  static final String TAG_IMAGE = "image";
  static final String TAG_WIKILINK = "wikipedia";
  static final String TAG_LOCATION = "location";
  static final String TAG_REGION = "region";
  static final String TAG_YEAR_BUILT = "year_built";

  private ListView listView;
  ArrayList<HashMap<String, String>> wonderList;

  public MainActivityFragment() {
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_main, container, false);
    listView = (ListView) rootView.findViewById(R.id.wonder_list_view);
    wonderList = new ArrayList<>();

    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String name = ((TextView) view.findViewById(R.id.wonder_name))
                          .getText().toString();
        String wikiLink = wonderList.get(position).get(TAG_WIKILINK);
        String wikiImage = wonderList.get(position).get(TAG_IMAGE);
        String location = wonderList.get(position).get(TAG_LOCATION);
        String region = wonderList.get(position).get(TAG_REGION);
        String final_location = location + ", " + region;
        String year_built = wonderList.get(position).get(TAG_YEAR_BUILT);
//
        Intent in = new Intent(getActivity(),
                                  DetailsActivity.class);
        in.putExtra(TAG_NAME, name);
        in.putExtra(TAG_WIKILINK, wikiLink);
        in.putExtra(TAG_LOCATION, final_location);
        in.putExtra(TAG_YEAR_BUILT, year_built);
        in.putExtra(TAG_IMAGE, wikiImage);
        startActivity(in);
      }
    });


    new FetchWondersTask().execute();
    return rootView;
  }

  public class FetchWondersTask extends AsyncTask<Void, Void, Void> {

    @Override
    protected Void doInBackground(Void... params) {
      final String LOG_TAG="WONDER_TASK";

      HttpURLConnection urlConnection = null;
      BufferedReader reader = null;

      try {
        URL url = new URL("http://jsonblob.com/api/56aa8522e4b01190df4c13a2");

        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.connect();
        int responseCode = urlConnection.getResponseCode();

        if (responseCode == 301) {
          // Redirect to new URL
          url = new URL(urlConnection.getURL().toString());
          urlConnection.disconnect();
          urlConnection = (HttpURLConnection) url.openConnection();
          urlConnection.setRequestMethod("GET");
          urlConnection.connect();
        }

        // Read the input stream into a String
        InputStream inputStream = urlConnection.getInputStream();
        StringBuilder buffer = new StringBuilder();

        if (inputStream == null) {
          // Nothing to do.
          return null;
        }
        reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        while ((line = reader.readLine()) != null) {
          buffer.append(line).append("\n");
        }

        if (buffer.length() == 0) {
          // Stream was empty.  No point in parsing.
          Log.v(LOG_TAG, "buffer was empty");
          return null;
        }

        String wonderJsonStr = buffer.toString();

        try {
          parseJson(wonderJsonStr);
        } catch (JSONException e) {
          e.printStackTrace();
        }

      } catch (IOException e) {
        Log.e(LOG_TAG, "Error ", e);
        return null;
      } finally {
        if (urlConnection != null) {
          urlConnection.disconnect();
        }
        if (reader != null) {
          try {
            reader.close();
          } catch (final IOException e) {
            Log.e(LOG_TAG, "Error closing stream", e);
          }
        }
      }
      return null;
    }

    private ArrayList parseJson(String wonderJsonStr)
        throws JSONException {
      JSONArray wonderArray = new JSONArray(wonderJsonStr);
//
      for(int i = 0; i < wonderArray.length(); i++) {

        JSONObject wonderObject = wonderArray.getJSONObject(i);
        String name = wonderObject.getString(TAG_NAME);
        String imageUrl = wonderObject.getString(TAG_IMAGE);
        String wikiLink = wonderObject.getString(TAG_WIKILINK);
        String location = wonderObject.getString(TAG_LOCATION);
        String region = wonderObject.getString(TAG_REGION);
        String yearBuilt = wonderObject.getString(TAG_YEAR_BUILT);

        HashMap<String, String> wonder = new HashMap<>();

        wonder.put(TAG_NAME, name);
        wonder.put(TAG_IMAGE, imageUrl);
        wonder.put(TAG_WIKILINK, wikiLink);
        wonder.put(TAG_LOCATION, location);
        wonder.put(TAG_REGION, region);
        wonder.put(TAG_YEAR_BUILT, yearBuilt);

        wonderList.add(wonder);

      }
      return null;
    }

    @Override
    protected void onPostExecute(Void result) {
      if(android.os.Debug.isDebuggerConnected())
        android.os.Debug.waitForDebugger();
        ListAdapter adapter = new SimpleAdapter(
          getActivity(), wonderList,
          R.layout.item_row, new String[]{TAG_NAME},
          new int[]{R.id.wonder_name}
          );

        listView.setAdapter(adapter);
    }
  }
}
