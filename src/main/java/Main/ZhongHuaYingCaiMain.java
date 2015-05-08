package Main;

import Pojo.KeyWord;
import Pojo.Resume;
import Utils.*;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

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
        ResumeGetMulti resumeGetMulti = new ResumeGetMulti(500);//实例化
        File file = new File("./errorKeywords.txt");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            StringBuffer stringBuffer = new StringBuffer();

            for (int i = 0; i < keyWordsList.size(); i++) {
                KeyWord keyWord = keyWordsList.get(i);
                logger.info("开始爬取:" + keyWord.getSecondlevel());
                String s = resumeGetMulti.SearchResumeByKeywordUseMulti(keyWord, name, password);
                if (s.equals("999999")) {
                    stringBuffer.append(keyWord.getSecondlevel() + "\n");
//                    logger.error("暂停爬取，休息1分钟");
                    logger.info("999999 错误 暂停1分钟 " + keyWord.getSecondlevel());
                    TimeUnit.SECONDS.sleep(60);
                    continue;
                } else if (s.equals("toofast")){
                    logger.error("暂停爬取，稍后手动启动");
                    break;
                } else {
                    logger.info("填写skipkeywords" + keyWord.getSecondlevel());
                    keyWordsManager.writeKeyWord(keyWord);
                }
                logger.info("完成爬取" + keyWord.getSecondlevel());
                logger.info("新添加简历"+MongoHelper.getNewAddResumeCount());
            }
            fileOutputStream.write(stringBuffer.toString().getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        catch (InterruptedException e) {
//            e.printStackTrace();
//        }
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
