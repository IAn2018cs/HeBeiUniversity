package cn.hbu.hebeiuniversity.Fragment;

        import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.hbu.hebeiuniversity.R;
import cn.hbu.hebeiuniversity.activity.bk_intro;
import cn.hbu.hebeiuniversity.activity.heda_intro;
import cn.hbu.hebeiuniversity.activity.st_intro;
import cn.hbu.hebeiuniversity.activity.zc_intro;

/**
 * Created by @vitovalov on 30/9/15.
 */
public class HomeFragment extends Fragment implements View.OnClickListener{

    private TextView tv_0, tv_1, tv_2, tv_3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initView(view);
        initListener();
        return view;
    }

    private void initView(View view) {
        tv_1 = (TextView) view.findViewById(R.id.tv_1);
        tv_2 = (TextView) view.findViewById(R.id.tv_2);
        tv_3 = (TextView) view.findViewById(R.id.tv_3);
        tv_0 = (TextView) view.findViewById(R.id.tv_0);
    }

    private void initListener() {
        tv_1.setOnClickListener(this);
        tv_2.setOnClickListener(this);
        tv_3.setOnClickListener(this);
        tv_0.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_0:
                Intent intent0 = new Intent(getContext(), heda_intro.class);
                startActivity(intent0);
                break;
            case R.id.tv_1:
                Intent intent1 = new Intent(getContext(), zc_intro.class);
                startActivity(intent1);
                break;
            case R.id.tv_2:
                Intent intent2 = new Intent(getContext(), bk_intro.class);
                startActivity(intent2);
                break;
            case R.id.tv_3:
                Intent intent3 = new Intent(getContext(), st_intro.class);
                startActivity(intent3);
                break;


            default:
                break;
        }
    }
}
