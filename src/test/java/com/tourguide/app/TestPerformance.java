package com.tourguide.app;

import com.tourguide.app.helper.InternalTestHelper;
import com.tourguide.app.object.User;
import com.tourguide.app.service.RewardsService;
import com.tourguide.app.service.TourGuideService;
import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import rewardCentral.RewardCentral;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestPerformance {

    /*
     * A note on performance improvements:
     *
     * The number of users generated for the high volume tests can be easily
     * adjusted via this method:
     *
     * InternalTestHelper.setInternalUserNumber(100000);
     *
     *
     * These tests can be modified to suit new solutions, just as long as the
     * performance metrics at the end of the tests remains consistent.
     *
     * These are performance metrics that we are trying to hit:
     *
     * highVolumeTrackLocation: 100,000 users within 15 minutes:
     * assertTrue(TimeUnit.MINUTES.toSeconds(15) >=
     * TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
     *
     * highVolumeGetRewards: 100,000 users within 20 minutes:
     * assertTrue(TimeUnit.MINUTES.toSeconds(20) >=
     * TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
     */

    @BeforeEach
    public void setUp() {

    }

    @Disabled
    @Test // 100000 = 200s environ
    public void highVolumeTrackLocation() {
        GpsUtil gpsUtil = new GpsUtil();
        RewardCentral rewardCentral = new RewardCentral();
        RewardsService rewardsService = new RewardsService(gpsUtil, rewardCentral);
        // Users should be incremented up to 100,000, and test finishes within 15
        // minutes
        InternalTestHelper.setInternalUserNumber(1000);
        TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService, rewardCentral);

        List<User> allUsers = tourGuideService.getAllUsers();

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        List<CompletableFuture<VisitedLocation>> completableFutures = allUsers.stream().map(tourGuideService::trackUserLocationAsync).toList();

        CompletableFuture.allOf(completableFutures.toArray(CompletableFuture[]::new)).join();

        stopWatch.stop();
        tourGuideService.tracker.stopTracking();

        System.out.println("highVolumeTrackLocation: Time Elapsed: "
                + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
        assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
    }

    @Disabled
    @Test // 100000 = 400s environ
    public void highVolumeGetRewards() {
        GpsUtil gpsUtil = new GpsUtil();
        RewardCentral rewardCentral = new RewardCentral();
        RewardsService rewardsService = new RewardsService(gpsUtil, rewardCentral);

        // Users should be incremented up to 100,000, and test finishes within 20
        // minutes
        InternalTestHelper.setInternalUserNumber(100000);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService, rewardCentral);

        Attraction attraction = gpsUtil.getAttractions().get(0);
        List<User> allUsers = tourGuideService.getAllUsers();
        allUsers.forEach(u -> u.addToVisitedLocations(new VisitedLocation(u.getUserId(), attraction, new Date())));

       // rewardsService.calculateRewardsForAllUsers(allUsers);

        List<CompletableFuture<Void>> completableFutures = allUsers.stream().map(rewardsService::calculateRewardsAsync).toList();

        CompletableFuture.allOf(completableFutures.toArray(CompletableFuture[]::new)).join();

        for (User user : allUsers) {
            assertTrue(user.getUserRewards().size() > 0);
        }

        stopWatch.stop();
        tourGuideService.tracker.stopTracking();

        System.out.println("highVolumeGetRewards: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime())
                + " seconds.");
        assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
    }
}