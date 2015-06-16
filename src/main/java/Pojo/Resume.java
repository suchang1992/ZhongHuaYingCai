package Pojo;

import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBObject;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/4/24.
 */
public class Resume {
    String resumeID;
    KeyWord keyWord;
    BasicDBObject simpleResume;
    BasicDBObject resumeDetil;
    String version;
    int refreshDate;

    public int getRefreshDate() {
        return refreshDate;
    }

    public void setRefreshDate(int refreshDate) {
        this.refreshDate = refreshDate;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public KeyWord getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(KeyWord keyWord) {
        this.keyWord = keyWord;
    }

    public Resume(String resumeID) {
        this.resumeID = resumeID;
    }

    public String getResumeID() {
        return resumeID;
    }

    public void setResumeID(String resumeID) {
        this.resumeID = resumeID;
    }

    public BasicDBObject getSimpleResume() {
        return simpleResume;
    }

    public void setSimpleResume(BasicDBObject simpleResume) {
        this.simpleResume = simpleResume;
    }

    public BasicDBObject getResumeDetil() {
        return resumeDetil;
    }


    public void setResumeDetil(BasicDBObject resumeDetil) {
        this.resumeDetil = resumeDetil;
    }
}
