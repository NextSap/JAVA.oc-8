package com.tourguide.app.object;

import gpsUtil.location.Location;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NearbyAttractionResponse {
    private String attractionName;
    private Location attractionLocation;
    private Location userLocation;
    private double distance;
    private int rewardPoints;
}
