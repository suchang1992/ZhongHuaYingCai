package Test;

import Pojo.KeyWord;
import Pojo.Resume;
import Utils.MongoHelper;
import Utils.ResumeGet;
import Utils.ZhongHuaYingCaiLogin;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Administrator on 2015/4/27.
 */
public class Test1 {
    public static void main(String[] args)  {
        KeyWord keyWord = new KeyWord("物流","船舶乘务",0,4);
        ResumeGet resumeGet = new ResumeGet();//实例化
        ArrayList<Resume> resumes = resumeGet.SearchResumeByKeyword(keyWord,"vipcdylf", "longhu123");
        new MongoHelper().upsertResumInfo(resumes.get(0),keyWord);
    }
}
