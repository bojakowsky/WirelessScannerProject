package com.example.brzydal.envscanner;

import android.app.ExpandableListActivity;
import android.os.AsyncTask;

import com.example.brzydal.envscanner.DataStructure.JSONSerializableAndroidData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import junit.framework.Test;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;

/**
 * Created by BRZYDAL on 4/19/2016.
 */
public class AsyncDataSend extends AsyncTask<Void, Void, Void>{

    public JSONSerializableAndroidData AndroidData;

    @Override
    protected Void doInBackground(Void... params) {
        try {
            //List<NameValuePair> args = new ArrayList<NameValuePair>();
            //args.add(new BasicNameValuePair("model", "Test123"));
            JSONHttpClient jsonHttpClient = new JSONHttpClient();
            //Model model = (Model) jsonHttpClient.PostParams("http://192.168.0.100:18608/api/Scanner", args, Model.class);
            //Model model = new Model();
            //model.model = "TEST";
            jsonHttpClient.PostObject("http://100.66.158.41:18608/api/Scanner", AndroidData, JSONSerializableAndroidData.class);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /*public class Model{
        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        private String model;
    }*/

}
//http://192.168.0.100:18608/
// 10.0.3.2
//10.0.2.2