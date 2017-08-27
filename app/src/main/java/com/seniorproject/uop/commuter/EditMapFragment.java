package com.seniorproject.uop.commuter;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class EditMapFragment extends Fragment implements OnMapReadyCallback {

    public static final String TAG = EditMapFragment.class.getSimpleName();

    private GoogleMap mMap;
    private List<Marker> originMarkers;
    private List<Marker> destinationMarkers;
    private List<Polyline> polylinePaths;
    private ProgressDialog progressDialog;
    private String originAddress;
    private String destinationAddress;
    private Alarm mAlarm;

    SupportPlaceAutocompleteFragment autocompleteFragmentOrigin;
    SupportPlaceAutocompleteFragment autocompleteFragmentDestination;

    private static View v;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (v != null) {
            ViewGroup parent = (ViewGroup) v.getParent();
            if (parent != null)
                parent.removeView(v);
        }
        try {
            v = inflater.inflate(R.layout.edit_map_fragment, container, false);
        } catch (InflateException e) {
         //map is already there, just return view as it is
        }

        SupportMapFragment mapFragment = new SupportMapFragment();
        getActivity().getSupportFragmentManager()
                .beginTransaction().add(R.id.map, mapFragment).commit();
        mapFragment.getMapAsync(this);

        autocompleteFragmentOrigin = new SupportPlaceAutocompleteFragment();
        getActivity().getSupportFragmentManager()
                .beginTransaction().add(R.id.origin_auto, autocompleteFragmentOrigin).commit();

        autocompleteFragmentDestination = new SupportPlaceAutocompleteFragment();
        getActivity().getSupportFragmentManager()
                .beginTransaction().add(R.id.dest_auto, autocompleteFragmentDestination).commit();

        //autocompleteFragmentOrigin.setHint("Starting address");
        //autocompleteFragmentDestination.setHint("Ending address");

        autocompleteFragmentOrigin.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.

                Log.i(TAG, "origin: " + place.getAddress());
                originAddress = String.valueOf(place.getAddress());
                Log.i(TAG, "PlaceSTRING: " + originAddress);
                mAlarm.setOrigin(originAddress);
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });
        autocompleteFragmentDestination.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                autocompleteFragmentDestination.setHint("Enter destination");
                // TODO: Get info about the selected place.
                Log.i(TAG, "destination: " + place.getAddress());
                destinationAddress = String.valueOf(place.getAddress());
                Log.i(TAG, "PlaceSTRING: " + destinationAddress);
                mAlarm.setDest(destinationAddress);
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });


        FloatingActionButton doneAction = (FloatingActionButton) v.findViewById(R.id.fab_map);
        doneAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
            }
        });

        return v;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    private void sendRequest() {
        String origin = originAddress;
        String destination = destinationAddress;
        if (origin == null) {
            Toast.makeText(getActivity(), "Enter origin address", Toast.LENGTH_SHORT).show();
            return;
        }
        if (destination == null) {
            Toast.makeText(getActivity(), "Enter destination address", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.i("locationInfo", origin); //ok
        Log.i("locationInfo", destination); //ok

        try {
            new DirectionFinder(new DirectionFinderListener() {
                @Override
                public void onDirectionFinderStart() {
                    onDirFinderStart();
                }

                @Override
                public void onDirectionFinderSuccess(List<Route> route) {
                    onDirFinderSuccess(route);
                }
            }, origin, destination).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void onDirFinderStart() {
        progressDialog = ProgressDialog.show(getActivity(), "Please wait.", "Looking up directions", true);

        if(originMarkers != null){
            for(Marker marker : originMarkers){
                marker.remove();
            }
        }
        if(destinationMarkers != null){
            for(Marker marker : destinationMarkers){
                marker.remove();
            }
        }
        if(polylinePaths != null){
            for(Polyline polyline : polylinePaths){
                polyline.remove();
            }
        }

    }

    public void onDirFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for(Route route : routes){
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));

            originMarkers.add(mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .title(route.startAddress)
                    .draggable(true)
                    .position(route.startLocation)));

            destinationMarkers.add(mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
                    .title(route.endAddress)
                    .draggable(true)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().geodesic(true).color(Color.BLUE).width(10);

            for(int i = 0; i < route.points.size(); i++){
                polylineOptions.add(route.points.get(i));
            }

            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }

        if (routes == null) {
            Log.i(TAG, "Routes are empty");
        } else if (routes.isEmpty()) {
            Log.i(TAG, "Routes are empty");
        } else {
            for (Route route : routes) {
                Log.i(TAG, String.format("%s", route.toString()));
            }
            mAlarm.setDur(routes.get(0).duration.value);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        MainActivity ma = (MainActivity) getActivity();
        ma.setAlarm(mAlarm);
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity ma = (MainActivity) getActivity();
        mAlarm = ma.getAlarm();
        Log.i(TAG, "Setting toolbar");
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Change Route");
    }
}
