package Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

public class ZhongHuaYingCaiLogin {
//    final static String LOGIN = "http://www.chinahr.com/modules/hmcompanyx/?c=login&m=chklogin";
    final static String LOGIN = "http://www.chinahr.com/modules/hmcompanyx/index.php?c=loginAjax&m=login&noblock=1";
    final static String SECEND_LOGIN_URL = "http://www.chinahr.com/modules/hmcompanyx/?c=home";
    final static String LOGOUT = "http://www.chinahr.com/modules/hmcompanyx/?c=logout";

    Header[] headers = null;
    HttpClient httpclient = null;
    String headerString = "";
    String location = "";

    public static void main(String arg[]) throws Exception {
        ZhongHuaYingCaiLogin zhongHuaYingCai = new ZhongHuaYingCaiLogin();
        //第一步post
        zhongHuaYingCai.login("vipcdylf", "longhu123");
//        System.out.println("登陆post完成");
        //第二步get
        zhongHuaYingCai.loginRedirect();
//        System.out.println("登陆get完成");
        //登出
        zhongHuaYingCai.logout();
		System.exit(0);
    }

    public void logout() throws ClientProtocolException, IOException {
        HttpGet httpLogout = new HttpGet(LOGOUT);
        httpLogout.setHeader("Referer", "http://www.chinahr.com/modules/hmrecruit/index.php?c=job_list&classify=0");
        httpLogout.setHeader("Origin", "http://www.chinahr.com");
        httpLogout.setHeader("Host", "www.chinahr.com");
        httpLogout.setHeader("Cookie", headerString);
        HttpResponse response1 = httpclient.execute(httpLogout);
//		System.out.println(response1.toString());
        httpLogout.releaseConnection();
    }



    public void login(String name, String password) throws ClientProtocolException, IOException {
        httpclient = HttpClients.createDefault();
        //设置浏览器参数
        HttpPost httppost = new HttpPost(LOGIN);
        httppost.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        httppost.setHeader("Accept-Encoding", "gzip,deflate,sdch");
        httppost.setHeader("Accept-Language", "en-US,en;q=0.8,zh-CN;q=0.6,zh;q=0.4");
        httppost.setHeader("Cache-Control", "max-age=0");
        httppost.setHeader("Connection", "keep-alive");
        httppost.setHeader("Referer", "http://www.chinahr.com");
        httppost.setHeader("Origin", "http://www.chinahr.com");
        httppost.setHeader("Host", "www.chinahr.com");
        httppost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/36.0.1985.125 Safari/537.36");
        //填写账号密码
        List<NameValuePair> params = new ArrayList<NameValuePair>(2);
//        params.add(new BasicNameValuePair("redirect", ""));
        params.add(new BasicNameValuePair("uname", name));
        params.add(new BasicNameValuePair("pass", password));
        httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
        HttpResponse postResponse = httpclient.execute(httppost);
//		System.out.println(postResponse.toString());
        httppost.releaseConnection();

        headers = postResponse.getHeaders("Set-Cookie");
        headerString += updateCookie(headers);
        if (postResponse.getStatusLine().getStatusCode() != 302) {
            return;
        }

//        Header header = postResponse.getHeaders("Location")[0];
//        location = header.getValue();
    }

    public void loginRedirect() throws ClientProtocolException, IOException {
        HttpGet httpget = new HttpGet(SECEND_LOGIN_URL);
        httpget.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        httpget.setHeader("Accept-Encoding", "gzip,deflate,sdch");
        httpget.setHeader("Accept-Language", "en-US,en;q=0.8,zh-CN;q=0.6,zh;q=0.4");
        httpget.setHeader("Cache-Control", "max-age=0");
        httpget.setHeader("Connection", "keep-alive");
        httpget.setHeader("Cookie", headerString);
        httpget.setHeader("Host", "www.chinahr.com");
        httpget.setHeader("Referer", "http://www.chinahr.com/modules/hmcompanyx/?c=login&http_referer=");
        HttpResponse getResponse = httpclient.execute(httpget);
//		System.out.println(getResponse.toString());
        System.out.println("登录成功");
        headers = getResponse.getHeaders("Set-Cookie");
        headerString += updateCookie(headers);
        httpget.releaseConnection();
    }


    public static String updateCookie(Header[] headers) {
        String headerString = "";
        for (Header h : headers) {
            String value = h.getValue();
            headerString += value+";";
        }
        return headerString;
    }
}
