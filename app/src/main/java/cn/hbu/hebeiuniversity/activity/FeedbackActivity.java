package cn.hbu.hebeiuniversity.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.hbu.hebeiuniversity.R;
import cn.hbu.hebeiuniversity.db.CustomerFeedback;
import cn.hbu.hebeiuniversity.utils.ToastUtli;

/**
 * Created by Administrator on 2016/9/22/022.
 */
public class FeedbackActivity extends AppCompatActivity {

    private EditText et_content;
    private Button bt_submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //加载布局
        setContentView(R.layout.activity_feedback);

        setTitle("意见反馈");

        initUI();
    }

    private void initUI() {
        et_content = (EditText) findViewById(R.id.et_content);
        bt_submit = (Button) findViewById(R.id.bt_submit);

        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = et_content.getText().toString().trim();
                if(!content.equals("")){
                    CustomerFeedback cf = new CustomerFeedback();
                    cf.setContent(content);
                    cf.save(new SaveListener<String>() {
                        @Override
                        public void done(String objectId,BmobException e) {
                            if(e==null){
                                ToastUtli.show(getApplicationContext(),"提交成功，感谢您的反馈");
                                finish();
                            }else{
                                ToastUtli.show(getApplicationContext(),"创建数据失败：" + e.getMessage());
                            }
                        }
                    });
                }else {
                    ToastUtli.show(getApplicationContext(),"请不要提交空内容哦");
                }
            }
        });
    }
}
