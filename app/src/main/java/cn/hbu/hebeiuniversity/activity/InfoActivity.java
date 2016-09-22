package cn.hbu.hebeiuniversity.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.baidu.mapapi.map.MapView;

import cn.hbu.hebeiuniversity.R;

/**
 * Created by Administrator on 2016/9/22/022.
 */
public class InfoActivity extends AppCompatActivity {

    private MapView mMapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //加载布局
        setContentView(R.layout.activity_info);
        setTitle("关于我们");

    }

}
