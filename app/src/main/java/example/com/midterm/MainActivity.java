package example.com.midterm;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static example.com.midterm.HttpUtils.getResponse;

public class MainActivity extends ListActivity  {

    private ProgressDialog pDialog;

    // URL to get contacts JSON
    private static String url = "http://joseniandroid.herokuapp.com/api/books";

    // JSON Node names
    private static final String TAG_books = "allbooks";
    private static final String TAG_id = "_id";
    private static final String TAG_title = "title";
    private static final String  TAG_isRead ="isRead";

    // contacts JSONArray
    JSONArray BooksArray = null;

    // Hashmap for ListView
    ArrayList<HashMap<String, String>> BookList;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BookList = new ArrayList<HashMap<String, String>>();

        ListView lv = getListView();

        new GetBooks().execute();
    }
    /**
     * Async task class to get json by making HTTP call
     * */
    private class GetBooks extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance

            // Making a request to url and getting response
            String jsonStr = getResponse(url, "GET");

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    // Getting JSON Array node
                    BooksArray = jsonObj.getJSONArray(TAG_books);
                    // looping through All Contacts
                    for (int i = 0; i < BooksArray.length(); i++) {
                        JSONObject c = BooksArray.getJSONObject(i);
                        String title = c.getString(TAG_title);
                        String isRead = c.getString(TAG_isRead);

                        HashMap<String, String> contact = new HashMap<String, String>();
                        contact.put(TAG_title, title);
                        contact.put(TAG_isRead, isRead);
                        BookList.add(contact);
  //                      BookList.add(JSONObject.getString("title"));
//                        BookList.add(contact);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, BookList,
                    R.layout.list_item,
                    new String[]{"title"},
                    new int[]{R.id.tvTitle});

            setListAdapter(adapter);
        }

    }

}