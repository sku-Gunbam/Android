package military.gunbam.view.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import military.gunbam.R;
import military.gunbam.utils.Util;
import military.gunbam.view.fragment.BoardListFragment;
import military.gunbam.view.fragment.ChattingFragment;
import military.gunbam.view.fragment.HomeFragment;
import military.gunbam.view.fragment.KakaoMapFragment;
import military.gunbam.view.fragment.MyPageFragment;
import military.gunbam.viewmodel.MainViewModel;

public class MainActivity extends BasicActivity {
    // 마지막으로 뒤로 가기 버튼을 눌렀던 시간 저장
    private long backKeyPressedTime = 0;

    private MainViewModel mainViewModel;

    private static final int PERMISSION_REQUEST_CODE = 1000;

    private KakaoMapFragment kakaoMapFragment;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // ViewModel에서 사용자 로그인 상태와 회원 정보 여부를 관찰합니다.
        mainViewModel.getUserLoggedIn().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean userLoggedIn) {
                if (userLoggedIn) {
                    mainViewModel.getUserHasInfo().observe(MainActivity.this, new Observer<Boolean>() {
                        @Override
                        public void onChanged(Boolean userHasInfo) {
                            if (userHasInfo) {

                                // 사용자가 로그인하고 회원 정보가 있는 경우, 홈 화면으로 이동합니다.
                                // 초기 화면을 HomeFragment로 설정
                                getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.container, new HomeFragment())
                                        .commit();
                            } else {
                                // 사용자가 로그인하고 회원 정보가 없는 경우, MemberInitActivity로 이동합니다.
                                startActivity(MemberInitActivity.class);
                                finish();
                            }
                        }
                    });
                } else {
                    // 사용자가 로그인하지 않은 경우, LoginActivity로 이동합니다.
                    startActivity(LoginActivity.class);
                }
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                switch (item.getItemId()) {
                    case R.id.home:
                        selectedFragment = new HomeFragment();
                        break;
                    case R.id.postList:
                        selectedFragment = new BoardListFragment();
                        break;
                    case R.id.myPage:
                        // 예시: MyPage 섹션으로 이동하는 Fragment를 설정합니다.
                        selectedFragment = new MyPageFragment();
                        break;
                    case R.id.tmoSearch:
                        // 권한 체크
                        if (!checkPermissions()) {
                            // 권한이 없는 경우 권한 요청
                            requestPermissions();
                        } else {
                            // 권한이 있는 경우 해당 권한이 필요한 작업 수행
                            if (kakaoMapFragment == null) {
                                kakaoMapFragment = new KakaoMapFragment();
                            }
                            selectedFragment = kakaoMapFragment;
                        }
                        break;
                    case R.id.report:
                        selectedFragment = new ChattingFragment();
                        break;
                }

                if (selectedFragment != null) {
                    Util.replaceFragment(getSupportFragmentManager(), R.id.container, selectedFragment);
                }

                return true;
            }
        });
    }
    private boolean checkPermissions() {
        // 필요한 권한들을 체크하고 모든 권한이 부여되었는지 확인
        int internetPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);
        int fineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int coarseLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        return internetPermission == PackageManager.PERMISSION_GRANTED &&
                fineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                coarseLocationPermission == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        // 권한 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.INTERNET, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_REQUEST_CODE
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            // 권한에 대한 응답 처리
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 부여된 경우
                // 이곳에서 권한이 부여된 경우에 대한 작업을 수행합니다.
                Util.replaceFragment(getSupportFragmentManager(), R.id.container, new KakaoMapFragment());
            } else {
                // 권한이 거부된 경우
                // 이곳에서 권한이 거부된 경우에 대한 처리를 수행합니다.
                showToast(MainActivity.this, "권한을 허용해주세요.");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mainViewModel.checkUserLoginStatus();
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        // 기존 뒤로 가기 버튼의 기능을 막기 위해 주석 처리 또는 삭제

        // 마지막으로 뒤로 가기 버튼을 눌렀던 시간에 2.5초를 더해 현재 시간과 비교 후
        // 마지막으로 뒤로 가기 버튼을 눌렀던 시간이 2.5초가 지났으면 Toast 출력
        // 2500 milliseconds = 2.5 seconds
        if (System.currentTimeMillis() > backKeyPressedTime + 2500) {
            backKeyPressedTime = System.currentTimeMillis();
            showToast(MainActivity.this, "뒤로 가기 버튼을 한 번 더 누르시면 종료됩니다.");
            return;
        }
        // 마지막으로 뒤로 가기 버튼을 눌렀던 시간에 2.5초를 더해 현재 시간과 비교 후
        // 마지막으로 뒤로 가기 버튼을 눌렀던 시간이 2.5초가 지나지 않았으면 종료
        if (System.currentTimeMillis() <= backKeyPressedTime + 2500) {
            finish();
            showToast(MainActivity.this, "앱을 종료합니다.");
        }
    }
}