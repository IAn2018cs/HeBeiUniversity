package cn.hbu.hebeiuniversity.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.thinkland.sdk.android.JuheData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.FindListener;
import cn.hbu.hebeiuniversity.Fragment.HomeFragment;
import cn.hbu.hebeiuniversity.Fragment.SignFragment;
import cn.hbu.hebeiuniversity.R;
import cn.hbu.hebeiuniversity.db.UpdateFile;
import cn.hbu.hebeiuniversity.utils.Constant;
import cn.hbu.hebeiuniversity.utils.SpUtils;
import cn.hbu.hebeiuniversity.utils.ToastUtli;
import cn.hbu.hebeiuniversity.utils.Weather;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";
    private LocationClient mLocationClient;
    private ConnectivityManager mConnectivityManager;
    private NetworkInfo mNetworkInfo;
    public BDLocationListener myListener = new MyLocationListener();
    private String address;
    private TextView tv_city;
    private ImageView imageView;
    private TextView tv_weather;
    private TextView tv_temperature;
    private String[] mLanguageDes = new String[]{"简体中文", "English", "日本の"};
    private String pathName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/hebeiuniversity.apk";
    private BmobQuery<UpdateFile> bmobQuery = new BmobQuery<UpdateFile>();
    private int mLanguageIndex;
    private ProgressDialog progressDialog;
    private BmobFile mBmobfile;
    private File file = new File(pathName);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化默认UI
        initDefUI();

        // 初始化侧滑菜单栏
        initNavigationView();

        // 初始化定位数据
        initLocationData();

        // 检测更新
        checkVersionCode();
    }

    // viewPager适配器
    static class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    // 设置viewpager
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new HomeFragment(), "主页");
        adapter.addFrag(new SignFragment(), "签到");
        adapter.addFrag(new HomeFragment(), "图集");

        viewPager.setAdapter(adapter);
    }

    // 初始化定位数据
    private void initLocationData() {
        //判断是否有网络
        //获取网络状态
        mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (mNetworkInfo == null) {
            ToastUtli.show(getApplicationContext(),"定位失败，请检查网络");
        }

        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        // 初始化定位
        initLocation();
        mLocationClient.registerLocationListener( myListener );    //注册监听函数

        // 开启定位
        mLocationClient.start();
    }

    // 初始化侧滑菜单栏
    private void initNavigationView() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        imageView = (ImageView) headerView.findViewById(R.id.imageView);
        tv_city = (TextView) headerView.findViewById(R.id.tv_city);
        tv_weather = (TextView) headerView.findViewById(R.id.tv_weather);
        tv_temperature = (TextView) headerView.findViewById(R.id.tv_temperature);
    }

    // 初始化定位
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

    // 定位监听类
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
            // 查询天气  并更新
            Weather.checkWeather(getApplication(), address, imageView, new Weather.WeatherCallBack() {
                @Override
                public void setUI(String city, String weather, String temperature) {
                    tv_city.setText(city);
                    tv_weather.setText(weather);
                    tv_temperature.setText(temperature+"°C");
                }
            });
        }
    }

    // 侧滑菜单点击事件
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        // 语言选择
        if (id == R.id.nav_language) {
            showSingleChoiceDialog();
        // 关于我们
        } else if (id == R.id.nav_info) {
            startActivity(new Intent(this,InfoActivity.class));
        // 意见反馈
        } else if (id == R.id.nav_feedback) {
            startActivity(new Intent(this,FeedbackActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // 返回键
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // 初始化默认UI
    private void initDefUI() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // 设置viewpager
        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        // 设置tabs
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    // 展示一个单选对话框
    protected void showSingleChoiceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请选择语言");
        mLanguageIndex = SpUtils.getIntSp(getApplicationContext(), Constant.GROUP_LANGUAGE_INT, 0);
        // 设置单选框(选项内容, 被选中的条目索引, 监听事件)
        builder.setSingleChoiceItems(mLanguageDes, mLanguageIndex, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, final int which) {
                //关闭对话框
                dialog.dismiss();

                //记录点击的索引值
                SpUtils.putIntSp(getApplicationContext(), Constant.GROUP_LANGUAGE_INT, which);
            }
        });
        // 设置消极的按钮
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        // 显示对话框
        builder.show();
    }

    // 检测更新
    private void checkVersionCode() {
        bmobQuery.findObjects(new FindListener<UpdateFile>() {
            @Override
            public void done(List<UpdateFile> object, BmobException e) {
                if(e==null){
                    for (UpdateFile updatefile : object) {
                        // 如果服务器的版本号大于本地的  就更新
                        if(updatefile.getVersion() > getVersionCode()){
                            BmobFile bmobfile = updatefile.getFile();
                            mBmobfile = bmobfile;
                            // 文件路径不为null  并且sd卡可用
                            if(file != null && Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                                // 展示下载对话框
                                showUpDataDialog(updatefile.getDescription(),bmobfile,file);
                            }
                        }
                    }
                }else{
                    Log.i("Bmob文件传输","查询失败："+e.getMessage());
                }
            }
        });
    }

    // 显示更新对话框
    protected void showUpDataDialog(String description, final BmobFile bmobfile, final File file) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        //设置对话框左上角图标
        builder.setIcon(R.mipmap.ic_launcher);
        //设置对话框标题
        builder.setTitle("发现新版本");
        //设置对话框内容
        builder.setMessage(description);
        //设置积极的按钮
        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    //请求权限
                    requestCameraPermission();
                } else {
                    //下载apk
                    downLoadApk(bmobfile, file);
                    // 显示一个进度条对话框
                    showProgressDialog();
                }
            }
        });
        //设置消极的按钮
        builder.setNegativeButton("暂不更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        //监听取消按钮
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            //当点击返回的按钮时执行
            @Override
            public void onCancel(DialogInterface dialog) {
                //让对话框消失
                dialog.dismiss();
            }
        });

        builder.show();
    }

    // 下载的进度条对话框
    protected void showProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setIcon(R.mipmap.ic_launcher);
        progressDialog.setTitle("下载安装包中");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();
    }

    // 下载文件
    private void downLoadApk(BmobFile bmobfile, final File file) {
        //调用bmobfile.download方法
        bmobfile.download(file, new DownloadFileListener() {
            @Override
            public void done(String s, BmobException e) {
                if(e==null){
                    ToastUtli.show(getApplicationContext(),"下载成功,保存路径:"+pathName);
                    Log.i("Bmob文件下载","下载成功,保存路径:"+pathName);
                    installApk(file);
                    progressDialog.dismiss();
                }else{
                    ToastUtli.show(getApplicationContext(),"下载失败："+e.getErrorCode()+","+e.getMessage());
                    Log.i("Bmob文件下载","下载失败："+e.getErrorCode()+","+e.getMessage());
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onProgress(Integer integer, long l) {
                progressDialog.setProgress(integer);
            }
        });
    }

    // 安装应用
    protected void installApk(File file) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        //文件作为数据源
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        startActivity(intent);
    }

    // 获取本应用版本号
    private int getVersionCode() {
        // 拿到包管理者
        PackageManager pm = getPackageManager();
        // 获取包的基本信息
        try {
            PackageInfo info = pm.getPackageInfo(getPackageName(), 0);
            // 返回应用的版本号
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // 6.0请求权限
    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
    }

    // 6.0请求权限结果
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //下载apk
                downLoadApk(mBmobfile, file);
                // 显示一个进度条对话框
                showProgressDialog();
            } else {
                ToastUtli.show(getApplicationContext(),"请求写入文件");
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        JuheData.cancelRequests(getApplication());
    }
}
