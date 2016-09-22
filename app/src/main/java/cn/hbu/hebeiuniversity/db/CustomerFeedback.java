package cn.hbu.hebeiuniversity.db;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2016/9/22/022.
 */

public class CustomerFeedback extends BmobObject {
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
