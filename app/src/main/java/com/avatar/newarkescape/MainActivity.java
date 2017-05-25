package com.avatar.newarkescape;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    public static final String ACTION_SLIDE_START = "com.avatar.newark.slide_start";
    public static final String ACTION_SLIDE_END = "com.avatar.newark.slide_end";

    private BaseFragment[] fragments = new BaseFragment[1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().add(R.id.activity_main_container, getFragment(0)).commit();
    }

    private Fragment getFragment(int position) {
        if (position == 0) {
            if (fragments[0] == null) {
                fragments[0] = new ControllerFragment();
            }
            return fragments[0];
        } else {
            Log.e(TAG, "the position of fragment is ERROR!!!!");
            return null;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    //点击返回按键时activity会销毁再重启。所以屏蔽掉返回按键。
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    public void updateLanguage(boolean isChinese) {
        for (BaseFragment fragment : fragments) {
            if (isChinese) {
                fragment.setTextToChinese();
            } else {
                fragment.setTextToEnglish();
            }
        }
    }
}
