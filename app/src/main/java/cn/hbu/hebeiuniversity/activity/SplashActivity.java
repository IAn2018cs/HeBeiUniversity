package cn.hbu.hebeiuniversity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;

import cn.bmob.v3.Bmob;
import cn.hbu.hebeiuniversity.R;

/**
 * Created by joho on 2016/5/29.
 */
public class SplashActivity extends AppCompatActivity {
    private LinearLayout ll_root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去掉Activity上面的状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //加载布局
        setContentView(R.layout.activity_splash);

        //初始化后端云
        Bmob.initialize(this, "9c955aa536a3f9154e47e4d823d04669");

        //初始化控件
        ll_root = (LinearLayout) findViewById(R.id.ll_root);

        //进入登陆界面
        ententSignIn();

        //添加一个透明动画
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(2000);
        ll_root.startAnimation(alphaAnimation);
    }

    //进入登陆界面
    private void ententSignIn() {
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
