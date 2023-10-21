package military.gunbam.view.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.util.List;

import military.gunbam.R;
import military.gunbam.model.MapMarkerItem;
import military.gunbam.viewmodel.KakaoMapViewModel;

public class KakaoMapFragment extends Fragment {
    private KakaoMapViewModel kakaoMapViewModel;
    private MapView mapView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_kakao_map, container, false);

        mapView = new MapView(getActivity());
        ViewGroup mapViewContainer = v.findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);

        kakaoMapViewModel = new ViewModelProvider(this).get(KakaoMapViewModel.class);

        kakaoMapViewModel.getMapMarkerItems().observe(getViewLifecycleOwner(), mapMarkerItems -> {
            // 새로운 마커 아이템이 로드되었을 때 UI 업데이트 로직 추가
            updateMapMarkers(mapMarkerItems);
            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
            mapView.setZoomLevel(5, true);
        });

        kakaoMapViewModel.loadMapMarkers();

        return v;
    }

    private void updateMapMarkers(List<MapMarkerItem> mapMarkerItems) {
        mapView.removeAllPOIItems();

        for (MapMarkerItem markerItem : mapMarkerItems) {
            addMarkerToMap(markerItem, mapView);
        }
    }

    private void addMarkerToMap(MapMarkerItem markerItem, MapView mapView) {
        MapPOIItem marker = new MapPOIItem();
        marker.setItemName(markerItem.getName());
        marker.setTag(0);
        marker.setMapPoint(markerItem.getMapPoint());
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin);
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
        mapView.addPOIItem(marker);
    }
}