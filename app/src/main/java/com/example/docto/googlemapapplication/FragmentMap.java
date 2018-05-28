package com.example.docto.googlemapapplication;

import android.Manifest;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.google.android.gms.maps.*;
import com.google.android.gms.location.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.M)
public class FragmentMap extends Fragment implements OnMapReadyCallback {

    private static final String[] locationPermissions;

    private GoogleMap googleMap;
    private MapView mapView;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private NearestFinder nearestFinder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nearestFinder = new NearestFinder(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_map, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = view.findViewById(R.id.map);
        if (mapView != null) {
            mapView.onCreate(savedInstanceState);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(this.getContext());
        this.googleMap = googleMap;
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            getActivity().requestPermissions(new String[] { Manifest.permission.INTERNET }, 0);
        }

        final int locationGrants = ActivityCompat.checkSelfPermission(getContext(), locationPermissions[0]) &
                ActivityCompat.checkSelfPermission(getContext(), locationPermissions[1]);
        if (locationGrants != PackageManager.PERMISSION_GRANTED) {
            getActivity().requestPermissions(locationPermissions, 1);
        }

        googleMap.setMyLocationEnabled(true);

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
                nearestFinder.find(location, 10000, "cafe");

                markFounded();
            }
        });
    }

    private void markFounded() {
        List<RestPlace> restPlaces = nearestFinder.getResult();

        if (restPlaces.isEmpty()) {
            Toast.makeText(getContext(), "No cafe founded in this region", Toast.LENGTH_LONG).show();
            return;
        }

        for (RestPlace restPlace : restPlaces) {
            final LatLng position = new LatLng(restPlace.getLocation().getLatitude(), restPlace.getLocation().getLongitude());

            googleMap.addMarker(new MarkerOptions()
                    .position(position)
                    .title(restPlace.getName()));
        }
    }

    static {
        locationPermissions = new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };
    }
}
