package military.gunbam.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;

import java.lang.reflect.Member;
import java.util.Objects;

import military.gunbam.R;
import military.gunbam.utils.Util;
import military.gunbam.view.activity.BoardListActivity;
import military.gunbam.view.activity.LoginActivity;
import military.gunbam.view.activity.MemberInitActivity;
import military.gunbam.view.activity.PostActivity;
import military.gunbam.viewmodel.MemberInitViewModel;
import military.gunbam.viewmodel.UserViewModel;

public class MyPageFragment extends Fragment {
    private UserViewModel userViewModel;
    public MyPageFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_page, container, false);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.loadCurrentUser();
        userViewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {

            } else {
            }
        });
        // 회원정보 조회 버튼
        view.findViewById(R.id.viewProfileButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(CalculatorFragment.class);
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_calculator_main, new CalculatorFragment())
                        .addToBackStack(null)
                        .commit();


            }
        });

        // 내가 쓴 글 조회 버튼
        view.findViewById(R.id.viewPostsButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String publisher= "";
                String publisher = userViewModel.getCurrentUser().getValue().getUid();
                myStartActivity(BoardListActivity.class, "publisher", publisher);
            }

        });

        // 내가 쓴 댓글 조회 버튼
        view.findViewById(R.id.viewCommentsButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myStartActivity(BoardListActivity.class, "commentId",userViewModel.getCurrentUser().getValue().getUid());
                /*getFragmentManager().beginTransaction()
                        .replace(R.id.main_board_list_fragment, new PostActivity())
                        .addToBackStack(null)
                        .commit();*/
            }
        });

        // 회원정보 수정 버튼
        view.findViewById(R.id.editProfileButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myStartActivity(MemberInitActivity.class);
            }
        });

        // 회원 탈퇴 버튼
        view.findViewById(R.id.deleteAccountButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MemberInitViewModel viewModel;
                //viewModel.
                showWithdrawPopup();
            }
        });
        return view;
    }

    private void myStartActivity(Class c, String field, String fieldValue) {
        Intent intent = new Intent(getActivity(), c);
        intent.putExtra("field", field);
        intent.putExtra("fieldValue", fieldValue);
        startActivity(intent);
    }
    private void myStartActivity(Class c){
        Intent intent = new Intent(getActivity(), c);
        startActivity(intent);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 여기에 프래그먼트가 생성될 때 실행할 코드를 작성합니다.
    }
    private void showWithdrawPopup() {
        if (getActivity() != null) {
            View popupView = getLayoutInflater().inflate(R.layout.withdraw_popup, null);

            PopupWindow popupWindow = new PopupWindow(
                    popupView,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            Button btnConfirmWithdraw = popupView.findViewById(R.id.btn_confirm_withdraw);
            Button btnCancelWithdraw = popupView.findViewById(R.id.btn_cancel_withdraw);

            btnConfirmWithdraw.setOnClickListener(v -> {
                // 회원 탈퇴 로직 추가
                // ...

                // 팝업 닫기
                popupWindow.dismiss();
            });

            btnCancelWithdraw.setOnClickListener(v -> popupWindow.dismiss());

            popupWindow.showAtLocation(getView(), Gravity.CENTER, 0, 0);
        }
    }
}
