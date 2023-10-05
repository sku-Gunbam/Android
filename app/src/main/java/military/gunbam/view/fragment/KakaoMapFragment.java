package military.gunbam.view.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import military.gunbam.R;

public class KakaoMapFragment extends Fragment {
    public MapPOIItem seoul, gwangmyeong, iksan, gangneung, gyeongsan, gyeryong, gwangjuSongjeong, daejeon, dongSeoul, mokpo, busan, suseo, osong, yongsan, ulsan, jeonju, cheonanAsan;
    public MapPOIItem[] TMOItem = {seoul, gwangmyeong, iksan, gangneung, gyeongsan, gyeryong, gwangjuSongjeong, daejeon, dongSeoul, mokpo, busan, suseo, osong, yongsan, ulsan, jeonju, cheonanAsan};
    public MapPoint[] TMOPoint = {seoulTMO, gwangmyeongTMO, iksanTMO, gangneungTMO, gyeongsanTMO, gyeryongTMO, gwangjuSongjeongTMO, daejeonTMO, dongSeoulTMO, mokpoTMO, busanTMO, suseoTMO, osongTMO, yongsanTMO, ulsanTMO, jeonjuTMO, cheonanAsanTMO};
    public String[] TMOList = {"서울역TMO", "광명역TMO", "익산역TMO","강릉역TMO","경산역TMO","계룡역TMO","광주송정역TMO","대전역TMO","동서울터미널TMO", "목포역TMO", "부산역TMO", "수서역TMO", "오송역TMO", "용산역TMO", "울산역TMO", "전주역TMO", "천안아산역TMO"};
    public String[] TMOLink = {};

    private static final MapPoint seoulTMO = MapPoint.mapPointWithGeoCoord(37.55466802125894,  126.97058428720725);
    private static final MapPoint gwangmyeongTMO = MapPoint.mapPointWithGeoCoord(37.416684, 126.884773);
    private static final MapPoint iksanTMO = MapPoint.mapPointWithGeoCoord(35.94020117282588, 126.94628052719726);
    private static final MapPoint gangneungTMO = MapPoint.mapPointWithGeoCoord(37.76452010320812, 128.89961838704014);
    private static final MapPoint gyeongsanTMO = MapPoint.mapPointWithGeoCoord(35.819345366079524, 128.72793200200258);
    private static final MapPoint gyeryongTMO = MapPoint.mapPointWithGeoCoord(36.27298388683782, 127.26524500680374);
    private static final MapPoint gwangjuSongjeongTMO = MapPoint.mapPointWithGeoCoord(35.13766750304521, 126.79080284052891);
    private static final MapPoint daejeonTMO = MapPoint.mapPointWithGeoCoord(36.33229254636109, 127.43409430186232);
    private static final MapPoint dongSeoulTMO = MapPoint.mapPointWithGeoCoord(37.534603115718866, 127.09417926707233);
    private static final MapPoint mokpoTMO = MapPoint.mapPointWithGeoCoord(34.79116817722938, 126.38664843793505);
    private static final MapPoint busanTMO = MapPoint.mapPointWithGeoCoord(35.11510120462853, 129.04141130662916);
    private static final MapPoint suseoTMO = MapPoint.mapPointWithGeoCoord(37.48529151821853, 127.10446073777598);
    private static final MapPoint osongTMO = MapPoint.mapPointWithGeoCoord(36.620144690648765, 127.32748951585555);
    private static final MapPoint yongsanTMO = MapPoint.mapPointWithGeoCoord(37.53005833781064, 126.96480144328397);
    private static final MapPoint ulsanTMO = MapPoint.mapPointWithGeoCoord(35.55152180936191, 129.13864624772947);
    private static final MapPoint jeonjuTMO = MapPoint.mapPointWithGeoCoord(35.84983512351117, 127.1617856354875);
    private static final MapPoint cheonanAsanTMO = MapPoint.mapPointWithGeoCoord(36.7946033734687, 127.1043803249363);


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_kakao_map, container, false);

        //지도
        MapView mapView = new MapView(getActivity());
        ViewGroup mapViewContainer = (ViewGroup) v.findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);

        // 중심점 변경 - 예제 좌표는 서울 남산
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);

        Handler handler = new Handler();
        long delayMillis = 5000; // 5초 뒤에 실행하려면 5000을 사용합니다. (단위: 밀리초)

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);// 실행할 작업
            }
        }, delayMillis);

        //마커 찍기
        for (int i = 0; i < TMOList.length; i++) {
            add_marker(TMOItem[i], TMOList[i], TMOPoint[i], mapView);
        }

        return v;
    }

    private void add_marker(MapPOIItem marker, String name, MapPoint mp, MapView mapView){
        marker = new MapPOIItem();
        marker.setItemName(name);
        marker.setTag(0);
        marker.setMapPoint(mp);
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
        mapView.addPOIItem(marker);
    }
}