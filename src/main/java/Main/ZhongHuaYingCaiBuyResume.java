package Main;

import Pojo.Job.YingCaiContact;
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

/**
 * Created by Administrator on 2015/6/3.
 */
public class ZhongHuaYingCaiBuyResume {

    private static final String postUrl = "http://www.chinahr.com/modules/hmresume/?c=buy&noblock=1";
    private static final String count_ramin_url = "http://www.chinahr.com/modules/hmresume/index.php?c=preview&m=getresume_remain&noblock=1";

    HttpClient httpclient = null;


    public static void main(String[] args) {
        String resumeId = "c49cae84fb1c10555171f504j";//成都的  msg:100020 正确
//        String resumeId = "2c6aae8473ce675523106946j"; //msg:110012 错误
        ZhongHuaYingCaiBuyResume zhongHuaYingCaiBuyResume = new ZhongHuaYingCaiBuyResume();
        YingCaiContact yingCaiContact = zhongHuaYingCaiBuyResume.buyResume("vipcdylf", "longhu123", resumeId);
        System.out.println(yingCaiContact);

    }

    /**
     * @param name
     * @param password
     * @param resume_id
     * @return
     */
    public YingCaiContact buyResume(String name, String password, String resume_id) {
        ZhongHuaYingCaiLogin zhongHuaYingCai = new ZhongHuaYingCaiLogin();
        try {
            zhongHuaYingCai.login(name, password);
            zhongHuaYingCai.loginRedirect();
            YingCaiContact yingCaiContact = new YingCaiContact();
            //验证是否登陆成功
            String s = zhongHuaYingCai.testLogin("http://www.chinahr.com/modules/feedback/index.php?m=userinfo&noblock=1", zhongHuaYingCai.getHeaderString());
            //{"type":"","cs_tel":"400-706-4000"}  //登陆失败
            //{"type":"hrmanagers","name":"\u6210\u90fd\u6613\u7acb\u65b9\u4fe1\u606f\u6280\u672f\u6709\u9650\u516c\u53f8","tel":"028-61837805","email":"hr@cdecube.com","cs_tel":"400-706-4000"} //登陆成功
            JSONObject jsonObject = JSONObject.parseObject(s);
            if (jsonObject.getString("type").equals("")) {
                yingCaiContact.setCode(StatuCodes.LOGIN_ERROR_CODE);
                return yingCaiContact;
            }
            int ramin_count = getResumeCanbeBuyCount(zhongHuaYingCai.getHeaderString());
            if (ramin_count == 0) {
                yingCaiContact.setCode(StatuCodes.RESUME_COUNT_NOT_ENOUGH);
                return yingCaiContact;
            }

            HashMap<String, String> formData = new HashMap<String, String>();
            formData.put("jobAppId", "");
            formData.put("cvId", resume_id);
            formData.put("src", "4");
            s = buyResume(formData, zhongHuaYingCai.getHeaderString());
            yingCaiContact.setMsg(s);
//            System.out.println(s);//{"msg":100020,"res":{"jsName":"\u5b89\u822a","phone":"13438011015","email":"19299168@163.com","emailShort":"19299168@16\u2026","emailLong":"19299168@163.com","jobAppId":"1db3ae84744271550551aa48j","jobId":null,"jobName":null,"remain_points":null,"tips_type":1},"json":null}
            JSONObject jso = JSONObject.parseObject(s);
            int msg = jso.getIntValue("msg");
            if(msg==100020){
                yingCaiContact.setCode(StatuCodes.GET_RESUME_CONTACT_SUCCESS_CODE);
            }else if(msg == 110012){
                yingCaiContact.setCode(StatuCodes.RESUME_ARE_ERROR);
            }else {
                yingCaiContact.setCode(StatuCodes.GET_RESUME_CONTACT_ERROR);
            }
            return yingCaiContact;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取剩余可购买数
     *
     * @param cookie
     * @return
     */
    public int getResumeCanbeBuyCount(String cookie) {
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(count_ramin_url);
        request.addHeader("Cookie", cookie);
        request.addHeader("Accept", "application/json, text/javascript, */*; q=0.01");
        request.addHeader("Connection", "keep-alive");
        request.addHeader("Host", "www.chinahr.com");
        request.addHeader("Origin", "http://www.chinahr.com");
        request.addHeader("X-Requested-With", "XMLHttpRequest");
//        request.addHeader("Referer", "http://www.chinahr.com/modules/hmresume/index.php?c=searchx&m=result");
        request.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.118 Safari/537.36");

        try {
            HttpResponse response = client.execute(request);
            return Integer.parseInt(getHtml(response));
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    private String buyResume(HashMap<String, String> formData, String cookie) {
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(postUrl);
        installFormData(formData, request);
        request.addHeader("Cookie", cookie);
        request.addHeader("Accept", "application/json, text/javascript, */*; q=0.01");
        request.addHeader("Connection", "keep-alive");
        request.addHeader("Host", "www.chinahr.com");
        request.addHeader("Origin", "http://www.chinahr.com");
        request.addHeader("X-Requested-With", "XMLHttpRequest");
//        request.addHeader("Referer", "http://www.chinahr.com/modules/hmresume/index.php?c=searchx&m=result");
        request.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.118 Safari/537.36");
        try {
            HttpResponse response = client.execute(request);
            return getHtml(response);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public String getHtml(HttpResponse response) {
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

    public void installFormData(HashMap<String, String> parameter, HttpPost request) {
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
