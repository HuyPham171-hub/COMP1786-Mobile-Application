package com.example.yogaadminapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

public class NetworkUtil {
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network network = cm.getActiveNetwork();
            if (network == null) {
                Log.d("NetworkUtil", "No active network");
                return false;
            }
            NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
            boolean hasConnection = capabilities != null &&
                    (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
            Log.d("NetworkUtil", "Connection status: " + hasConnection);
            return hasConnection;
        } else {
            NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
            boolean hasConnection = activeNetworkInfo != null && activeNetworkInfo.isConnected();
            Log.d("NetworkUtil", "Legacy connection status: " + hasConnection);
            return hasConnection;
        }
    }
}

