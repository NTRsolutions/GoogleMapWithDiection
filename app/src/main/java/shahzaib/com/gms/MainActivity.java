package shahzaib.com.gms;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import cz.msebera.android.httpclient.entity.mime.Header;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,GoogleApiClient.ConnectionCallbacks, OnMapReadyCallback,LocationListener {

    protected GoogleApiClient googleApiClient;
    private String TAG = "GMS";
    private String TAG1 = "Location";
    private GoogleMap mMap;
    private LocationRequest request;
    private static final int REQUEST_CHECK_SETTINGS = 1000;
    private Location mLastLocation;
    private boolean mRequestingLocationUpdates;
    View mapView;
    private AutoCompleteTextView locationTextView,getLocationTextView;
    private boolean isCameraSet;
    private Polyline polylin;
   private LatLng lat,lat1;
    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG,"Pause Method Called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG,"Resume Method Called");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);

       mapFragment.getMapAsync(this);
        ActionBar ab = getSupportActionBar();
        ab.hide();
        getLocationTextView = (AutoCompleteTextView) findViewById(R.id.location_text1);
        locationTextView = (AutoCompleteTextView) findViewById(R.id.location_text);
        CheckMapPermission();

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG,"OnConnected Method Called");

    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        connectionResult.getErrorCode();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(googleApiClient.isConnected()) {

        }
        else {
            googleApiClient.disconnect();

        }
    }

    private void buidApiClient() {
        Log.i(TAG1,"buidApiClient Method Called");


            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .build();
            //mGoogleApiClient = new GoogleApiClient.Builder(this);



    }


    @Override
    protected void onStart() {
        super.onStart();
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if(resultCode != ConnectionResult.SUCCESS ) {
            googleApiAvailability.getErrorDialog(this,resultCode,1).show();
        }
        else {
            Log.i(TAG1,"Start Method Called");

            googleApiClient.connect();
            createLocationRequest();
        }




    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then over   riding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

    }


    @Override
    public void onLocationChanged(Location location) {

    }


    protected void createLocationRequest() {
        Log.i(TAG1,"createLoc Method Called");

        request = new LocationRequest();
        request.setSmallestDisplacement(10);
        request.setFastestInterval(50000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setNumUpdates(3);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(request);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient,
                        builder.build());


        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates states = result.getLocationSettingsStates();

                switch (status.getStatusCode()) {

                    case LocationSettingsStatusCodes.SUCCESS:
                        setInitialLocation();
                        break;

                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    MainActivity.this,
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;

                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.

                        break;

                }


            }
        });


    }


    private void CheckMapPermission() {
        Log.i(TAG1,"CheckPermission Method Called");
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {

            if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1002);
            }else {

                buidApiClient();
            }
        }
        else {
            buidApiClient();
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1002: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        buidApiClient();

                    }
                } else {

                    Toast.makeText(MainActivity.this,"Permission Denied",Toast.LENGTH_SHORT).show();
                    //finish();
                }
            }
            break;
        }
    }
    private void setInitialLocation() {
        Log.i(TAG1,"setIn Method Called");


        if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, request, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {


                mLastLocation = location;

                try {
                    LatLng positionUpdate = new LatLng(location.getLatitude(), location.getLongitude());
                    CameraUpdate update = CameraUpdateFactory.newLatLngZoom(positionUpdate, 15);
                    mMap.animateCamera(update);
                    GooglePlacesAutocompleteAdapter adapter = new  GooglePlacesAutocompleteAdapter(MainActivity.this, R.layout.autocompletelistitem);
                    locationTextView.setAdapter(adapter);
                    getLocationTextView.setAdapter(adapter);

                    locationTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                            InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            in.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
                            String str = (String) adapterView.getItemAtPosition(i);
                            String[] places = str.split("@");
                            String place_id = places[1];

                            locationTextView.setText("");
                            locationTextView.setHint(places[0]);
                            //getLatLng Method is not built-in method, find this method below
                            getLatLang(place_id);
                        }
                    });
                    getLocationTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                            InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            in.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
                            String str = (String) adapterView.getItemAtPosition(i);
                            String[] places = str.split("@");
                            String place_id = places[1];

                            getLocationTextView.setText("");
                            getLocationTextView.setHint(places[0]);
                            //getLatLng Method is not built-in method, find this method below
                            getLatLang1(place_id);
                        }
                    });


                        } catch (Exception ex) {

                    ex.printStackTrace();
                    Log.e("MapException", ex.getMessage());

                }

            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("onActivityResult()", Integer.toString(resultCode));

        //final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode)
        {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode)
                {
                    case Activity.RESULT_OK:
                    {

                        setInitialLocation();

                        Toast.makeText(MainActivity.this, "Location enabled by user!", Toast.LENGTH_LONG).show();
                        mRequestingLocationUpdates = true;
                        break;
                    }
                    case Activity.RESULT_CANCELED:
                    {
                        // The user was asked to change settings, but chose not to
                        Toast.makeText(MainActivity.this, "Location not enabled, user cancelled.", Toast.LENGTH_LONG).show();
                        mRequestingLocationUpdates = false;
                        break;
                    }
                    default:
                    {
                        break;
                    }
                }
                break;
        }
    }

    public void getLatLang1(String placeId) {
        Places.GeoDataApi.getPlaceById(googleApiClient, placeId)
                .setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        if (places.getStatus().isSuccess() && places.getCount() > 0) {
                            final Place place = places.get(0);
                            lat1 = place.getLatLng();
                            try {

                                if(locationTextView != null) {


                                    showDirection(lat1, lat);
                                }
                            }
                            catch (Exception ex) {

                                ex.printStackTrace();
                                Log.e("MapException",ex.getMessage());

                            }

                            Log.i("place", "Place found: " + place.getLatLng());
                        } else {
                            Log.e("place", "Place not found");
                        }
                        places.release();
                    }
                });
    }

    public void getLatLang(String placeId) {
        Places.GeoDataApi.getPlaceById(googleApiClient, placeId)
                .setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        if (places.getStatus().isSuccess() && places.getCount() > 0) {
                            final Place place = places.get(0);
                            lat  = place.getLatLng();
                            try {
                                if(getLocationTextView != null) {


                                    showDirection(lat1, lat);
                                }
                            }
                            catch (Exception ex) {

                                ex.printStackTrace();
                                Log.e("MapException",ex.getMessage());

                            }

                            Log.i("place", "Place found: " + place.getLatLng());
                        } else {
                            Log.e("place", "Place not found");
                        }
                        places.release();
                    }
                });
    }
    private void showDirection(final LatLng me, final LatLng dest) {
        final GMapV2Direction md = new GMapV2Direction();
        md.getDocument(me, dest, GMapV2Direction.MODE_DRIVING, new AsyncHttpResponseHandler() {


            @Override
            public void onStart() {
                //pd.show();
            }

            

            @Override
            public void onFinish() {
                //pd.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                try {

                    mMap.clear();
                    isCameraSet = false;
                   // addMarkers();

                    DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    InputSource is = new InputSource();
                    is.setCharacterStream(new StringReader(new String(responseBody)));

                    ArrayList<LatLng> directionPoint = md.getDirection(db.parse(is));
                    PolylineOptions rectLine = new PolylineOptions().width(12).color(
                            getResources().getColor(android.R.color.black));

                    for (int i = 0; i < directionPoint.size(); i++) {

                        rectLine.add(directionPoint.get(i));
                    }
                    if (polylin != null) {
                        polylin.remove();
                    }
                    polylin = mMap.addPolyline(rectLine);
                    polylin.setZIndex(100);
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(me);
                    builder.include(dest);
                    LatLngBounds bounds = builder.build();

                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,200);
                    mMap.animateCamera(cu, new GoogleMap.CancelableCallback(){
                        public void onCancel(){}
                        public   void onFinish(){
                            CameraUpdate zout = CameraUpdateFactory.zoomBy((float) -0.1);
                            mMap.animateCamera(zout);
                        }
                    });                    // zoomToLocation(dest);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }
    public void zoomRoute(GoogleMap googleMap, List<LatLng> lstLatLngRoute) {

        if (googleMap == null || lstLatLngRoute == null || lstLatLngRoute.isEmpty()) return;

        LatLngBounds currentLatLongBounds =
                googleMap.getProjection().getVisibleRegion().latLngBounds;
        boolean updateBounds = false;

        for (LatLng latLng : lstLatLngRoute) {
            if (!currentLatLongBounds.contains(latLng)) {
                updateBounds = true;
            }
        }

        if (updateBounds) {

            CameraUpdate cameraUpdate;

            if (lstLatLngRoute.size() == 1) {

                LatLng latLng = lstLatLngRoute.iterator().next();
                cameraUpdate = CameraUpdateFactory.newLatLng(latLng);

            } else {

                LatLngBounds.Builder builder = LatLngBounds.builder();
                for (LatLng latLng : lstLatLngRoute) {
                    builder.include(latLng);
                }
                LatLngBounds latLongBounds = builder.build();

                cameraUpdate =
                        CameraUpdateFactory.newLatLngBounds(latLongBounds, 90);

            }

            try {
                googleMap.animateCamera(cameraUpdate, 500,
                        new GoogleMap.CancelableCallback() {
                            @Override
                            public void onFinish() {
                            }

                            @Override
                            public void onCancel() {
                            }
                        });
            } catch (IllegalStateException ex) {
                // Ignore it. We're just being a bit lazy, as this exception only happens if
                // we try to animate the camera before the map has a size
            }
        }
    }

}
