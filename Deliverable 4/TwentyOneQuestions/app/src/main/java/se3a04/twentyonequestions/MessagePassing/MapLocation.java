package se3a04.twentyonequestions.MessagePassing;

/**
 * MapLocation
 *      ADT holding information about location to display on map
 */
public class MapLocation {
    private double latitude = 0;
    private double longitude = 0;
    private int zoom = 10;

    public MapLocation(double latitude, double longitude, int zoom){
        this.latitude = latitude;
        this.longitude = longitude;
        this.zoom = zoom;
    }

    public double getLongitude() {
        return longitude;
    }
    public double getLatitude() {
        return latitude;
    }
    public int getZoom() {
        return zoom;
    }
}
