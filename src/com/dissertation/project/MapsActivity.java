package com.dissertation.project;

import android.os.Bundle;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

public class MapsActivity extends MapActivity {

	private MapView mapView;

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	/** Called when the activity is first created. */
   	@Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);

        mapView = (MapView) findViewById(R.id.map);
        mapView.setStreetView(true);
//        mapView.setBuiltInZoomControls(true);
        MapController mapController = mapView.getController();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
        	Double longtitude = extras.getDouble("longtitude");
        	Double latitude = extras.getDouble("latitude");
           	Double lon = -longtitude * 1E6;
        	Double lat = latitude * 1E6;
//        	Toast.makeText(this, "long "+lon, Toast.LENGTH_LONG).show();
//        	Toast.makeText(this, "lat "+lat, Toast.LENGTH_LONG).show();
        	GeoPoint point = new GeoPoint(lat.intValue(), lon.intValue());
        	mapController.setCenter(point);
        	mapController.animateTo(point);
        	mapController.setZoom(15);
        }
    }

}
