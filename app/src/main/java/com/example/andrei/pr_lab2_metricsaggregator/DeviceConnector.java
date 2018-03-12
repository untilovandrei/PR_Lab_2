package com.example.andrei.pr_lab2_metricsaggregator;

import android.os.Handler;
import android.util.Log;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by andrei on 3/7/18.
 */

public class DeviceConnector {
    public static final String TAG = "DeviceConnector";
    public static List<Device> devicesList=new ArrayList<>();


    public void performTask() {
        for(int i=0;i<LinkConnector.getLinksList().size();i++){
            Thread thread = new Thread(new Runnable(){
                @Override
                public void run(){
                    requestDevices();
                }
            });
            thread.start();
        }

    }

    public  void parseCSV(InputStream content) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(content));
        try {
            String csvLine;
            boolean firstLine=true;
            while ((csvLine = reader.readLine()) != null) {
                String[] row = csvLine.split(",");

                if(firstLine==false){
                    Device device=new Device(row[0],Integer.valueOf(row[1]),Double.valueOf(row[2]));
                    devicesList.add(device);
                    //Log.i("DEVICE DATA : ",device.toString());
                    Log.i("AGGREGATED DEVICE DATA : ",aggregateData(device).toString());
                }
                firstLine=false;
            }
            content.close();
        }
        catch (IOException ex) {
            throw new RuntimeException("Error in reading CSV file: "+ex);
        }

    }

    public  void parseXML(InputStream content) {
        Device device=new Device();
        try
        {
            XmlPullParserFactory factory=XmlPullParserFactory.newInstance();
            XmlPullParser parser=factory.newPullParser();
            parser.setInput(content,null);
            int event=parser.getEventType();
            String tagValue=null;
            Boolean isSiteMeta=true;

            do {
                String tagName=parser.getName();

                switch (event)
                {
                    case XmlPullParser.START_TAG:
                        if(tagName.equalsIgnoreCase("device")){
                            String id=parser.getAttributeValue(null, "id");
                            device.setDeviceId(id);
                            isSiteMeta=false;
                        }
                        break;
                    case XmlPullParser.TEXT:
                        tagValue=parser.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        if(!isSiteMeta)
                        {
                            if(tagName.equalsIgnoreCase("type"))
                            {
                                device.setSensorType(Integer.valueOf(tagValue));
                            }else if(tagName.equalsIgnoreCase("value"))
                            {
                                String value=tagValue;
                                device.setValue(Double.valueOf(value.substring(value.indexOf("/>")+1)));
                            }
                        }

                        break;
                }
                event=parser.next();
            }while (event != XmlPullParser.END_DOCUMENT);
            /*Log.i("DEVICE DATA : ",device.toString());
            devicesList.add(device);*/
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Log.i("DEVICE DATA : ",device.toString());
        Log.i("AGGREGATED DEVICE DATA : ",aggregateData(device).toString());
        devicesList.add(device);
    }

    public  void  parseJSON(InputStream content) {
        Device device=new Device();
        try {
            //Read the server response and attempt to parse it as JSON
            Reader reader = new InputStreamReader(content);
            Gson gson = new GsonBuilder().create();
            device=gson.fromJson(reader, Device.class);
            //Log.i("DEVICE DATA : ",device.toString());
            Log.i("AGGREGATED DEVICE DATA : ",aggregateData(device).toString());


        } catch (Exception ex) {
            Log.e(TAG, "Failed to parse JSON due to: " + ex);
        }
        devicesList.add(device);
    }

    public void requestDevices(){
        try {
            //Create an HTTP client
            HttpClient client = new DefaultHttpClient();

            HttpGet get = new HttpGet(LinkConnector.URL_ADDRESS+LinkConnector.getLinksStack().pop().getPath());
            get.setHeader("Session",String.valueOf(LinkConnector.getKey()));

            //Perform the request and check the status code
            HttpResponse response = client.execute(get);
            StatusLine statusLine = response.getStatusLine();

            Log.i("CONTENT_TYPE",response.getFirstHeader("Content-Type").getValue());

            if(statusLine.getStatusCode() == 200 ) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();

                String contentType= response.getFirstHeader("Content-Type").getValue();

                if(contentType.equals("Application/json")){
                    parseJSON(content);
                }else if(contentType.equals("Application/xml")){
                    parseXML(content);
                }else if(contentType.equals("text/csv")) {
                    parseCSV(content);
                }
                content.close();
            } else {
                Log.e(TAG, "Server responded with status code: " + statusLine.getStatusCode());
            }
        } catch(Exception ex) {
            Log.e(TAG, "Failed to send HTTP GET request due to: " + ex);
        }
    }


    public AggregatedDevice aggregateData(Device device){
        AggregatedDevice aggregatedDevice=new AggregatedDevice();
        aggregatedDevice.setDeviceId(device.getDeviceId());

        switch(device.getSensorType()){
            case 0: aggregatedDevice.setSensorType("Temperature");
                    aggregatedDevice.setSensorValue(device.getValue()+"°C");
                    break;
            case 1: aggregatedDevice.setSensorType("Humidity");
                    aggregatedDevice.setSensorValue(String.valueOf(device.getValue()));
                    break;
            case 2: aggregatedDevice.setSensorType("Motion");
                    aggregatedDevice.setSensorValue(String.valueOf(device.getValue()));
                    break;
            case 3: aggregatedDevice.setSensorType("Alien Presence");
                    if(device.getSensorType()==0){
                        aggregatedDevice.setSensorValue("No aliens detected");
                    } else {
                        aggregatedDevice.setSensorValue("Aliens detected");
                    }
                    break;
            case 4: aggregatedDevice.setSensorType("Dark Matter");
                    if(device.getSensorType()==50){
                        aggregatedDevice.setSensorValue("is CERN' particle accelerator turned off?");
                    } else {
                        aggregatedDevice.setSensorValue("Darkness");
                    }
                    break;
            default:aggregatedDevice.setSensorType("Unknown Device");
                    aggregatedDevice.setSensorValue(String.valueOf(device.getValue()));




        }


        return aggregatedDevice;
    }
}
