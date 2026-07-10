package com.example.kucingputeh.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

/**
 * Small helper around launching Google Maps to show a ride's pickup (origin)
 * and destination.
 *
 * Origin/Destination in this app are plain address/place-name strings (not
 * lat/lng), so instead of embedding the Maps SDK (which needs an API key and
 * billing set up) we simply hand the addresses off to the Google Maps app
 * (or a browser fallback) using its public "dir" URL scheme. Google Maps
 * geocodes the text on its own side and draws the route between the two
 * points.
 */
public final class MapUtils {

    private MapUtils() {
        // no instances
    }

    /**
     * Opens Google Maps showing directions between {@code origin} and
     * {@code destination}. Falls back to any browser that can handle a
     * regular https Google Maps link if the Google Maps app isn't installed.
     */
    public static void openRouteOnMap(Context context, String origin, String destination) {
        if (context == null) return;

        if (isBlank(origin) || isBlank(destination)) {
            Toast.makeText(context, "Pickup or destination is missing for this ride.", Toast.LENGTH_SHORT).show();
            return;
        }

        String uriString = "https://www.google.com/maps/dir/?api=1"
                + "&origin=" + Uri.encode(origin)
                + "&destination=" + Uri.encode(destination)
                + "&travelmode=driving";

        Uri mapUri = Uri.parse(uriString);
        Intent intent = new Intent(Intent.ACTION_VIEW, mapUri);
        // Prefer opening directly in the Google Maps app when it's installed
        intent.setPackage("com.google.android.apps.maps");

        try {
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
            } else {
                // Google Maps app not installed - fall back to opening the link
                // in a browser instead.
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, mapUri);
                context.startActivity(browserIntent);
            }
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "No app found to display the map.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Opens Google Maps centered on a single address (e.g. just the pickup
     * point on its own).
     */
    public static void openSingleLocationOnMap(Context context, String address, String label) {
        if (context == null) return;

        if (isBlank(address)) {
            Toast.makeText(context, "No location available to show on the map.", Toast.LENGTH_SHORT).show();
            return;
        }

        String query = isBlank(label) ? address : address + "(" + label + ")";
        Uri geoUri = Uri.parse("geo:0,0?q=" + Uri.encode(query));
        Intent intent = new Intent(Intent.ACTION_VIEW, geoUri);
        intent.setPackage("com.google.android.apps.maps");

        try {
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
            } else {
                Uri webUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=" + Uri.encode(address));
                context.startActivity(new Intent(Intent.ACTION_VIEW, webUri));
            }
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "No app found to display the map.", Toast.LENGTH_SHORT).show();
        }
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}