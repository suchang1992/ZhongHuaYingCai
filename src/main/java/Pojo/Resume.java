package Pojo;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/4/24.
 */
public class Resume {
    String resumeID;
    KeyWord keyWord;
    JSONObject simpleResume;
    JSONObject resumeDetil;


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

    public JSONObject getSimpleResume() {
        return simpleResume;
    }

    public void setSimpleResume(JSONObject simpleResume) {
        try{
            simpleResume.remove("percent");
        }catch (Exception e){
            e.printStackTrace();
            if (simpleResume==null)
                System.out.println("simpleResume null");
        }
        this.simpleResume = simpleResume;
    }

    public JSONObject getResumeDetil() {
        return resumeDetil;
    }


    public void setResumeDetil(JSONObject resumeDetil) {
        try{
            resumeDetil.remove("percent");
            resumeDetil.remove("score");
        }catch (Exception e){
            e.printStackTrace();
            if (resumeDetil==null)
                System.out.println("resumeDetil null");
        }
        this.resumeDetil = resumeDetil;
    }
}
