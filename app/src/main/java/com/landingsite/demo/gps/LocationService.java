package com.landingsite.demo.gps;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.List;

public class LocationService extends Service implements LocationListener {
    public final static String ACTION_DATA_READY = "com.landingsite.LOCAL_BROADCAST";

    private static final String TAG = LocationService.class.getSimpleName();
    private static final String CHANNEL_ID = "CHANNEL_ID";
    private static final String CHANNEL_NAME = "CHANNEL_NAME";

    private LocationManager locationManager;


    @SuppressLint("ForegroundServiceType")
    @Override
    public void onCreate() {
        super.onCreate();
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_MIN);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Title")
                .setContentText("Text")
                .build();
        startForeground(1, notification);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.w(TAG, "NO PERMISSION: " + Manifest.permission.ACCESS_FINE_LOCATION);
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.w(TAG, "NO PERMISSION: " + Manifest.permission.ACCESS_COARSE_LOCATION);
        } else {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            Log.i(TAG, "____ LocationManager:" + locationManager);

            if (locationManager != null) {
                List<String> providers = locationManager.getProviders(true);
                for (String provider : providers) {
                    Log.d(TAG, "____ provider: " + provider);
                }
            }
        }


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            Log.d(TAG, "Requesting LocationUpdates");
            locationManager.requestLocationUpdates(LocationManager.FUSED_PROVIDER, 3000L, 0.1F, this);
        } catch (SecurityException e) {
            Log.w(TAG, e);
        }
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        Log.d(TAG, "---> (Latitude: " + latitude + ", Longitude: " + longitude + ") <---");

        Intent intent = new Intent(ACTION_DATA_READY);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }

}
