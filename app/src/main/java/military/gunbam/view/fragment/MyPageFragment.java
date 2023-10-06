package military.gunbam.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

import java.lang.reflect.Member;

import military.gunbam.R;
import military.gunbam.utils.Util;
import military.gunbam.view.activity.BoardListActivity;
import military.gunbam.view.activity.LoginActivity;
import military.gunbam.view.activity.MemberInitActivity;
import military.gunbam.view.activity.PostActivity;
import military.gunbam.viewmodel.MemberInitViewModel;

public class MyPageFragment extends Fragment {
    public MyPageFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_page, container, false);

        // 회원정보 조회 버튼
        view.findViewById(R.id.viewProfileButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //StartActivity(CalculatorFragment.class);
            }
        });

        // 내가 쓴 글 조회 버튼
        view.findViewById(R.id.viewPostsButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String publisher= "";
                String publisher = FirebaseAuth.getInstance().getCurrentUser().getUid();
                myStartActivity(BoardListActivity.class, "publisher", publisher);
            }

        });

        // 내가 쓴 댓글 조회 버튼
        view.findViewById(R.id.viewCommentsButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //myStartActivity(BoardListActivity.class, "publisher",);
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
                //myStartActivity(MemberInitActivity.class);
            }
        });

        // 회원 탈퇴 버튼
        view.findViewById(R.id.deleteAccountButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MemberInitViewModel viewModel;
                //viewModel.
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
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 여기에 프래그먼트가 생성될 때 실행할 코드를 작성합니다.
    }
}
