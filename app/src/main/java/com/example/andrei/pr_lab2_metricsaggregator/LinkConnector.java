package com.example.andrei.pr_lab2_metricsaggregator;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;


/**
 * Created by andrei on 3/5/18.
 */

public class LinkConnector extends AsyncTask<Void, Void, String> {
        private static final String TAG = "LinkConnector";
        public static final String URL_ADDRESS="https://desolate-ravine-43301.herokuapp.com/";
        private static String key;
        private static List<Link> linksList=new ArrayList<>();
        private static Stack<Link> linksStack=new Stack<>();

    public static synchronized Stack<Link> getLinksStack() {
        return linksStack;
    }

    public static synchronized List<Link> getLinksList() {
        return linksList;
    }

    public static String getKey() {
        return key;
    }

    public static void setKey(String key) {
        LinkConnector.key = key;
    }

    @Override
        protected String doInBackground(Void... params) {
            try {
                //Create an HTTP client
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(URL_ADDRESS);

                //Perform the request and check the status code
                HttpResponse response = client.execute(post);

                StatusLine statusLine = response.getStatusLine();
                if(statusLine.getStatusCode() == 200) {
                    setKey(response.getFirstHeader("Session").getValue());
                    HttpEntity entity = response.getEntity();
                    InputStream content = entity.getContent();

                    try {
                        //Read the server response and attempt to parse it as JSON
                        Reader reader = new InputStreamReader(content);

                        GsonBuilder gsonBuilder = new GsonBuilder();

                        Gson gson = gsonBuilder.create();
                        linksList = Arrays.asList(gson.fromJson(reader, Link[].class));
                        Log.i(TAG,"LINKS LIST SIZE = "+String.valueOf(linksList.size()));
                        Log.i(TAG,"Session link = "+key);
                        for(Link link:linksList){
                            Log.i(TAG,link.getPath());
                        }
                        cloneToStack();
                        content.close();

                    } catch (Exception ex) {
                        Log.e(TAG, "Failed to parse JSON due to: " + ex);
                    }
                } else {
                    Log.e(TAG, "Server responded with status code: " + statusLine.getStatusCode());
                }
            } catch(Exception ex) {
                Log.e(TAG, "Failed to send HTTP POST request due to: " + ex);
            }
            return null;
        }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        new DeviceConnector().performTask();
        for(Device dev:DeviceConnector.devicesList){
            Log.e("DEVICE LIST ELEMENT ",dev.getDeviceId());
        }

    }

    private void cloneToStack() {
        for(int i=0;i<linksList.size();i++){
            linksStack.push(linksList.get(i));
        }
    }
}


