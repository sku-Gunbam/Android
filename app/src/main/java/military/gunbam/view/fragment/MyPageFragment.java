package military.gunbam.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import military.gunbam.R;
import military.gunbam.model.member.MemberInfo;
import military.gunbam.view.activity.BoardListActivity;
import military.gunbam.view.activity.MemberInitActivity;
import military.gunbam.viewmodel.CommentListViewModel;
import military.gunbam.viewmodel.MemberInitViewModel;
import military.gunbam.viewmodel.PostViewModel;
import military.gunbam.viewmodel.UserViewModel;

public class MyPageFragment extends Fragment {
    private MemberInitViewModel memberInitViewModel;

    private UserViewModel userViewModel;
    private MemberInfo memberInfo;
    public MyPageFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_page, container, false);

        memberInitViewModel = new ViewModelProvider(this).get(MemberInitViewModel.class);
        memberInitViewModel.loadMemberInfo();
        memberInitViewModel.getMemberInfo().observe(getViewLifecycleOwner(), member->{
            memberInfo = member;
        });
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

                showMemberInfoPopup(memberInfo);
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
                //댓글 작성시 parentCommentId 값을 입력되도록 수정해야함.
                String commentAuthor = userViewModel.getCurrentUser().getValue().getUid();
                myStartActivity(BoardListActivity.class, "commentAuthor", commentAuthor);

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
            View popupView = getLayoutInflater().inflate(R.layout.popup_withdraw, null);

            PopupWindow popupWindow = new PopupWindow(
                    popupView,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            Button btnConfirmWithdraw = popupView.findViewById(R.id.btn_confirm_withdraw);
            Button btnCancelWithdraw = popupView.findViewById(R.id.btn_cancel_withdraw);

            btnConfirmWithdraw.setOnClickListener(v -> {
                memberInitViewModel.withDraw(getContext());
                popupWindow.dismiss();
            });

            btnCancelWithdraw.setOnClickListener(v -> popupWindow.dismiss());

            popupWindow.showAtLocation(getView(), Gravity.CENTER, 0, 0);
        }
    }
    private void showMemberInfoPopup(MemberInfo memberInfo) {

        if (getActivity() != null) {
            View popupView = getLayoutInflater().inflate(R.layout.popup_member_info, null);
            TextView nicknameTextView = popupView.findViewById(R.id.nicknameTextView);
            TextView nameTextView = popupView.findViewById(R.id.nameTextView);
            TextView birthdateTextView = popupView.findViewById(R.id.birthdateTextView);

            nicknameTextView.setText(memberInfo.getNickName());
            nameTextView.setText(memberInfo.getName());
            birthdateTextView.setText(memberInfo.getBirthDate());
            PopupWindow popupWindow = new PopupWindow(
                    popupView,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );


            Button btnConfirmWithdraw = popupView.findViewById(R.id.btn_confirm_member_info);

            btnConfirmWithdraw.setOnClickListener(v -> {
                // 팝업 닫기
                popupWindow.dismiss();
            });

            btnConfirmWithdraw.setOnClickListener(v -> popupWindow.dismiss());

            popupWindow.showAtLocation(getView(), Gravity.CENTER, 0, 0);
        }else{

        }
    }
}
