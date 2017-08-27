package com.seniorproject.uop.commuter;

import java.util.List;

/**
 * Created by GlennRC on 5/5/16.
 */
public interface DirectionFinderListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> route);
}

