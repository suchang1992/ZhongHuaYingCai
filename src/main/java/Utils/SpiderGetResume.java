package Utils;

import Pojo.KeyWord;
import Pojo.Resume;
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
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2015/6/10.
 */
public class SpiderGetResume {
    private static Logger logger = Logger.getLogger(SpiderGetResume.class.getName());
    private static int delay = 1000;


    /**
     * 得到列表页
     * @param url
     * @param formData
     * @param cookie
     * @return
     */
    public static  String doPostToGetList(String url, HashMap<String, String> formData, String cookie) {
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(url);
        installFormData(formData, request);
        request.addHeader("Cookie", cookie);
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

    /**
     * 得到具体简历内容
     * @param obj
     * @param keyWord
     * @param cookie
     * @return
     */
    public static Resume getResumeDetil(JSONObject obj, KeyWord keyWord, String cookie, MongoDBHelper mongoDBHelper, int delay) {
        String resumeID = obj.getString("cvId");
        Resume resume = new Resume(resumeID);
        resume.setKeyWord(keyWord);
        resume.setSimpleResume(obj);
        if (mongoDBHelper.isInMongoSearchById(resumeID)) {
            logger.info("skip:" + resumeID);
            return resume;
        }
        try {
            logger.info("休息" + delay + "毫秒");
            TimeUnit.MILLISECONDS.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String s = getResumeDetil(resumeID, keyWord.getSecondlevel(), cookie);
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

    /**
     * 填写参数和重爬控制
     * @param resumeID
     * @param keyword
     * @param cookie
     * @return
     */
    private static String getResumeDetil(String resumeID, String keyword, String cookie) {
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

        String s = getResumeDetil(CommonParameter.resume_detil_url, formData, cookie);
        int maxPostCount = 3;
        if (s.length() <= 3 && maxPostCount-- > 0) {
            try {
                logger.error("重爬暂停2秒");
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            s = getResumeDetil(CommonParameter.resume_detil_url, formData, cookie);
        }
        return s;
    }

    /**
     * 具体实施爬取
     * @param url
     * @param formData
     * @param cookie
     * @return
     */
    private static String getResumeDetil(String url, HashMap<String, String> formData, String cookie) {
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
