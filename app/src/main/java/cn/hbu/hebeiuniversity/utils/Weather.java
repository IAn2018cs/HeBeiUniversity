package cn.hbu.hebeiuniversity.utils;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.thinkland.sdk.android.DataCallBack;
import com.thinkland.sdk.android.JuheData;
import com.thinkland.sdk.android.Parameters;

import org.json.JSONException;
import org.json.JSONObject;

import cn.hbu.hebeiuniversity.R;

/**
 * Created by Administrator on 2016/9/22/022.
 */

public class Weather {

    private static final String TAG = "Weather";
    private static String strcity;
    private static String strweather;
    private static String strtemperature;

    /**
     * 查询天气
     * @param context 上下文环境
     * @param address 查询天气的城市
     * @param imageView 设置天气图片的view
     * @param cb 回调方法  更新UI
     */
    public static void checkWeather(Context context, String address, final ImageView imageView, final WeatherCallBack cb) {
        Parameters params = new Parameters();
        params.add("cityname", address);
        params.add("dtype", "json");
        JuheData.executeWithAPI(context, 73,
                "http://op.juhe.cn/onebox/weather/query", JuheData.GET, params, new DataCallBack() {
                    @Override
                    public void onSuccess(int i, String s) {
                        try {
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
                                        strcity = realtime.getString("city_name");
                                        // 日期
                                        realtime.getString("date");
                                        Log.i(TAG,"日期:"+realtime.getString("date"));
                                        // 更新时间
                                        realtime.getString("time");
                                        Log.i(TAG,"更新时间:"+realtime.getString("time"));
                                        // 星期
                                        realtime.getString("week");
                                        Log.i(TAG,"星期:"+realtime.getString("week"));
                                        // 阴历
                                        realtime.getString("moon");
                                        Log.i(TAG,"阴历:"+realtime.getString("moon"));
                                        // 实况天气
                                        JSONObject weather = realtime.getJSONObject("weather");
                                        if(weather != null){
                                            // 温度
                                            weather.getString("temperature");
                                            Log.i(TAG,"温度:"+weather.getString("temperature"));
                                            strtemperature = weather.getString("temperature");
                                            // 湿度
                                            weather.getString("humidity");
                                            Log.i(TAG,"湿度:"+weather.getString("humidity"));
                                            // 天气
                                            weather.getString("info");
                                            Log.i(TAG,"天气:"+weather.getString("info"));
                                            strweather = weather.getString("info");
                                            // 图片
                                            Log.i(TAG,"图片:"+weather.getString("img"));
                                            changeImg(imageView,weather.getString("img"));
                                        }
                                    }
                                }
                            }
                            // 回调 更改UI
                            cb.setUI(strcity, strweather, strtemperature);
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

    public static void changeImg(ImageView imageView, String img) {
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

    public static abstract class WeatherCallBack {
        /**
         * @param city 城市
         * @param weather 天气
         * @param temperature 温度
         */
        public abstract void setUI(String city, String weather, String temperature);
    }
}
