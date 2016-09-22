package cn.hbu.hebeiuniversity.activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.thinkland.sdk.android.DataCallBack;
import com.thinkland.sdk.android.JuheData;
import com.thinkland.sdk.android.Parameters;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cn.hbu.hebeiuniversity.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    private LocationClient mLocationClient;
    private ConnectivityManager mConnectivityManager;
    private NetworkInfo mNetworkInfo;
    public BDLocationListener myListener = new MyLocationListener();
    private String address;
    private TextView tv;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View view = navigationView.getHeaderView(0);
        imageView = (ImageView) view.findViewById(R.id.imageView);

        tv = (TextView) findViewById(R.id.tv);



        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        initLocation();
        mLocationClient.registerLocationListener( myListener );    //注册监听函数

        //判断是否有网络
        //获取网络状态
        mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (mNetworkInfo == null) {
            Toast.makeText(getApplicationContext(), "定位失败，请检查网络", Toast.LENGTH_SHORT).show();
        }

        mLocationClient.start();


    }

    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span=1000*60*30;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }

    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //Receive Location
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");

            sb.append(location.getTime());

            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());// 单位：公里每小时
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
                sb.append("\nheight : ");
                sb.append(location.getAltitude());// 单位：米
                sb.append("\ndirection : ");
                sb.append(location.getDirection());// 单位度

                sb.append("\naddr : ");
                address = location.getAddrStr();
                sb.append(location.getAddrStr());

                sb.append("\ndescribe : ");
                sb.append("gps定位成功");

            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                address = location.getAddrStr();
                //运营商信息
                sb.append("\noperationers : ");
                sb.append(location.getOperators());
                sb.append("\ndescribe : ");
                sb.append("网络定位成功");
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }
            sb.append("\nlocationdescribe : ");
            sb.append(location.getLocationDescribe());// 位置语义化信息
            List<Poi> list = location.getPoiList();// POI数据
            if (list != null) {
                sb.append("\npoilist size = : ");
                sb.append(list.size());
                for (Poi p : list) {
                    sb.append("\npoi= : ");
                    sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                }
            }
            Log.i("MainActivity", sb.toString());
            if(address != null){
                address = address.substring(5,8);
                Log.i(TAG,address);
            }
            checkWeather(address);
        }
    }

    private void checkWeather(String address) {
        Parameters params = new Parameters();
        params.add("cityname", address);
        params.add("dtype", "json");
        JuheData.executeWithAPI(this, 73,
                "http://op.juhe.cn/onebox/weather/query", JuheData.GET, params, new DataCallBack() {
                    @Override
                    public void onSuccess(int i, String s) {
                        try {
                            StringBuffer sb = new StringBuffer();
                            JSONObject jsonObject = new JSONObject(s);
                            Log.i(TAG,s);
                            JSONObject result = jsonObject.getJSONObject("result");
                            if(result != null){
                                JSONObject data = result.getJSONObject("data");
                                if(data != null){
                                    JSONObject realtime = data.getJSONObject("realtime");
                                    if(realtime != null){
                                        // 城市
                                        realtime.getString("city_name");
                                        Log.i(TAG,"城市:"+realtime.getString("city_name"));
                                        sb.append("城市:"+realtime.getString("city_name")+'\n');
                                        // 日期
                                        realtime.getString("date");
                                        Log.i(TAG,"日期:"+realtime.getString("date"));
                                        sb.append("日期:"+realtime.getString("date")+'\n');
                                        // 更新时间
                                        realtime.getString("time");
                                        Log.i(TAG,"更新时间:"+realtime.getString("time"));
                                        sb.append("更新时间:"+realtime.getString("time")+'\n');
                                        // 星期
                                        realtime.getString("week");
                                        Log.i(TAG,"星期:"+realtime.getString("week"));
                                        sb.append("星期:"+realtime.getString("week")+'\n');
                                        // 阴历
                                        realtime.getString("moon");
                                        Log.i(TAG,"阴历:"+realtime.getString("moon"));
                                        sb.append("阴历:"+realtime.getString("moon")+'\n');
                                        // 实况天气
                                        JSONObject weather = realtime.getJSONObject("weather");
                                        if(weather != null){
                                            // 温度
                                            weather.getString("temperature");
                                            Log.i(TAG,"温度:"+weather.getString("temperature"));
                                            sb.append("温度:"+weather.getString("temperature")+'\n');
                                            // 湿度
                                            weather.getString("humidity");
                                            Log.i(TAG,"湿度:"+weather.getString("humidity"));
                                            sb.append("湿度:"+weather.getString("humidity")+'\n');
                                            // 天气
                                            weather.getString("info");
                                            Log.i(TAG,"天气:"+weather.getString("info"));
                                            sb.append("天气:"+weather.getString("info")+'\n');
                                            // 图片
                                            sb.append("图片:"+weather.getString("img")+'\n');
                                            changeImg(weather.getString("img"));
                                        }
                                    }
                                }
                            }
                            tv.setText(sb.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFinish() {

                    }

                    @Override
                    public void onFailure(int i, String s, Throwable throwable) {

                    }
                });
    }

    private void changeImg(String img) {
        if(img.equals("00")){
            imageView.setImageResource(R.drawable.ww0);
        }else if(img.equals("1")){
            imageView.setImageResource(R.drawable.ww1);
        }else if(img.equals("2")){
            imageView.setImageResource(R.drawable.ww2);
        }else if(img.equals("3")){
            imageView.setImageResource(R.drawable.ww3);
        }else if(img.equals("4")){
            imageView.setImageResource(R.drawable.ww4);
        }else if(img.equals("5")){
            imageView.setImageResource(R.drawable.ww5);
        }else if(img.equals("6")){
            imageView.setImageResource(R.drawable.ww6);
        }else if(img.equals("7")){
            imageView.setImageResource(R.drawable.ww7);
        }else if(img.equals("8")){
            imageView.setImageResource(R.drawable.ww8);
        }else if(img.equals("9")){
            imageView.setImageResource(R.drawable.ww9);
        }else if(img.equals("10")){
            imageView.setImageResource(R.drawable.ww10);
        }else if(img.equals("13")){
            imageView.setImageResource(R.drawable.ww13);
        }else if(img.equals("14")){
            imageView.setImageResource(R.drawable.ww14);
        }else if(img.equals("15")){
            imageView.setImageResource(R.drawable.ww15);
        }else if(img.equals("16")){
            imageView.setImageResource(R.drawable.ww16);
        }else if(img.equals("17")){
            imageView.setImageResource(R.drawable.ww17);
        }else if(img.equals("18")){
            imageView.setImageResource(R.drawable.ww18);
        }else if(img.equals("19")){
            imageView.setImageResource(R.drawable.ww19);
        }else if(img.equals("20")){
            imageView.setImageResource(R.drawable.ww20);
        }else if(img.equals("29")){
            imageView.setImageResource(R.drawable.ww29);
        }else if(img.equals("30")){
            imageView.setImageResource(R.drawable.ww30);
        }else if(img.equals("31")){
            imageView.setImageResource(R.drawable.ww31);
        }else if(img.equals("32")){
            imageView.setImageResource(R.drawable.ww32);
        }else if(img.equals("33")){
            imageView.setImageResource(R.drawable.ww33);
        }else if(img.equals("34")){
            imageView.setImageResource(R.drawable.ww34);
        }else if(img.equals("35")){
            imageView.setImageResource(R.drawable.ww35);
        }else if(img.equals("36")){
            imageView.setImageResource(R.drawable.ww36);
        }else if(img.equals("45")){
            imageView.setImageResource(R.drawable.ww45);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        JuheData.cancelRequests(getApplication());
    }
}
