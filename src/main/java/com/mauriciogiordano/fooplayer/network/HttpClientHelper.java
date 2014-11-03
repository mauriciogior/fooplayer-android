package com.mauriciogiordano.fooplayer.network;

import android.content.Context;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class HttpClientHelper
{
    public static Integer PORT 	= 80;
    public static String METHOD	= "http";

    private URI uri = null;
    private String url;
    private String path;

    private static DefaultHttpClient httpClient = null;

    private List<NameValuePair> dataPost = new ArrayList<NameValuePair>();
    private List<NameValuePair> dataGet  = new ArrayList<NameValuePair>();

    public HttpClientHelper(String url, String path, Context context)
    {
        this.url = url;
        this.path = path;

        if(httpClient == null)
        {
            httpClient = new DefaultHttpClient();
        }
    }

    public void setSSL()
    {
        PORT = 443;
        METHOD = "https";
    }

    public void addParamForGet(String key, String value)
    {
        dataGet.add(new BasicNameValuePair(key, value));
    }

    public void addParamForPost(String key, String value)
    {
        dataPost.add(new BasicNameValuePair(key, value));
    }

    public HttpResponse executePost()
    {
        HttpResponse response = null;

        try {
            uri = URIUtils.createURI(METHOD, url, PORT, path,
                    dataGet == null ? null : URLEncodedUtils.format(dataGet, "UTF-8"), null);

            HttpPost httpPost = new HttpPost(uri);

            httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");

            httpPost.setEntity(new UrlEncodedFormEntity(dataPost, HTTP.UTF_8));

            response = httpClient.execute(httpPost);

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (URISyntaxException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        return response;
    }

    public HttpResponse executeGet()
    {
        try {
            uri = URIUtils.createURI(METHOD, url, PORT, path,
                    dataGet == null ? null : URLEncodedUtils.format(dataGet, "UTF-8"), null);
        } catch (URISyntaxException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        HttpGet httpget = new HttpGet(uri);

        HttpResponse response = null;

        try {
            response = httpClient.execute(httpget);
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return response;
    }

    public DefaultHttpClient getHttpClient() { return httpClient; }

    public String getURI()
    {
        return uri.toString();
    }

    public List<NameValuePair> getParams()
    {
        return dataGet;
    }

    public List<NameValuePair> postParams()
    {
        return dataPost;
    }
}