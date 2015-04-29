package Utils;

import Callable.CallableGetResume;
import Pojo.KeyWord;
import Pojo.PageInfo;
import Pojo.Resume;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
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

    public void SearchResumeByKeywordUseMulti(KeyWord keyWord, String name, String password) {
        String s = null;
        ZhongHuaYingCaiLogin zhongHuaYingCai = new ZhongHuaYingCaiLogin();
        //登陆
        try {
            zhongHuaYingCai.login(name, password);
            zhongHuaYingCai.loginRedirect();
            logger.info(keyWord.getSecondlevel() + ":开始爬取 登陆完成");
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("登陆失败");
        }
        logger.info(keyWord.getSecondlevel() + ":第1页爬取开始");

        HashMap<String, String> formData = new HashMap<>();
        formData.put("flag", "1");
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
        if(s.length()<3){
            logger.error(keyWord.getSecondlevel()+" 第一页获取失败，跳过该关键词");
            return;
        }
        JSONObject jsonObject = null;
        PageInfo pageInfo = null;
        try {
            jsonObject = JSON.parseObject(s);
            pageInfo = new PageInfo(jsonObject.getJSONObject("res").getJSONObject("page"));
        } catch (Exception e) {
            logger.error("page:" + s);
            return;
        }
        logger.info(keyWord.getSecondlevel() + ":共有" + pageInfo.getMaxPageNum() + "页");
        ExecutorService pool = null;
        if (pageInfo.getMaxPageNum() > 1) {
            logger.info(keyWord.getSecondlevel()+" 建立线程池");
            pool = Executors.newFixedThreadPool(7);
            int pageNum = pageInfo.getMaxPageNum() > 8 ? 8 : pageInfo.getMaxPageNum();
            for (int i = 2; i <= pageNum; i++) {
                pool.submit(new CallableGetResume(zhongHuaYingCai, keyWord, i));
                logger.info(keyWord.getSecondlevel()+" 建立线程 "+i);
            }
            pool.shutdown();
        }
        JSONArray jsonArray = jsonObject.getJSONObject("res").getJSONArray("resumeList");
        Iterator<Object> iterator = jsonArray.iterator();
        while (iterator.hasNext()) {
            JSONObject obj = (JSONObject) iterator.next();
            Resume resume = getResumeDetil(obj, keyWord, zhongHuaYingCai);
            if (resume != null)
                new MongoHelper().upsertResumInfo(resume, keyWord);
        }
        logger.info(keyWord.getSecondlevel()+"第1页爬取完成");
    }

    private Resume getResumeDetil(JSONObject obj, KeyWord keyWord, ZhongHuaYingCaiLogin zhongHuaYingCai) {
        String resumeID = obj.getString("cvId");

        Resume resume = new Resume(resumeID);
        resume.setKeyWord(keyWord);
        resume.setSimpleResume(obj);
        if (new MongoHelper().isInMongoSearchById(resumeID)) {
            logger.info("skip:" + resumeID);
            return resume;
        }
        String s = getResumeDetil(resumeID, keyWord.getSecondlevel(), zhongHuaYingCai.getHeaderString());
        if (s.equals("400")) {
            logger.info(resumeID + " response 为status 400 参数错误");
            return null;
        }
        logger.error(resumeID + ":detil:" + s);
        try {//如果转化失败 则返回空
            resume.setResumeDetil(JSONObject.parseObject(s));
        } catch (JSONException e) {
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
        if (s.length() < 3 && maxPostCount-- > 0) {
            s = getResumeDetil(url, formData, cookie + "kw=" + keyword);
        }
        return s;
    }

    private String getResumeDetil(String url, HashMap<String, String> formData, String cookie) {
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(url);
        installFormData(formData, request);
        request.addHeader("Cookie", cookie);
        request.addHeader("Host", "www.chinahr.com");
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
        request.addHeader("Host", "www.chinahr.com");
        request.addHeader("X-Requested-With", "XMLHttpRequest");
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
