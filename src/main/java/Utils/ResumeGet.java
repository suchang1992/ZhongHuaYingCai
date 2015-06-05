package Utils;

import Pojo.KeyWord;
import Pojo.PageInfo;
import Pojo.Resume;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
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

/**
 * Created by Administrator on 2015/4/23.
 */
public class ResumeGet {
    private static Logger logger = Logger.getLogger(ResumeGet.class.getName());

    /**
     * 根据关键词搜索
     *
     * @param keyWord 包含 主关键词 子关键词 关键词坐标
     * @return 返回能搜索到的所有简历
     */
    public ArrayList<Resume> SearchResumeByKeyword(KeyWord keyWord, String name, String password) {
        //ArrayList<Resume> resumes = new ArrayList<>();
        String s = null;
        ZhongHuaYingCaiLogin zhongHuaYingCai = new ZhongHuaYingCaiLogin();
        //登陆
        try {
            zhongHuaYingCai.login(name, password);
            zhongHuaYingCai.loginRedirect();
            logger.info("验证是否登陆成功");
            s = zhongHuaYingCai.testLogin("http://www.chinahr.com/modules/hmcompanyx/?new=index&src=searchx", zhongHuaYingCai.getHeaderString());
            if (s.length()>1000)
                logger.info(keyWord.getSecondlevel() + ":开始爬取 登陆完成");
            else {
                logger.error(s);
                logger.error("登陆失败");
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("登陆失败");
        }

        logger.info(keyWord.getSecondlevel() + ":第1页爬取开始");
        //爬取第一页列表
        s = getResumeListPage(keyWord, zhongHuaYingCai, 1);
        logger.error("page string:" + s.length());
        if(s.length() < 1000) {
            logger.error(s);
            return null;
        }
        JSONObject jsonObject = JSON.parseObject(s);
        PageInfo pageInfo = new PageInfo(jsonObject.getJSONObject("res").getJSONObject("page"));
        logger.info(keyWord.getSecondlevel() + ":共有" + pageInfo.getMaxPageNum() + "页");
        JSONArray jsonArray = jsonObject.getJSONObject("res").getJSONArray("resumeList");
        Iterator<Object> iterator = jsonArray.iterator();
        while (iterator.hasNext()) {
            JSONObject obj = (JSONObject) iterator.next();
            Resume resume = getResumeDetil(obj, keyWord, zhongHuaYingCai);
            if (resume != null)
                new MongoHelper().upsertResumInfo(resume, keyWord);
        }
        logger.info(keyWord.getSecondlevel() + ":第1页爬取完成");
        //如果大于1页 才访问后面的
        if (pageInfo.getMaxPageNum() > 1) {
            int pageNum = pageInfo.getMaxPageNum() > 8 ? 8 : pageInfo.getMaxPageNum();
            for (int i = 2; i <= pageNum; i++) {
                logger.info(keyWord.getSecondlevel() + ":第" + i + "页爬取开始");
                s = getResumeListPage(keyWord,zhongHuaYingCai,i);
                logger.error("page string:" + s.length());
                jsonObject = JSON.parseObject(s);
                jsonArray = jsonObject.getJSONObject("res").getJSONArray("resumeList");
                iterator = jsonArray.iterator();
                while (iterator.hasNext()) {
                    JSONObject obj = (JSONObject) iterator.next();
                    Resume resume = getResumeDetil(obj, keyWord, zhongHuaYingCai);
                    if (resume != null)
                        new MongoHelper().upsertResumInfo(resume, keyWord);
                }
                logger.info(keyWord.getSecondlevel() + ":第" + i + "页爬取完成");
            }
        }
        try {
            zhongHuaYingCai.logout();
            logger.info(keyWord.getSecondlevel()+":爬取完成 登出完成");
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("登出失败");
        }
        return null;
    }

    private Resume getResumeDetil(JSONObject obj, KeyWord keyWord, ZhongHuaYingCaiLogin zhongHuaYingCai){
        String resumeID = obj.getString("cvId");
        if(new MongoHelper().isInMongoSearchById(resumeID)){
            logger.info("skip:"+resumeID);
            return null;
        }
        Resume resume = new Resume(resumeID);
        resume.setKeyWord(keyWord);
        resume.setSimpleResume(obj);
        String s = getResumeWithNotLogin(resumeID, keyWord.getSecondlevel(), zhongHuaYingCai.headerString);
        if (s == null || s.equals("400")) {
            logger.info(resumeID+" response 为status 400 参数错误");
            return null;
        }
        logger.error(resumeID + ":detil:" + s);
        try {//如果转化失败 则返回空
            resume.setResumeDetil(JSONObject.parseObject(s));
        }catch (JSONException e){
            logger.error("error:"+s);
            return null;
        }
        return resume;
    }



    private String getResumeListPage(KeyWord keyWord, ZhongHuaYingCaiLogin zhongHuaYingCai, int pageNum){
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
        String s = doPost(url, formData, zhongHuaYingCai.headerString);
        if (s==null ||s.length()<2){
            s = doPost(url, formData, zhongHuaYingCai.headerString);
        }
        return s;
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
        formData.put("keyword[]", "null");
        formData.put("iterator", "0");

        String url = "http://www.chinahr.com/modules/jmw/SocketAjax.php?m=hmresume&f=getresume&tp=4";
        String s = doPost(url, formData, cookie + "kw=" + keyword);
        if(s==null ||s.length()<2){
            s = doPost(url, formData, cookie + "kw=" + keyword);
        }
        return s;
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
            if(response.getStatusLine().getStatusCode() == 400)
                return "400";
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
//        KeyWord keyWord = new KeyWord("物流", "物流", 0, 0);
//        new ResumeGet().SearchResumeByKeyword(keyWord, "vipcdylf", "longhu123");
    }
}
