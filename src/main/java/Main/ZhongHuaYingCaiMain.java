package Main;

import Pojo.KeyWord;
import Pojo.Resume;
import Utils.*;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Administrator on 2015/4/24.
 */
public class ZhongHuaYingCaiMain {
    private static Logger logger = Logger.getLogger(ZhongHuaYingCaiMain.class.getName());

    /**
     * 读取xls中关键词，完成遍历爬取
     *
     * @param name
     * @param password
     */
    public void SearchResumeByxls(String name, String password) {
        KeyWordsManager keyWordsManager = new KeyWordsManager();
        ArrayList<KeyWord> keyWordsList = keyWordsManager.getKeyWordsList();//读取需要爬取的 关键词
        ResumeGet resumeGet = new ResumeGet();//实例化
        for (int i = 0; i < keyWordsList.size(); i++) {
            KeyWord keyWord = keyWordsList.get(i);
            logger.info("开始爬取:" + keyWord.getSecondlevel());
            ArrayList<Resume> resumes = resumeGet.SearchResumeByKeyword(keyWord, name, password);
            logger.info("填写skipkeywords" + keyWord.getSecondlevel());
            keyWordsManager.writeKeyWord(keyWord);
            logger.info("完成爬取" + keyWord.getSecondlevel());
        }
    }

    public void SearchResumeByxlsUseMulti(String name, String password) {
        KeyWordsManager keyWordsManager = new KeyWordsManager();
        ArrayList<KeyWord> keyWordsList = keyWordsManager.getKeyWordsList();//读取需要爬取的 关键词
        ResumeGetMulti resumeGetMulti = new ResumeGetMulti();//实例化
        for (int i = 0; i < keyWordsList.size(); i++) {
            KeyWord keyWord = keyWordsList.get(i);
            logger.info("开始爬取:" + keyWord.getSecondlevel());
            resumeGetMulti.SearchResumeByKeywordUseMulti(keyWord, name, password);
            logger.info("填写skipkeywords" + keyWord.getSecondlevel());
            keyWordsManager.writeKeyWord(keyWord);
            logger.info("完成爬取" + keyWord.getSecondlevel());
        }
    }


    private void saveInMongo(ArrayList<Resume> resumes, KeyWord keyWord) {
        MongoHelper mongoHelper = new MongoHelper();
        for (int i = 0; i < resumes.size(); i++) {
            mongoHelper.upsertResumInfo(resumes.get(i), keyWord);
        }
    }

    public static void main(String[] args) {
//        new ZhongHuaYingCaiMain().SearchResumeByxls("vipcdylf", "longhu123");
        new ZhongHuaYingCaiMain().SearchResumeByxlsUseMulti("vipcdylf", "longhu123");
    }
}
