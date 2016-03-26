package se3a04.twentyonequestions.MessagePassing;

/**
 * Created by curtis on 25/03/16.
 */
public class MapLocation {
    private int latitude =0;
    private int longitude= 0;
    private int zoom =10;

    public MapLocation(int latitude, int longitude, int zoom){
        this.latitude = latitude;
        this.longitude = longitude;
        this.zoom =zoom;
    }

    public int getLongitude() {
        return longitude;
    }

    public int getLatitude() {
        return latitude;
    }

    public int getZoom() {
        return zoom;
    }
}
