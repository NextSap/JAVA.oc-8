package com.tourguide.app;

import com.tourguide.app.helper.InternalTestHelper;
import com.tourguide.app.object.NearbyAttractionResponse;
import com.tourguide.app.service.RewardsService;
import com.tourguide.app.service.TourGuideService;
import com.tourguide.app.object.User;
import gpsUtil.GpsUtil;
import gpsUtil.location.VisitedLocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rewardCentral.RewardCentral;
import tripPricer.Provider;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestTourGuideService {

	private TourGuideService tourGuideService;

	@BeforeEach
	public void setup() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardCentral rewardsCentral = new RewardCentral();
		RewardsService rewardsService = new RewardsService(gpsUtil, rewardsCentral);
		InternalTestHelper.setInternalUserNumber(0);
		tourGuideService = new TourGuideService(gpsUtil, rewardsService, rewardsCentral);
	}

	@Test
	public void getUserLocation() {
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);
		tourGuideService.tracker.stopTracking();
		assertEquals(visitedLocation.userId, user.getUserId());
	}

	@Test
	public void addUser() {
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

		tourGuideService.addUser(user);
		tourGuideService.addUser(user2);

		User retrivedUser = tourGuideService.getUser(user.getUserName());
		User retrivedUser2 = tourGuideService.getUser(user2.getUserName());

		tourGuideService.tracker.stopTracking();

		assertEquals(user, retrivedUser);
		assertEquals(user2, retrivedUser2);
	}

	@Test
	public void getAllUsers() {
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

		tourGuideService.addUser(user);
		tourGuideService.addUser(user2);

		List<User> allUsers = tourGuideService.getAllUsers();

		tourGuideService.tracker.stopTracking();

		assertTrue(allUsers.contains(user));
		assertTrue(allUsers.contains(user2));
	}

	@Test
	public void trackUser() {
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);

		tourGuideService.tracker.stopTracking();

		assertEquals(user.getUserId(), visitedLocation.userId);
	}

	@Test
	public void getNearbyAttractions() {
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);

		List<NearbyAttractionResponse> attractions = tourGuideService.getNearByAttractions(visitedLocation);

		tourGuideService.tracker.stopTracking();

		assertEquals(5, attractions.size());
	}

	public void getTripDeals() {
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

		List<Provider> providers = tourGuideService.getTripDeals(user);

		tourGuideService.tracker.stopTracking();

		assertEquals(10, providers.size());
	}
}