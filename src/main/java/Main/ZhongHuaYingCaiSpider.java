package Main;

import Callable.CallableGetResumeV2;
import Pojo.KeyWord;
import Pojo.PageInfo;
import Pojo.Resume;
import Pojo.SpiderConfig;
import Utils.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2015/6/9.
 */
public class ZhongHuaYingCaiSpider {
    private static Logger logger = Logger.getLogger(ZhongHuaYingCaiSpider.class.getName());
    private SpiderConfig spiderConfig = new SpiderConfig();
    private String keyword_path_name;// = "./keywords.xls";
    private String keyword_output_path;// = "./";
    private String output_file_name;// = "skipkeywords.xls";
    private boolean isInit = false;


    private static int delay = 1000;
    private int count = 0;
    private MongoDBHelper mongoDBHelper;

    private static final int ERROR = -1;
    private static final int ERROR_999999 = -999999;


    public boolean init(String dbName, String db_ip, int port, String collectionName,
                        String username, String password, String area_id, int delay_time,
                        String keyword_path_name, String keyword_output_path, String output_file_name) {
        if (dbName == null) {
            logger.error("dbName == null,example:yingcai");
            return false;
        } else
            this.spiderConfig.setDbName(dbName);
        if (db_ip == null) {
            logger.error("db_ip == null,example:192.168.3.222");
            return false;
        } else
            this.spiderConfig.setDb_ip(db_ip);

        if (port != 27017) {
            this.spiderConfig.setPort(port);
        }

        if (collectionName == null) {
            logger.error("collectionName == null,example:yingcai_resume");
            return false;
        } else
            this.spiderConfig.setCollectionName(collectionName);

        if (username == null) {
            logger.error("username == null");
            return false;
        } else
            this.spiderConfig.setUsername(username);

        if (password == null) {
            logger.error("password == null");
            return false;
        } else
            this.spiderConfig.setPassword(password);

        if (area_id != null) {
            this.spiderConfig.setLimitArea(true);
            this.spiderConfig.setArea_id(area_id);
        }
        if (delay_time != 1000)
            this.spiderConfig.setDelay_time(delay_time);

        if (keyword_path_name == null) {
            logger.error("keyword_path_name == null");
            return false;
        } else
            this.keyword_path_name = keyword_path_name;
        if (keyword_output_path == null) {
            logger.error("keyword_output_path == null");
            return false;
        } else
            this.keyword_output_path = keyword_output_path;
        if (output_file_name == null) {
            logger.error("output_file_name == null");
            return false;
        } else
            this.output_file_name = output_file_name;

        this.isInit = true;
        return true;
    }

    public boolean init(String dbName, String db_ip, int port, String collectionName, String mongodbUserName, String mongodbPassword,
                        String username, String password, String area_id, int delay_time) {
        if (dbName == null) {
            logger.error("dbName == null,example:yingcai");
            return false;
        } else
            this.spiderConfig.setDbName(dbName);
        if (db_ip == null) {
            logger.error("db_ip == null,example:192.168.3.222");
            return false;
        } else
            this.spiderConfig.setDb_ip(db_ip);

        if (port != 27017) {
            this.spiderConfig.setPort(port);
        }

        if (collectionName == null) {
            logger.error("collectionName == null,example:yingcai_resume");
            return false;
        } else
            this.spiderConfig.setCollectionName(collectionName);

        if (mongodbUserName == null) {
            logger.error("mongodbUserName == null,example:admin");
            return false;
        } else
            this.spiderConfig.setMongodbUserName(mongodbUserName);

        if (mongodbPassword == null) {
            logger.error("mongodbPassword == null,example:123456");
            return false;
        } else
            this.spiderConfig.setMongodbPassword(mongodbPassword);

        if (username == null) {
            logger.error("username == null");
            return false;
        } else
            this.spiderConfig.setUsername(username);

        if (password == null) {
            logger.error("password == null");
            return false;
        } else
            this.spiderConfig.setPassword(password);

        if (area_id != null) {
            this.spiderConfig.setLimitArea(true);
            this.spiderConfig.setArea_id(area_id);
        }
        if (delay_time != 1000)
            this.spiderConfig.setDelay_time(delay_time);
        this.isInit = true;
        return true;
    }


    public void start() {
        if (!this.isInit) {
            logger.info("尚未初始化 请执行init()");
            return;
        }
        KeyWordsManager keyWordsManager = new KeyWordsManager();
        ArrayList<KeyWord> keyWordsList = keyWordsManager.getKeyWordsList(this.keyword_path_name, this.keyword_output_path + this.output_file_name);
        this.mongoDBHelper = new MongoDBHelper(spiderConfig);
        for (int i = 0; i < keyWordsList.size(); i++) {
            KeyWord keyWord = keyWordsList.get(i);
            logger.info("开始爬取:" + keyWord.getSecondlevel());
            try {
                int code = SearchResumeByKeyword(keyWord);
                if (code < 0) {
                    logger.info("错误暂停5分钟");
                    TimeUnit.SECONDS.sleep(60 * 5);
                    continue;
                } else {
                    logger.info("填写skipkeywords" + keyWord.getSecondlevel() + ":" + code);
                    keyWordsManager.writeKeyWord(keyWord, code);
                    TimeUnit.SECONDS.sleep(10);
                    count = 0;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private int SearchResumeByKeyword(KeyWord keyWord) throws Exception {
        ZhongHuaYingCaiLogin zhongHuaYingCai = new ZhongHuaYingCaiLogin();
        //登陆
        zhongHuaYingCai.login(this.spiderConfig.getUsername(), this.spiderConfig.getPassword());
        zhongHuaYingCai.loginRedirect();
        logger.info("验证是否登陆成功");
        String s = zhongHuaYingCai.testLogin("http://www.chinahr.com/modules/hmcompanyx/?new=index&src=searchx", zhongHuaYingCai.getHeaderString());
        if (s.contains("抱歉，您访问我们网站速度过快")) {
            logger.error(s);
            logger.error("登陆失败");
            throw new Exception("登陆失败");
        } else
            logger.info(keyWord.getSecondlevel() + ":开始爬取 登陆完成");
        logger.info(keyWord.getSecondlevel() + ":第1页爬取开始");

        HashMap<String, String> formData = new HashMap<>();
        formData.put("flag", "1");
        if (this.spiderConfig.isLimitArea()) {
            formData.put("wishPlacesId", spiderConfig.getArea_id());
            formData.put("recruitRangeSelector312result", spiderConfig.getArea_id());
        }
        formData.put("keywordSelect1", "0");
        formData.put("fuzzyWishPlace", "1");
        formData.put("matchLevel", "1,2");
        formData.put("searcherCount", "0");
        formData.put("used", "0");
        formData.put("allKeyword", "0");
        formData.put("allKeyword2", "0");
        formData.put("keyword", keyWord.getSecondlevel());
        formData.put("keywordSelect", "0");
        formData.put("page", "1");

        s = SpiderGetResume.doPostToGetList(CommonParameter.resume_list_url, formData, zhongHuaYingCai.getHeaderString());
        JSONObject jsonObject = null;
        PageInfo pageInfo = null;
        try {
            jsonObject = JSON.parseObject(s);
            pageInfo = new PageInfo(jsonObject.getJSONObject("res").getJSONObject("page"));
        } catch (Exception e) {
            logger.error("page:" + s);
            if (s.length() < 3) {
                return ERROR;
            } else if (s.contains("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">")) {
                return ERROR;
            }
            jsonObject = JSON.parseObject(s);
            int msg = jsonObject.getIntValue("msg");
            if (msg == 999999) {
                return ERROR_999999;
            }
            return ERROR;
        }
        logger.info(keyWord.getSecondlevel() + ":共有" + pageInfo.getMaxPageNum() + "页");

        FutureTask<Integer>[] tasks = null;
        ExecutorService pool = null;
        int pageNum = 0;
        if (pageInfo.getMaxPageNum() > 1) {
            logger.info(keyWord.getSecondlevel() + " 建立线程池");
            pool = Executors.newFixedThreadPool(3);
            pageNum = pageInfo.getMaxPageNum() > 8 ? 8 : pageInfo.getMaxPageNum();
//            int pageNum = pageInfo.getMaxPageNum();
            tasks = new FutureTask[pageNum+1];
            for (int i = 2; i <= pageNum; i++) {
                logger.info(keyWord.getSecondlevel() + " 建立线程 " + i);
                tasks[i] = new FutureTask<Integer>(new CallableGetResumeV2(zhongHuaYingCai.getHeaderString(), this.spiderConfig, this.mongoDBHelper, keyWord, i, 0));
                pool.submit(tasks[i]);
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            pool.shutdown();
        }

        JSONArray jsonArray = jsonObject.getJSONObject("res").getJSONArray("resumeList");
        Iterator<Object> iterator = jsonArray.iterator();
        while (iterator.hasNext()) {
            JSONObject obj = (JSONObject) iterator.next();
            Resume resume = SpiderGetResume.getResumeDetil(obj, keyWord, zhongHuaYingCai.getHeaderString(), this.mongoDBHelper,1000);
            if (resume != null) {
                int i = this.mongoDBHelper.upsertResumInfo(resume, keyWord);
                if (i == MongoDBHelper.INSERT)
                    count++;
            }
        }
        logger.info(keyWord.getSecondlevel() + "第1页爬取完成");
        if (pageInfo.getMaxPageNum() > 1) {
            logger.info(keyWord.getSecondlevel() + "等待子线程结束");
            for (int i = 2; i <= pageNum; i++) {
                count += tasks[i].get();
            }
            logger.info(keyWord.getSecondlevel() + "子线程结束");
        }
        return count;
    }





}
