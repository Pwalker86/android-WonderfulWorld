package com.impwalker.wonderfulworld;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class DetailsActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_details);

    Intent intent = getIntent();
    String name = intent.getStringExtra(MainActivityFragment.TAG_NAME);
    String wikiLink = intent.getStringExtra(MainActivityFragment.TAG_WIKILINK);
    String location = intent.getStringExtra(MainActivityFragment.TAG_LOCATION);
    String yearBuilt = intent.getStringExtra(MainActivityFragment.TAG_YEAR_BUILT);
//    String image = intent.getStringExtra(MainActivityFragment.TAG_IMAGE);

//    new FetchWonderImage(image,(ImageView) findViewById(R.id.imageView)).execute();

    TextView nameView = (TextView) findViewById(R.id.wonder_name);
    nameView.setText(String.format(getString(R.string.wonder_what), name));

    TextView wikiView = (TextView) findViewById(R.id.wikiLink);
    wikiView.setText(wikiLink);

    TextView locationView = (TextView) findViewById(R.id.location);
    locationView.setText(String.format(getString(R.string.wonder_where), location));

    TextView yearView = (TextView) findViewById(R.id.yearBuilt);
    yearView.setText(String.format(getString(R.string.wonder_when), yearBuilt));

  }

//  public class FetchWonderImage extends AsyncTask<Void,Void,Bitmap> {
//    private String url;
//    private ImageView imageView;
//
//    public FetchWonderImage(String url, ImageView imageView) {
//      this.url = url;
//      this.imageView = imageView;
//    }
//
//    @Override
//    protected Bitmap doInBackground(Void... params) {
//      try {
//        URL urlConnection = new URL(url);
//        HttpURLConnection connection = (HttpURLConnection) urlConnection
//                                                               .openConnection();
//        connection.setDoInput(true);
//        connection.connect();
//        InputStream input = connection.getInputStream();
//        return BitmapFactory.decodeStream(input);
//      } catch (Exception e) {
//        e.printStackTrace();
//      }
//      return null;
//    }
//
//    @Override
//    protected void onPostExecute(Bitmap result) {
//      super.onPostExecute(result);
//      imageView.setImageBitmap(result);
//    }
//  }
}
