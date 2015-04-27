package Utils;

import Pojo.PageInfo;
import Pojo.Resume;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2015/4/23.
 */
public class ResumeGet {

    String cookie = null;

    public ArrayList<Resume> SearchResume(String keyword, String name, String password) {
        ArrayList<Resume> resumes = new ArrayList<>();
        ZhongHuaYingCaiLogin zhongHuaYingCai = new ZhongHuaYingCaiLogin();
        String s = null;
        try {
            //登陆
            zhongHuaYingCai.login(name, password);
            zhongHuaYingCai.loginRedirect();

            //准备post参数，确认关键词和页数
            HashMap<String, String> formData = new HashMap<>();

            formData.put("flag", "1");
            formData.put("keywordSelect1", "0");
            formData.put("fuzzyWishPlace", "1");
            formData.put("matchLevel", "1,2");
            formData.put("searcherCount", "0");
            formData.put("used", "0");
            formData.put("allKeyword", "0");
            formData.put("allKeyword2", "0");
            formData.put("keyword", keyword);
            formData.put("keywordSelect", "0");
//            formData.put("page", "2");
            String url = "http://www.chinahr.com/modules/jmw/SocketAjax.php?m=hmresume&f=resume&action=myresume&list_type=search&usetoken=1";
            s = doPost(url, formData, zhongHuaYingCai.headerString);
            JSONObject jsonObject = JSON.parseObject(s);
            PageInfo pageInfo = new PageInfo(jsonObject.getJSONObject("res").getJSONObject("page"));
            JSONArray jsonArray = jsonObject.getJSONObject("res").getJSONArray("resumeList");
            Iterator<Object> iterator = jsonArray.iterator();
            while (iterator.hasNext()) {
                JSONObject obj = (JSONObject) iterator.next();
                String resumeID = obj.getString("cvId");
                Resume resume = new Resume(resumeID);
                resume.setSimpleResume(obj.toJSONString());
                s = getResumeWithNotLogin(resumeID, keyword, zhongHuaYingCai.headerString);
                resume.setResumeDetil(s);
                resumes.add(resume);
            }
            if(pageInfo.getMaxPageNum()>1){
                for (int i = 2; i<= 8; i++){
                    formData.put("page",""+i);
                    url = "http://www.chinahr.com/modules/jmw/SocketAjax.php?m=hmresume&f=resume&action=myresume&list_type=search&usetoken=1";
                    s = doPost(url, formData, zhongHuaYingCai.headerString);
                    jsonObject = JSON.parseObject(s);
                    jsonArray = jsonObject.getJSONObject("res").getJSONArray("resumeList");
                    iterator = jsonArray.iterator();
                    while (iterator.hasNext()) {
                        JSONObject obj = (JSONObject) iterator.next();
                        String resumeID = obj.getString("cvId");
                        Resume resume = new Resume(resumeID);
                        resume.setSimpleResume(obj.toJSONString());
                        s = getResumeWithNotLogin(resumeID, keyword, zhongHuaYingCai.headerString);
                        resume.setResumeDetil(s);
                        resumes.add(resume);
                    }
                }
            }

            return resumes;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    private String getResumeWithNotLogin(String resumeID, String keyword, String cookie) {
        HashMap<String, String> formData = new HashMap<>();
        formData.put("uid", "");
        formData.put("cvId", resumeID);
        formData.put("hr1", "1");
        formData.put("pr1", "2");
        formData.put("pr2", "2");
        formData.put("hm1", "400");
        formData.put("hm2", "1800");
        formData.put("keyword[]", keyword);
        formData.put("iterator", "0");

        String url = "http://www.chinahr.com/modules/jmw/SocketAjax.php?m=hmresume&f=getresume&tp=4";
        String s = doPost(url, formData, cookie + "kw=" + keyword);
//        System.out.println(s);
        return s;
    }

    public void getRusumeByResumeID(String resumeID) {

    }

    public static String doPost(String url, HashMap<String, String> formData, String pageCookie) {
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(url);
        installFormData(formData, request);
        request.addHeader("Cookie", pageCookie);
        request.addHeader("Host", "www.chinahr.com");
        request.addHeader("X-Requested-With", "XMLHttpRequest");
        request.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.118 Safari/537.36");
        try {
            HttpResponse response = client.execute(request);
//            System.out.println(response.toString());
            return getHtml(response);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String doGet(String url, String pageCookie) {
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        try {
            request.addHeader("Cookie", pageCookie);
            HttpResponse response = client.execute(request);
            return getHtml(response);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
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

    public static void main(String[] args) {
        new ResumeGet().SearchResume("java", "vipcdylf", "longhu123");
    }
}
