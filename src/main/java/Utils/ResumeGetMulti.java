package Utils;

import Callable.CallableGetResume;
import Pojo.KeyWord;
import Pojo.PageInfo;
import Pojo.Resume;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import com.mongodb.util.JSONParseException;
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
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2015/4/29.
 */
public class ResumeGetMulti {

    private static Logger logger = Logger.getLogger(ResumeGetMulti.class.getName());

    int delay = 0;

    public ResumeGetMulti(int delay) {
        this.delay = delay;
    }

    public String SearchResumeByKeywordUseMulti(KeyWord keyWord, String name, String password) {
        String s = null;
        ZhongHuaYingCaiLogin zhongHuaYingCai = new ZhongHuaYingCaiLogin();
        //登陆
        try {
            zhongHuaYingCai.login(name, password);
            zhongHuaYingCai.loginRedirect();
            logger.info("验证是否登陆成功");
            s = zhongHuaYingCai.testLogin("http://www.chinahr.com/modules/hmcompanyx/?new=index&src=searchx", zhongHuaYingCai.getHeaderString());
            if (s.contains("抱歉，您访问我们网站速度过快")) {
                logger.error(s);
                logger.error("登陆失败");
                return "toofast";
            } else
                logger.info(keyWord.getSecondlevel() + ":开始爬取 登陆完成");
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("登陆失败");
            return "error";
        }
        logger.info(keyWord.getSecondlevel() + ":第1页爬取开始");

        HashMap<String, String> formData = new HashMap<>();
        formData.put("flag", "1");
        formData.put("recruitRangeSelector312result", "27,312");
        formData.put("wishPlacesId", "27,312");
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
        String url = "http://www.chinahr.com/modules/jmw/SocketAjax.php?m=hmresume&f=resume&action=myresume&list_type=search&usetoken=1";
        s = doPostToGetList(url, formData, zhongHuaYingCai.getHeaderString());
        int maxPostCount = 3;
        if (s.length() < 3 && maxPostCount-- > 0) {
            s = doPostToGetList(url, formData, zhongHuaYingCai.getHeaderString());
        }
        BasicDBObject jsonObject = null;
        PageInfo pageInfo = null;
        try {
            jsonObject = (BasicDBObject) JSON.parse(s);
            pageInfo = new PageInfo((BasicDBObject)((BasicDBObject)jsonObject.get("res")).get("page"));
        } catch (Exception e) {
            logger.error("page:" + s);
            if (s.length() < 3) {
                return "error";
            } else if (s.contains("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">")) {
                return "error";
            }
            jsonObject = (BasicDBObject)JSON.parse(s);
            int msg = jsonObject.getInt("msg");
            if (msg == 999999) {
                return "" + msg;
            }
            return "error";
        }
        logger.info(keyWord.getSecondlevel() + ":共有" + pageInfo.getMaxPageNum() + "页");
        ExecutorService pool = null;
        if (pageInfo.getMaxPageNum() > 1) {
            logger.info(keyWord.getSecondlevel() + " 建立线程池");
            pool = Executors.newFixedThreadPool(3);
            int pageNum = pageInfo.getMaxPageNum() > 8 ? 8 : pageInfo.getMaxPageNum();
//            int pageNum = pageInfo.getMaxPageNum();
            for (int i = 2; i <= pageNum; i++) {
                pool.submit(new CallableGetResume(zhongHuaYingCai, keyWord, i, delay));
                logger.info(keyWord.getSecondlevel() + " 建立线程 " + i);
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            pool.shutdown();
        }
        BasicDBList jsonArray = (BasicDBList)((BasicDBObject)jsonObject.get("res")).get("resumeList");
        Iterator<Object> iterator = jsonArray.iterator();
        while (iterator.hasNext()) {
            BasicDBObject obj = (BasicDBObject) iterator.next();
            Resume resume = getResumeDetil(obj, keyWord, zhongHuaYingCai);
            if (resume != null)
                new MongoHelper().upsertResumInfo(resume, keyWord);
        }




        logger.info(keyWord.getSecondlevel() + "第1页爬取完成");
        return "success";
    }

    private Resume getResumeDetil(BasicDBObject obj, KeyWord keyWord, ZhongHuaYingCaiLogin zhongHuaYingCai) {
        String resumeID = obj.getString("cvId");

        Resume resume = new Resume(resumeID);
        resume.setKeyWord(keyWord);
        resume.setSimpleResume(obj);
        if (new MongoHelper().isInMongoSearchById(resumeID)) {
            logger.info("skip:" + resumeID);
            return resume;
        }
        try {
            logger.info("休息" + delay + "毫秒");
            TimeUnit.MILLISECONDS.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String s = getResumeDetil(resumeID, keyWord.getSecondlevel(), zhongHuaYingCai.getHeaderString());
        if (s.equals("400")) {
            logger.info(resumeID + " response 为status 400 参数错误");
            return null;
        }
        logger.error(resumeID + ":detil:" + s);
        try {//如果转化失败 则返回空
            resume.setResumeDetil((BasicDBObject)JSON.parse(s));
        } catch (JSONParseException e) {
            logger.error("error:" + s);
            return null;
        }
        return resume;
    }

    private String getResumeDetil(String resumeID, String keyword, String cookie) {
        HashMap<String, String> formData = new HashMap<>();
        formData.put("uid", "");
        formData.put("cvId", resumeID);
        formData.put("hr1", "1");
        formData.put("pr1", "2");
        formData.put("pr2", "2");
        formData.put("hm1", "400");
        formData.put("hm2", "1800");
        formData.put("keyword[]", "null");
        formData.put("iterator", "0");

        String url = "http://www.chinahr.com/modules/jmw/SocketAjax.php?m=hmresume&f=getresume&tp=4";
        String s = getResumeDetil(url, formData, cookie + "kw=" + keyword);
        int maxPostCount = 3;
        if (s.length() <= 3 && maxPostCount-- > 0) {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            s = getResumeDetil(url, formData, cookie);
        }
        return s;
    }

    private String getResumeDetil(String url, HashMap<String, String> formData, String cookie) {
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(url);
        installFormData(formData, request);
        request.addHeader("Cookie", cookie);
        request.addHeader("Accept", "application/json, text/javascript, */*; q=0.01");
        request.addHeader("Connection", "keep-alive");
        request.addHeader("Host", "www.chinahr.com");
        request.addHeader("Origin", "http://www.chinahr.com");
        request.addHeader("X-Requested-With", "XMLHttpRequest");
        request.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.118 Safari/537.36");
        try {
            HttpResponse response = client.execute(request);
            if (response.getStatusLine().getStatusCode() == 400)
                return "400";
//            System.out.println(response.toString());
            return getHtml(response);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private String doPostToGetList(String url, HashMap<String, String> formData, String pageCookie) {
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(url);
        installFormData(formData, request);
        request.addHeader("Cookie", pageCookie);
        request.addHeader("Accept", "application/json, text/javascript, */*; q=0.01");
        request.addHeader("Connection", "keep-alive");
        request.addHeader("Host", "www.chinahr.com");
        request.addHeader("Origin", "http://www.chinahr.com");
        request.addHeader("X-Requested-With", "XMLHttpRequest");
        request.addHeader("Referer", "http://www.chinahr.com/modules/hmresume/index.php?c=searchx&m=result");
        request.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.118 Safari/537.36");
        try {
            HttpResponse response = client.execute(request);
            if (response.getStatusLine().getStatusCode() == 400)
                return "400";
            return getHtml(response);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getHtml(HttpResponse response) {
        StringBuffer result = new StringBuffer();
        try {
            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    public static void installFormData(HashMap<String, String> parameter, HttpPost request) {
        List<NameValuePair> formData = new ArrayList<NameValuePair>();
        for (String key : parameter.keySet()) {
            formData.add(new BasicNameValuePair(key, parameter.get(key)));
        }
        try {
            request.setEntity(new UrlEncodedFormEntity(formData, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
