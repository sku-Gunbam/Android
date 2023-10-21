package military.gunbam.model;

import net.daum.mf.map.api.MapPoint;

public class MapMarkerItem {
    private String name;
    private MapPoint mapPoint;

    public MapMarkerItem(String name, MapPoint mapPoint) {
        this.name = name;
        this.mapPoint = mapPoint;
    }

    public String getName() {
        return name;
    }

    public MapPoint getMapPoint() {
        return mapPoint;
    }
}
