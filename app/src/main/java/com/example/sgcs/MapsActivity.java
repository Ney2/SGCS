package com.example.sgcs;

import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    public static final String url = "http://192.168.43.110/androidphpmysql/notification.php";
    private JSONArray result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // This is how you can draw the shortest path between two points
        LatLng origin = new LatLng(8.960662740046882, 38.77280735505152);
        LatLng destination = new LatLng(8.976831653490418, 38.74334306685664);
        LatLng[] waypoints = new LatLng[]{
                new LatLng(8.95864157525013, 38.76779433379614),
                new LatLng(8.968242007887543, 38.76104210108481),
                new LatLng(8.951163167790439, 38.759098276516404)
        };
        drawShortestPath(origin, destination, waypoints);

        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{

                            JSONObject jsonObject = new JSONObject(response);
                            String sucess = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("data");

                            if(sucess.equals("1")){


                                for(int i=0;i<jsonArray.length();i++){

                                    JSONObject object = jsonArray.getJSONObject(i);

                                    String id = object.getString("id");
                                    String latitude = object.getString("latitude");
                                    String longitude = object.getString("longitude");
                                    String level = object.getString("level");

                                    LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));

                                    mMap.addMarker(new MarkerOptions()
                                            .position(latLng)
                                            .title(level)
                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))
                                    );

                                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                                }
                            }
                        }
                        catch (JSONException e){
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MapsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);

    }

    private void drawShortestPath(LatLng origin, LatLng destination, LatLng[] waypoints) {
        try {
            // Get shortest path from api
            DirectionsResult results = getDirectionsResult(origin, destination, waypoints);
            List<LatLng> decodedPath = PolyUtil.decode(results.routes[0].overviewPolyline.getEncodedPath());
            // Draw the path with polyline
            mMap.addPolyline(new PolylineOptions().addAll(decodedPath)).setColor(Color.BLUE);
            // Zoom camera to origin
            mMap.animateCamera(CameraUpdateFactory.newLatLng(origin));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(origin, 14));
            // Set markers
            mMap.addMarker(new MarkerOptions()
                    .position(origin)
                    .title("Origin")
            );
            mMap.addMarker(new MarkerOptions()
                    .position(destination)
                    .title("Destination")
            );

        } catch (ApiException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private DirectionsResult getDirectionsResult(LatLng origin, LatLng destination, LatLng[] waypoints) throws ApiException, InterruptedException, IOException {
        DirectionsApiRequest request = DirectionsApi.newRequest(getGeoContext())
                .mode(TravelMode.DRIVING)
                .origin(new com.google.maps.model.LatLng(origin.latitude, origin.longitude))
                .destination(new com.google.maps.model.LatLng(destination.latitude, destination.longitude));
        if (waypoints != null) {
            List<com.google.maps.model.LatLng> _waypoints = Arrays.stream(waypoints).map(waypoint ->
                    new com.google.maps.model.LatLng(waypoint.latitude, waypoint.longitude)
            ).collect(Collectors.toList());
            request.waypoints(_waypoints.toArray(new com.google.maps.model.LatLng[0]));

        }
        return request.await();
    }

    private GeoApiContext getGeoContext() {
        String apiKey = getString(R.string.directions_api_key);
        return new GeoApiContext.Builder()
                .apiKey(apiKey)
                .build();
    }



    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }
}