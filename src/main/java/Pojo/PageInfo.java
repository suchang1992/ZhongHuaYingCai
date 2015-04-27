package Pojo;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * Created by Administrator on 2015/4/24.
 */
public class PageInfo {
    int maxPageNum;
    int maxResumeNum;
//    int maxCanReadPageNum;

    public PageInfo(JSONObject jsonObject) {
        this.maxPageNum = jsonObject.getInteger("max");
        this.maxResumeNum = jsonObject.getInteger("total");
    }

    public int getMaxPageNum() {
        return maxPageNum;
    }

    public void setMaxPageNum(int maxPageNum) {
        this.maxPageNum = maxPageNum;
    }

    public int getMaxResumeNum() {
        return maxResumeNum;
    }

    public void setMaxResumeNum(int maxResumeNum) {
        this.maxResumeNum = maxResumeNum;
    }
}
