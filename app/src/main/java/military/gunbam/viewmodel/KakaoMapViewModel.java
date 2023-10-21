package military.gunbam.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;

import java.util.ArrayList;
import java.util.List;

import military.gunbam.model.MapMarkerItem;

public class KakaoMapViewModel extends ViewModel {
    private MutableLiveData<List<MapMarkerItem>> mapMarkerItems = new MutableLiveData<>();

    public LiveData<List<MapMarkerItem>> getMapMarkerItems() {
        return mapMarkerItems;
    }

    public void loadMapMarkers() {
        List<MapMarkerItem> markerItems = new ArrayList<>();

        // 여기에 마커 아이템 생성 및 추가 로직 추가
        markerItems.add(new MapMarkerItem("서울역TMO", MapPoint.mapPointWithGeoCoord(37.55466802125894, 126.97058428720725)));
        markerItems.add(new MapMarkerItem("광명역TMO", MapPoint.mapPointWithGeoCoord(37.416684, 126.884773)));
        markerItems.add(new MapMarkerItem("익산역TMO", MapPoint.mapPointWithGeoCoord(35.94020117282588, 126.94628052719726)));
        markerItems.add(new MapMarkerItem("강릉역TMO", MapPoint.mapPointWithGeoCoord(37.76452010320812, 128.89961838704014)));
        markerItems.add(new MapMarkerItem("경산역TMO", MapPoint.mapPointWithGeoCoord(35.819345366079524, 128.72793200200258)));
        markerItems.add(new MapMarkerItem("계룡역TMO", MapPoint.mapPointWithGeoCoord(36.27298388683782, 127.26524500680374)));
        markerItems.add(new MapMarkerItem("광주송정역TMO", MapPoint.mapPointWithGeoCoord(35.13766750304521, 126.79080284052891)));
        markerItems.add(new MapMarkerItem("대전역TMO", MapPoint.mapPointWithGeoCoord(36.33229254636109, 127.43409430186232)));
        markerItems.add(new MapMarkerItem("동서울터미널TMO", MapPoint.mapPointWithGeoCoord(37.534603115718866, 127.09417926707233)));
        markerItems.add(new MapMarkerItem("목포역TMO", MapPoint.mapPointWithGeoCoord(34.79116817722938, 126.38664843793505)));
        markerItems.add(new MapMarkerItem("부산역TMO", MapPoint.mapPointWithGeoCoord(35.11510120462853, 129.04141130662916)));
        markerItems.add(new MapMarkerItem("수서역TMO", MapPoint.mapPointWithGeoCoord(37.48529151821853, 127.10446073777598)));
        markerItems.add(new MapMarkerItem("오송역TMO", MapPoint.mapPointWithGeoCoord(36.620144690648765, 127.32748951585555)));
        markerItems.add(new MapMarkerItem("용산역TMO", MapPoint.mapPointWithGeoCoord(37.53005833781064, 126.96480144328397)));
        markerItems.add(new MapMarkerItem("울산역TMO", MapPoint.mapPointWithGeoCoord(35.55152180936191, 129.13864624772947)));
        markerItems.add(new MapMarkerItem("전주역TMO", MapPoint.mapPointWithGeoCoord(35.84983512351117, 127.1617856354875)));
        markerItems.add(new MapMarkerItem("천안아산역TMO", MapPoint.mapPointWithGeoCoord(36.7946033734687, 127.1043803249363)));

        mapMarkerItems.setValue(markerItems);
    }
}