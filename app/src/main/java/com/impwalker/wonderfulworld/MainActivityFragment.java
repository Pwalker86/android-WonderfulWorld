package com.impwalker.wonderfulworld;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

  private ArrayAdapter<String> listAdapter;
  private ListView listView;

  public MainActivityFragment() {
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_main, container, false);

    ArrayList<String> emptyList = new ArrayList<>();

    listAdapter = new ArrayAdapter<>(getActivity(),R.layout.item_row, emptyList );

    listView = (ListView)  rootView.findViewById(R.id.wonder_list);
    listView.setAdapter(listAdapter);
    new FetchWondersTask().execute();
    return rootView;
  }

  public class FetchWondersTask extends AsyncTask<Void, Void, String[]> {

    @Override
    protected String[] doInBackground(Void... params) {
      final String LOG_TAG="WONDER_TASK";

      HttpURLConnection urlConnection = null;
      BufferedReader reader = null;

      String wonderJsonStr = null;
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
        wonderJsonStr = buffer.toString();

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

      try{
        return parseJson(wonderJsonStr);
      } catch (JSONException e){
        Log.e(LOG_TAG, e.getMessage(), e);
        e.printStackTrace();
      }

      return null;
    }

    private String[] parseJson(String wonderJsonStr)
        throws JSONException {
      JSONArray wonderArray = new JSONArray(wonderJsonStr);

      String[] resultStrs = new String[wonderArray.length()];
      for(int i = 0; i < wonderArray.length(); i++) {
        JSONObject wonderObject = wonderArray.getJSONObject(i);
        String name = wonderObject.getString("name");
        resultStrs[i] = name;
      }
      return resultStrs;
    }

    @Override
    protected void onPostExecute(String[] result) {
      if (result != null) {
        listAdapter.clear();
        for(String wonderStr : result) {
          listAdapter.add(wonderStr);
        }
      }
    }
  }
}
