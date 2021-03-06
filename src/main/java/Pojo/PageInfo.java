package Pojo;


import com.mongodb.BasicDBObject;

/**
 * Created by Administrator on 2015/4/24.
 */
public class PageInfo {
    int maxPageNum;
    int maxResumeNum;
//    int maxCanReadPageNum;

    public PageInfo(BasicDBObject jsonObject) {
        this.maxPageNum = jsonObject.getInt("max");
        this.maxResumeNum = jsonObject.getInt("total");
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
