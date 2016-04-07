package se3a04.twentyonequestions.Interface;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.lang.reflect.Field;

import se3a04.twentyonequestions.MessagePassing.MapLocation;
import se3a04.twentyonequestions.R;

/**
 * GoogleMapsFragment
 *      Extends the fragment class to be able to
 */
public class GoogleMapsFragment extends Fragment implements OnMapReadyCallback {
    /**
     * Fields:
     *      mMap: Map of the location to display
     *      mMapView: View object holding mMap
     *      location: ADT holding the data about the location
     */
    private GoogleMap mMap;
    MapView mMapView;
    private MapLocation location;


    /**
     * onCreateView
     *      Method to be called when the view is created
     * @param inflater: object to turn XML into view
     * @param container: parent view holding child views
     * @param savedInstanceState: mapping from strings to instance types
     * @return: view to be put on the screen
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflate and return the layout

        View v = inflater.inflate(R.layout.blank, container,false);
        mMapView = (MapView) v.findViewById(R.id.map_frame);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();// needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(this);

        return v;
    }


    /**
     * onMapReady
     *      Method displays the map once it is rendered
     * @param googleMap: Map to display
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // create marker
        MarkerOptions marker = new MarkerOptions().position(
                new LatLng(location.getLatitude(), location.getLongitude()));

        // Changing marker icon
        marker.icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_ROSE));

        // adding marker
        googleMap.addMarker(marker);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(location.getZoom()).build();
        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

    }

    /**
     * setLocation
     *      sets the location to display on the screen
     * @param location: location to display
     */
    public void setLocation(MapLocation location){
        this.location = location;
    }


    /**
     * Methods to handle activity lifecycle
     */
    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

}

