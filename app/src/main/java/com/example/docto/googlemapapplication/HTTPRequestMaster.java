package com.example.docto.googlemapapplication;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HTTPRequestMaster {

    private static HttpURLConnection connection;

    public static JSONObject getJSON(String url) throws JSONException{
        try {
            return getJSON(new URL(url));
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static JSONObject getJSON(URL url) throws JSONException {
        final StringBuilder stringBuilder = new StringBuilder();

        try {
            connection = (HttpURLConnection) url.openConnection();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }finally {
            connection.disconnect();
        }

        return new JSONObject(stringBuilder.toString());
    }
}
