package com.example.geocache.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.geocache.R;
import com.example.geocache.databinding.ActivityMapsBinding;
import com.example.geocache.databinding.FragmentHomeBinding;
import com.example.geocache.databinding.FragmentSignUpBinding;
import com.example.geocache.models.Geocache;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment implements GoogleMap.OnMarkerClickListener {
    FragmentHomeBinding binding;
    NavController controller;
    private GoogleMap mMap;
    private ActivityMapsBinding mapsBinding;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private int geocacheCreatorId;
    private Integer maxDistInput;
    private Marker selectedMarker;
    public Polyline currentLine;
    private Location location;

    final Integer OPTION_ALL = R.id.allSelector;
    final Integer OPTION_OTHERS = R.id.othersSelector;
    final Integer OPTION_CURRENT_USER = R.id.meSelector;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        controller = NavHostFragment.findNavController(this);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            geocacheCreatorId = bundle.getInt("geocacheCreatorId");
            maxDistInput = bundle.getInt("maxDistInput");
        }

        ActivityResultLauncher<String> launcher =
                registerForActivityResult(
                        new ActivityResultContracts.RequestPermission(),
                        isGranted -> {
                            if (isGranted) {
                                setupMap();
                            }
                        }
                );
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            setupMap();
        } else {
            launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        checkUserIsAuthenticated();
        createLogoutButtonListener();
        createMyPlacesButtonListener();
        createSearchButtonListener();
        createTrackButtonListener();
        createStopTrackingButtonListener();
    }

    private void setupMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync((map) -> {
            mMap = map;
            mMap.setOnMarkerClickListener(this);
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
                fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        location = task.getResult();
                        CameraPosition pos = new CameraPosition.Builder()
                                .target(new LatLng(location.getLatitude(), location.getLongitude()))
                                .zoom(10)
                                .build();

                        mMap.animateCamera(
                                CameraUpdateFactory.newCameraPosition(pos),
                                500,
                                new GoogleMap.CancelableCallback() {
                                    @Override
                                    public void onFinish() {
                                    }
                                    @Override
                                    public void onCancel() {
                                    }
                                }

                        );
                    }
                });
                mMap.setMyLocationEnabled(true);
            }
        });
        findAndDisplayGeocaches();
    }

    private void checkUserIsAuthenticated() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            controller.navigate(R.id.action_homeFragment_to_signInFragment);
        }
    }

    private void createLogoutButtonListener() {
        binding.logoutButton.setOnClickListener((view) -> {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.signOut();
            controller.navigate(R.id.action_homeFragment_to_signInFragment);
        });
    }

    private void createMyPlacesButtonListener() {
        binding.myPlacesButton.setOnClickListener((view) -> {
            controller.navigate(R.id.action_homeFragment_to_myPlacesPageFragment);
        });
    }

    private void createSearchButtonListener() {
        binding.searchLocationsButton.setOnClickListener((view) -> {
            controller.navigate(R.id.action_homeFragment_to_searchFragment);
        });
    }

    private void createTrackButtonListener() {
        binding.trackButton.setOnClickListener((view) -> {
            if(selectedMarker != null) {
                binding.navigationBar.setVisibility(View.INVISIBLE);
                binding.stopTrackButton.setVisibility(View.VISIBLE);
                double distance = calculateDistanceBetweenTwoPoints(getUserLocation(), selectedMarker.getPosition());
                binding.distanceString.setText(String.format("%.2f miles", distance));

                currentLine = mMap.addPolyline(
                        new PolylineOptions()
                                .add(
                                        getUserLocation(),
                                        selectedMarker.getPosition()
                                )
                                .color(Color.RED)
                                .startCap(new RoundCap())
                                .endCap(new RoundCap())
                );
            }
        });
    }

    private void createStopTrackingButtonListener() {
        binding.stopTrackButton.setOnClickListener((view) -> {
            binding.navigationBar.setVisibility(View.VISIBLE);
                binding.stopTrackButton.setVisibility(View.INVISIBLE);
            binding.distanceString.setText("");

            if(currentLine != null) {
                currentLine.remove();
            }
        });
    }

    private void findAndDisplayGeocaches() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference geocacheCollectionReference = db.collection("geocaches");

        geocacheCollectionReference
                .get()
                .addOnCompleteListener((task) -> {
                    if(task.isSuccessful()) {
                        QuerySnapshot collection = task.getResult();
                        List<Geocache> geocaches = (List<Geocache>) collection.toObjects(Geocache.class);
                        displayGeocacheMarkers(geocaches);
                    }
                });
    }

    private void displayGeocacheMarkers(List<Geocache> geocaches) {
        for(Geocache geocache : geocaches) {
            LatLng latLng = new LatLng(geocache.getLatitude(), geocache.getLongitude());
            double distance = calculateDistanceFromUserToGeocache(latLng);
            double maxDist = (maxDistInput == null) ? 100000.0 : maxDistInput;

            if(distance <= maxDist && hasDesiredCreator(geocache)) {
                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("Coord: (" + geocache.getLatitude() + ", " + geocache.getLongitude() + ")")
                        .snippet(String.format("Dist: %.2f miles", distance))
                );
            }
        }
    }

    private boolean hasDesiredCreator(Geocache geocache) {
        boolean showGeocache = true;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(geocacheCreatorId == OPTION_OTHERS && geocache.getUserId().equals(user.getUid())) {
            showGeocache = false;
        } else if (geocacheCreatorId == OPTION_CURRENT_USER && !geocache.getUserId().equals(user.getUid())) {
            showGeocache = false;
        }

        return showGeocache;
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        if (!marker.isInfoWindowShown()) {
            marker.showInfoWindow();
            selectedMarker = marker;
        }
        return false;
    }

    private double calculateDistanceFromUserToGeocache(LatLng geocacheLatLng) {
        LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        return calculateDistanceBetweenTwoPoints(userLatLng, geocacheLatLng);
    }

    private LatLng getUserLocation() {
        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        String locationProvider = LocationManager.NETWORK_PROVIDER;
        @SuppressLint("MissingPermission") android.location.Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
        double currentLatitude = lastKnownLocation.getLatitude();
        double currentLongitude = lastKnownLocation.getLongitude();
        LatLng userLatLng = new LatLng(currentLatitude, currentLongitude);

        return userLatLng;
    }

    private double calculateDistanceBetweenTwoPoints(LatLng location1, LatLng location2) {
        float[] result = new float[1];
        Location.distanceBetween(
                location1.latitude,
                location1.longitude,
                location2.latitude,
                location2.longitude,
                result
        );
        double distanceMeters = result[0];
        return distanceMeters * .000621371;
    }
}