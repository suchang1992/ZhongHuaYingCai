package Main;

import Pojo.Resume;
import Utils.ResumeGet;
import Utils.mongohelper;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/4/24.
 */
public class ZhongHuaYingCaiMain {

    public void SearchResumeByKeyWord(String keyword, String name, String password) {
        ResumeGet resumeGet = new ResumeGet();
        ArrayList<Resume> resumes = resumeGet.SearchResume(keyword, name, password);
        SaveInMongo(resumes);
        System.out.println("finish");
    }

    private void SaveInMongo(ArrayList<Resume> resumes){
        new mongohelper().inserResumeInfo(resumes.get(0));
        System.out.println("111");
    }

    public static void main(String[] args) {
        new ZhongHuaYingCaiMain().SearchResumeByKeyWord("船舶乘务","vipcdylf", "longhu123");
    }
}
