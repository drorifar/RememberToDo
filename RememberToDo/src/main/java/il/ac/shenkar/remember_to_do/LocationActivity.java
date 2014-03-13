package il.ac.shenkar.remember_to_do;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ActivityInfo;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

/**
 * This class manage giofance in the app
 * @author Dror Afargan & Ran Nahmijas
 */
public class LocationActivity extends FragmentActivity implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener{

    private final static DecimalFormat DF = new DecimalFormat("#.##");
    private final static int
            CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private EditText mLocationIn;
    private TextView mLocationOut;
    private GoogleMap mGoogleMap = null;
    private Marker marker = null;

    private LocationClient mLocationClient;
    private Geocoder mGeoCoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_picker_layout);

        //lock the screen in portrait orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //gets details from createTaskActivity
        Intent receiveDataIntent = getIntent();
        Bundle receivedDataBundle = receiveDataIntent.getExtras();

        //bind to layout
        mLocationIn = (EditText) findViewById(R.id.location_input);
        mLocationOut = (TextView) findViewById(R.id.location_output);
        mGeoCoder = new Geocoder(this);

         /*
         * Create a new location client, using the enclosing class to
         * handle callbacks.
         */
        mLocationClient = new LocationClient(this, this, this);

        //initialize the map object
        SupportMapFragment supportMapFragment =
                (SupportMapFragment)getSupportFragmentManager().findFragmentByTag("mapFragment");

        mGoogleMap = supportMapFragment.getMap();
        if (mGoogleMap!=null) {
            //map available
            mGoogleMap.setMyLocationEnabled(true);
        }

        mLocationIn.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                int i = EditorInfo.IME_ACTION_DONE;
                lookUp(mLocationIn.getText().toString());
                return false;
            }
        });

        final Button setLocation = (Button) findViewById(R.id.set_location);
        setLocation.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Bundle taskDetailsBundle = new Bundle();
                taskDetailsBundle.putString("location", mLocationIn.getText().toString());
                intent.putExtras(taskDetailsBundle);
                setResult(RESULT_OK, intent);
                finish();
            }
        }
        );

        final Button clearLocation = (Button)findViewById(R.id.clear);
        clearLocation.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
            @Override
            public void onClick(View v) {
                mLocationIn.setText("");
                setLocation.callOnClick();
            }
        });

        final ImageView findMe = (ImageView) findViewById(R.id.myLocation);
        findMe.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                Location location = mLocationClient.getLastLocation();
                if (location==null){
                    Toast.makeText(getBaseContext(), "No last known location", Toast.LENGTH_SHORT).show();
                    return;
                }
                MyLocationAsyncTaskRunner runner = new MyLocationAsyncTaskRunner();
                runner.execute(location);
            }
        }
        );

        //checks if its a new ic_task_board or existing ic_task_board
        if (receivedDataBundle != null)  {
            if (receivedDataBundle.getString("location") != null && !receivedDataBundle.getString("location").isEmpty()) {
                mLocationIn.setText(receivedDataBundle.getString("location"));
                lookUp(mLocationIn.getText().toString());
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Connect the client.
        mLocationClient.connect();
    }

    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        mLocationClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        // Display the connection status
        Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDisconnected() {
        Toast.makeText(this, "Disconnected. Connection lost.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        try {
            // Start an Activity that tries to resolve the error
            connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    /**
     * Lookup the address in input, format an output string and update map if possible
     */
    private void lookUp(String addressString) {
        String out;
        LookUpAsyncTaskRunner runner = new LookUpAsyncTaskRunner();
        runner.execute(addressString);
    }

    /**
     * Display a marker on the map and reposition the camera according to location
     * @param latLng
     */
    private void updateMap(LatLng latLng){
        if (mGoogleMap==null){
            return; //no play services
        }

        if (marker!=null){
            marker.remove();
        }

        marker = mGoogleMap.addMarker(new MarkerOptions().position(latLng));

        //reposition camera
        CameraPosition newPosition = new CameraPosition.Builder()
                .target(latLng)
                .zoom(15)
                .build();
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(newPosition));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case CONNECTION_FAILURE_RESOLUTION_REQUEST:
                switch (resultCode){
                    case RESULT_OK:
                        mLocationClient.connect();
                        break;
                    default:
                        Toast.makeText(this, "There is problem connecting to geoLocation", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     * private class for async search my location
     */
    private class MyLocationAsyncTaskRunner extends AsyncTask<Location, Void, String> {

        private String resp;

        @Override
        protected String doInBackground(Location... params) {
            try {
                Location location = params[0];
                           List<Address> addresses = mGeoCoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (addresses.size()>0){
                   resp = addresses.get(0).getAddressLine(0);
                }
                else resp = "No geocoder results";
            } catch (IOException e) {
                e.printStackTrace();
                resp = "No geocoder";
            } catch (Exception e) {
                e.printStackTrace();
                resp = "No geocoder";
            }
            return resp;
        }

        @Override
        protected void onPostExecute(String result) {
            mLocationIn.setText(result);
            if (!result.contains("No geocoder") && mLocationIn.getText() != null){
                lookUp(mLocationIn.getText().toString());
            }
        }
    }

    /**
     * private class for lookup the location in the map
     */
    private class LookUpAsyncTaskRunner extends AsyncTask<String, Void, List<Address>> {
        @Override
        protected List<Address> doInBackground(String... params) {
            try {
                String addressString = params[0];
                return mGeoCoder.getFromLocationName(addressString, 1);
            }
            catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute( List<Address> addresses) {
            String out;
            try
            {
                if (addresses != null && addresses.size() >= 1) {
                    Address address = addresses.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    out = address.getAddressLine(0) + " ("
                            + DF.format(address.getLatitude()) + " , "
                            + DF.format(address.getLongitude()) + ")";
                    updateMap(latLng);
                } else {
                    out = "Not found";
                }
            } catch (Exception e) {
                out = "Not available";
            }
            mLocationOut.setText(out);
        }
    }
}

