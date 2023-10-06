package military.gunbam.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


// 모든 엑티비티에 중복되는 값
public class BasicActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE); // Remove title
    }

    protected void startActivity(Class targetActivityClass) {
        Intent intent = new Intent(this, targetActivityClass);
        startActivity(intent);
    }

    protected void startNewActivityAndClearStack(Class targetActivityClass) {
        Intent intent = new Intent(this, targetActivityClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    protected void showToast(Activity activity, String msg){
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
    }
}
