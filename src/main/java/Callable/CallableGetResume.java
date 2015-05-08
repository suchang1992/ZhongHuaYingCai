package Callable;

import Pojo.KeyWord;
import Pojo.Resume;
import Utils.MongoHelper;
import Utils.ZhongHuaYingCaiLogin;
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
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2015/4/29.
 */
public class CallableGetResume implements Callable {
    private static Logger logger = Logger.getLogger(CallableGetResume.class.getName());
    ZhongHuaYingCaiLogin zhongHuaYingCai;
    KeyWord keyWord;
    int pageNum;
    int delay;

    public CallableGetResume(ZhongHuaYingCaiLogin zhongHuaYingCai, KeyWord keyWord, int pageNum, int delay) {
        this.zhongHuaYingCai = zhongHuaYingCai;
        this.keyWord = keyWord;
        this.pageNum = pageNum;
        this.delay = delay;
    }

    @Override
    public Object call() throws Exception {
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
        formData.put("page", "" + pageNum);
        String url = "http://www.chinahr.com/modules/jmw/SocketAjax.php?m=hmresume&f=resume&action=myresume&list_type=search&usetoken=1";
        logger.info(keyWord.getSecondlevel() + ":第" + pageNum + "页爬取开始");
        String s = doPostToGetList(url, formData, zhongHuaYingCai.getHeaderString());
        int maxPostCount = 3;
        if (s.length() < 3 && maxPostCount-- > 0) {
            s = doPostToGetList(url, formData, zhongHuaYingCai.getHeaderString());
        }


        JSONObject jsonObject = null;
        try {
            jsonObject = JSON.parseObject(s);
        } catch (JSONException e) {
            logger.error("page:" + s);
            return null;
        }
        JSONArray jsonArray = jsonObject.getJSONObject("res").getJSONArray("resumeList");
        Iterator<Object> iterator = jsonArray.iterator();
        while (iterator.hasNext()) {
            JSONObject obj = (JSONObject) iterator.next();
            Resume resume = getResumeDetil(obj, keyWord, zhongHuaYingCai);
            if (resume != null)
                new MongoHelper().upsertResumInfo(resume, keyWord);
        }
        logger.info(keyWord.getSecondlevel() + ":第" + pageNum + "页爬取完成");
        return null;
    }

    private Resume getResumeDetil(JSONObject obj, KeyWord keyWord, ZhongHuaYingCaiLogin zhongHuaYingCai) {
        String resumeID = obj.getString("cvId");

        Resume resume = new Resume(resumeID);
        resume.setKeyWord(keyWord);
        resume.setSimpleResume(obj);
        //
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

        //获取详细简历信息
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
        String referer = "http://www.chinahr.com/modules/hmresume/index.php?c=preview&m=view_waterfall&ids="+resumeID+"|"+resumeID+"&keyword=%5Bnull%5D";
        String s = getResumeDetil(url, formData, cookie + "kw=" + keyword, referer);
        int maxPostCount = 3;
        if (s.length() <= 3 && maxPostCount-- > 0) {
            s = getResumeDetil(url, formData, cookie + "kw=" + keyword, referer);
        }
        return s;
    }

    private String getResumeDetil(String url, HashMap<String, String> formData, String cookie, String referer) {
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(url);
        installFormData(formData, request);
        request.addHeader("Cookie", cookie);
        request.addHeader("Accept", "application/json, text/javascript, */*; q=0.01");
        request.addHeader("Connection", "keep-alive");
        request.addHeader("Host", "www.chinahr.com");
        request.addHeader("Origin", "http://www.chinahr.com");
        request.addHeader("X-Requested-With", "XMLHttpRequest");
        request.addHeader("Referer",referer);
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
