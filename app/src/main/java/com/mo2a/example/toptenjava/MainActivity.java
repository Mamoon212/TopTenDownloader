package com.mo2a.example.toptenjava;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ListView listEntries;
    private String feedUrl="http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml";
    private int limit= 10;
    private String feedChachedUrl= "invalid";
    public static final String LIMIT_KEY= "KEY1";
    public static final String URL_KEY= "KEY2";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listEntries= findViewById(R.id.xmlListview);
        if(savedInstanceState!= null){
            limit= savedInstanceState.getInt(LIMIT_KEY);
            feedUrl= savedInstanceState.getString(URL_KEY);
        }
        downloadUrl(String.format(feedUrl, limit));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.feeds_menu, menu);
        if(limit==10){
            menu.findItem(R.id.mnu10).setChecked(true);
        }else{
            menu.findItem(R.id.mnu20).setChecked(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id= item.getItemId();
        switch (id){
            case R.id.mnuFree:
                feedUrl= "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml";
                break;
            case R.id.mnuPaid:
                feedUrl="http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/toppaidapplications/limit=%d/xml";
                break;
            case R.id.mnuSongs:
                feedUrl="http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=%d/xml";
                break;
            case R.id.mnu10:
            case R.id.mnu20:
                if(!item.isChecked()){
                    item.setChecked(true);
                    limit= 35- limit;
                }
                break;
            case R.id.mnuRefresh:
                feedChachedUrl="kalb";
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        downloadUrl(String.format(feedUrl, limit));
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(LIMIT_KEY, limit);
        outState.putString(URL_KEY, feedUrl);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        limit= savedInstanceState.getInt(LIMIT_KEY);
        feedUrl= savedInstanceState.getString(URL_KEY);
    }

    private void downloadUrl(String url){
        if(!feedUrl.equalsIgnoreCase(feedChachedUrl)){
            Log.d(TAG, "downloadUrl: starting async task");
            DownloadData downloadData = new DownloadData();
            downloadData.execute(url);
            feedChachedUrl= feedUrl;
            Log.d(TAG, "downloadUrl: done");
        }

    }

    private class DownloadData extends AsyncTask<String, Void, String> {
        private static final String TAG = "DownloadData";
        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG, "doInBackground: starts with "+ strings[0]);
            String rssFeed= downloadXML(strings[0]);
            if(rssFeed == null){
                Log.e(TAG, "doInBackground: error downloading");
            }
            return rssFeed;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            ParseEntries parseEntries= new ParseEntries();
            parseEntries.parse(s);

            FeedAdapter feedAdapter= new FeedAdapter(MainActivity.this, R.layout.list_record, parseEntries.getEntries());
            listEntries.setAdapter(feedAdapter);
        }

        private String downloadXML(String urlPath) {
            StringBuilder xmlResult = new StringBuilder();
            try {
                URL url = new URL(urlPath);
                HttpURLConnection conn= (HttpURLConnection) url.openConnection();
                int response= conn.getResponseCode();
                Log.d(TAG, "downloadXML: response code was "+ response);
                BufferedReader reader= new BufferedReader(new InputStreamReader(conn.getInputStream()));

                int charsRead;
                char[] inputBuffer = new char[500];
                while(true) {
                    charsRead = reader.read(inputBuffer);
                    if(charsRead < 0) {
                        break;
                    }
                    if(charsRead > 0) {
                        xmlResult.append(String.copyValueOf(inputBuffer, 0, charsRead));
                    }
                }
                reader.close();
                return xmlResult.toString();
            } catch(MalformedURLException e) {
                Log.e(TAG, "downloadXML: invalid url "+ e.getMessage());
            } catch(IOException e) {
                Log.e(TAG, "downloadXML: error reading data "+ e.getMessage());
            } catch(SecurityException e) {
                Log.e(TAG, "downloadXML: security exception maybe permission "+ e.getMessage());
            }
            return null;
        }
    }
}
