package com.example.docto.googlemapapplication;

import android.content.Context;
import android.location.Location;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class NearestFinder {

    private final Context context;

    private List<RestPlace> restPlaces = new ArrayList<>();

    public NearestFinder(Context context) {
       this.context = context;
    }

    public void find(Location location, int radius, String type) {

        DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        df.setMaximumFractionDigits(6);

        final String requestUrl = String.format(context.getString(R.string.google_search_url) + "json?location=%s,%s&radius=%d&type=%s&key=%s",
                df.format(location.getLatitude()), df.format(location.getLongitude()), radius, type, context.getString(R.string.api_key));

        try {
            final JSONObject jsonObject = HTTPRequestMaster.getJSON(requestUrl);
            if (jsonObject == null) return;

            String status = jsonObject.getString("status");

            if (!status.equals("OK")) return;

            JSONArray jsonArray = jsonObject.getJSONArray("results");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonElement = jsonArray.getJSONObject(i);

                final RestPlace restPlace = new RestPlace(jsonElement.getString("name"));

                JSONObject locationParams = jsonElement.getJSONObject("geometry").getJSONObject("location");

                final Location placeLocation = new Location("location");
                placeLocation.setLatitude(locationParams.getDouble("lat"));
                placeLocation.setLongitude(locationParams.getDouble("lng"));

                restPlace.setLocation(placeLocation);

                JSONObject openingParams = jsonElement.getJSONObject("opening_hours");

                restPlace.setOpened(openingParams.getBoolean("open_now"));
                restPlace.setRating((float)jsonElement.getDouble("rating"));

                restPlaces.add(restPlace);
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    public List<RestPlace> getResult() {
        return restPlaces;
    }
}
