
package com.stefandinic.test;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.stefandinic.test.display.DisplayModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView itemList;

    public static final String TITLE = "title";
    public static final String DESC = "desc";
    public static final String IMG = "image";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Create global configuration and initialize image loader with this configuration
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .build();

        ImageLoader.getInstance().init(config);

        itemList = (ListView) findViewById(R.id.itemList);
    }

    public class JSONTask extends AsyncTask<String, String, List<DisplayModel>> {

        @Override
        protected List<DisplayModel> doInBackground(String...params) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();

                String line = "";

                while((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                String finalJson = buffer.toString();

                JSONArray parentArray = new JSONArray(finalJson);
                List<DisplayModel> displayModelList = new ArrayList<>();

                for (int i = 0; i < parentArray.length(); i++) {

                    JSONObject finalObject = parentArray.getJSONObject(i);
                    DisplayModel displayModel = new DisplayModel();

                    //Adding data to the displayModel that will be added to the displayModelList
                    displayModel.setTitle(finalObject.getString("title"));
                    displayModel.setDescription(finalObject.getString("description"));
                    displayModel.setImage(finalObject.getString("image"));

                    //Adding the final object in the list
                    displayModelList.add(displayModel);
                }
                return displayModelList;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<DisplayModel> result) {

            super.onPostExecute(result);

            //Need to set data to the list
            final DisplayAdapter adapter = new DisplayAdapter(getApplicationContext(), R.layout.row, result);
            itemList.setAdapter(adapter);

            //Setting onItemClickListener
            itemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    //Using Intent for passing data
                    DisplayModel item = (DisplayModel)adapter.getItem(position);
                    Intent intent = new Intent(getApplicationContext(), SecondActivity.class);

                    intent.putExtra(TITLE, item.getTitle());
                    intent.putExtra(DESC, item.getDescription());
                    intent.putExtra(IMG, item.getImage());
                    startActivity(intent);
                }
            });
        }
    }

    public class DisplayAdapter extends ArrayAdapter {

        private List<DisplayModel> displayModelList;
        private int resource;
        private LayoutInflater inflater;

        public DisplayAdapter(Context context, int resource, List<DisplayModel> objects) {

            super(context, resource, objects);
            displayModelList = objects;
            this.resource = resource;
            inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = inflater.inflate(resource, null);
            }

            TextView titleId;
            TextView descId;
            ImageView imageId;

            titleId = (TextView)convertView.findViewById(R.id.titleId);
            descId = (TextView)convertView.findViewById(R.id.descId);
            imageId = (ImageView)convertView.findViewById(R.id.imageView);

            //Loading image
            ImageLoader.getInstance().displayImage(displayModelList.get(position).getImage(), imageId);

            //Displaying title and description in the list
            titleId.setText(displayModelList.get(position).getTitle());
            descId.setText(displayModelList.get(position).getDescription());

            return convertView;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_refresh) {

            //Passing JSON file URL
            new JSONTask().execute("https://raw.githubusercontent.com/danieloskarsson/mobile-coding-exercise/master/items.json");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
