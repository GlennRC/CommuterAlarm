package com.seniorproject.uop.commuter;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;


/**
 * Created by GlennRC on 5/5/16.
 */
public class Route {
    public Distance distance;
    public Duration duration;
    public String endAddress;
    public LatLng endLocation;
    public String startAddress;
    public LatLng startLocation;
    public List<LatLng> points;
}
