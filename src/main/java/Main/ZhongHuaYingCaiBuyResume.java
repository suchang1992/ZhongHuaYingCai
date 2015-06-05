package Main;

import Utils.ZhongHuaYingCaiLogin;
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
        String resumeId = "e995ae84a717ba530e3f4a14j";//成都的  msg:100020 正确
//        String resumeId = "2c6aae8473ce675523106946j"; //msg:110012 错误
        ZhongHuaYingCaiLogin zhongHuaYingCai = new ZhongHuaYingCaiLogin();
        try {
            zhongHuaYingCai.login("vipcdylf", "longhu123");
            zhongHuaYingCai.loginRedirect();
//            HashMap<String, String> formData = new HashMap<>();
//            formData.put("jobAppId", "");
//            formData.put("cvId", resumeId);
//            formData.put("src", "4");
            ZhongHuaYingCaiBuyResume zhongHuaYingCaiBuyResume = new ZhongHuaYingCaiBuyResume();
//
//            String s = zhongHuaYingCaiBuyResume.buyResume(formData,zhongHuaYingCai.getHeaderString());
//            System.out.println(s);
            System.out.println(zhongHuaYingCaiBuyResume.getResumeCanbeBuyCount(zhongHuaYingCai.getHeaderString()));

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     *
     * @param name
     * @param password
     * @param resume_id
     * @return  错误返回null 正确返回json （json中msg代表状态码）
     */
    public String buyResume(String name, String password, String resume_id){
        ZhongHuaYingCaiLogin zhongHuaYingCai = new ZhongHuaYingCaiLogin();
        try {
            zhongHuaYingCai.login(name, password);
            zhongHuaYingCai.loginRedirect();

            int ramin_count = getResumeCanbeBuyCount(zhongHuaYingCai.getHeaderString());
            if(ramin_count==0)
                return "";

            HashMap<String, String> formData = new HashMap<>();
            formData.put("jobAppId", "");
            formData.put("cvId", resume_id);
            formData.put("src", "4");
            ZhongHuaYingCaiBuyResume zhongHuaYingCaiBuyResume = new ZhongHuaYingCaiBuyResume();
            String s = zhongHuaYingCaiBuyResume.buyResume(formData,zhongHuaYingCai.getHeaderString());
//            System.out.println(s);
            return s;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取剩余可购买数
     * @param cookie
     * @return
     */
    public int getResumeCanbeBuyCount(String cookie){
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


    private String buyResume(HashMap<String, String> formData, String cookie){
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
