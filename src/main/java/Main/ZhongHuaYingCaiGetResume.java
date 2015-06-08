package Main;

import Pojo.Job.YingCaiResume;
import Pojo.StatuCodes;
import Utils.ZhongHuaYingCaiLogin;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2015/6/5.
 */
public class ZhongHuaYingCaiGetResume {

    private static final String resume_get_url = "http://www.chinahr.com/modules/jmw/SocketAjax.php?m=hmresume&f=getresume&tp=4";

    public static void main(String[] args) throws IOException {

        YingCaiResume yingCaiResume = new ZhongHuaYingCaiGetResume().getResumeDetil("vipcdylf", "longhu123","e995ae84a717ba530e3f4a14j");
        System.out.println(yingCaiResume.getResume_string());
    }

    public YingCaiResume getResumeDetil(String username, String password,String resumeId) throws IOException {
        ZhongHuaYingCaiLogin zhongHuaYingCai = new ZhongHuaYingCaiLogin();
        zhongHuaYingCai.login(username, password);
        zhongHuaYingCai.loginRedirect();
        YingCaiResume yingCaiResume = new YingCaiResume();
        //验证是否登陆成功
        String s = zhongHuaYingCai.testLogin("http://www.chinahr.com/modules/feedback/index.php?m=userinfo&noblock=1", zhongHuaYingCai.getHeaderString());
        //{"type":"","cs_tel":"400-706-4000"}  //登陆失败
        //{"type":"hrmanagers","name":"\u6210\u90fd\u6613\u7acb\u65b9\u4fe1\u606f\u6280\u672f\u6709\u9650\u516c\u53f8","tel":"028-61837805","email":"hr@cdecube.com","cs_tel":"400-706-4000"} //登陆成功
        JSONObject jsonObject = JSONObject.parseObject(s);
        if (jsonObject.getString("type").equals("")) {
            yingCaiResume.setCode(StatuCodes.LOGIN_ERROR_CODE);
            return yingCaiResume;
        }
        HashMap<String, String> formData = new HashMap<String, String>();
        formData.put("uid", "");
        formData.put("cvId", resumeId);
        formData.put("hr1", "1");
        formData.put("pr1", "2");
        formData.put("pr2", "2");
        formData.put("hm1", "400");
        formData.put("hm2", "1800");
        formData.put("keyword[]", "null");
        formData.put("iterator", "0");

        s = getResumeDetil(resume_get_url, formData, zhongHuaYingCai.getHeaderString());
        int maxPostCount = 3;
        if (s.length() <= 3 && maxPostCount-- > 0) {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            s = getResumeDetil(resume_get_url, formData, zhongHuaYingCai.getHeaderString());
        }
        yingCaiResume.setResume_string(s);
        yingCaiResume.setCode(StatuCodes.GET_RESUME_SUCCESS_CODE);
        return yingCaiResume;
    }



    public String getResumeDetil(String resumeId, String cookie) {
        HashMap<String, String> formData = new HashMap<>();
        formData.put("uid", "");
        formData.put("cvId", resumeId);
        formData.put("hr1", "1");
        formData.put("pr1", "2");
        formData.put("pr2", "2");
        formData.put("hm1", "400");
        formData.put("hm2", "1800");
        formData.put("keyword[]", "null");
        formData.put("iterator", "0");

        String s = getResumeDetil(resume_get_url, formData, cookie);
        int maxPostCount = 3;
        if (s.length() <= 3 && maxPostCount-- > 0) {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            s = getResumeDetil(resume_get_url, formData, cookie);
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

    private void installFormData(HashMap<String, String> parameter, HttpPost request) {
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

    private String getHtml(HttpResponse response) {
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
}
