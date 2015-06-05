package Main;

import Pojo.Job.JobResume;
import Pojo.Job.ZhongHuaYingCaiGetContact;
import Pojo.Job.ZhongHuaYingCaiJobResume;
import Pojo.StatuCodes;
import Utils.ZhongHuaYingCaiLogin;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2015/6/4.
 */
public class ZhongHuaYingCaiPullJobResume {
    private static final String job_list_url = "http://www.chinahr.com/modules/jmw/SocketAjax.php?m=hmresume&f=resume&action=myresume&list_type=&usetoken=1";
//    src:1
//    jobId:b394ae84ecfb6f5540dede5fj
//    jobName:运维工程师
//    flag:1
//    matchLevel:1,2

    public static void main(String[] args) {
        ZhongHuaYingCaiPullJobResume zhongHuaYingCaiPullJobResume = new ZhongHuaYingCaiPullJobResume();
        try {
            ZhongHuaYingCaiJobResume resumes = zhongHuaYingCaiPullJobResume.pullResume("vipcdylf", "longhu123", "b394ae84ecfb6f5540dede5fj", "java");
            ArrayList<JobResume> resumeIds = resumes.getResumeIds();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public ZhongHuaYingCaiJobResume pullResume(String username, String password, String job_id, String job_name) throws IOException {
        ZhongHuaYingCaiJobResume jonResume = new ZhongHuaYingCaiJobResume();
        jonResume.setJobId(job_id);
        ZhongHuaYingCaiLogin zhongHuaYingCai = new ZhongHuaYingCaiLogin();
        zhongHuaYingCai.login(username, password);
        zhongHuaYingCai.loginRedirect();
        //验证是否登陆成功
        String s = zhongHuaYingCai.testLogin("http://www.chinahr.com/modules/feedback/index.php?m=userinfo&noblock=1", zhongHuaYingCai.getHeaderString());
        //{"type":"","cs_tel":"400-706-4000"}  //登陆失败
        //{"type":"hrmanagers","name":"\u6210\u90fd\u6613\u7acb\u65b9\u4fe1\u606f\u6280\u672f\u6709\u9650\u516c\u53f8","tel":"028-61837805","email":"hr@cdecube.com","cs_tel":"400-706-4000"} //登陆成功
        JSONObject jsonObject = JSONObject.parseObject(s);
        if (jsonObject.getString("type").equals("")) {
            jonResume.setCode(StatuCodes.LOGIN_ERROR_CODE);
            return jonResume;
        }
        pullResume(zhongHuaYingCai, jonResume, job_id, job_name);

        return jonResume;
    }

    private void pullResume(ZhongHuaYingCaiLogin zhongHuaYingCai, ZhongHuaYingCaiJobResume jonResume, String job_id, String job_name) throws IOException {

        String json = getJobListJson(zhongHuaYingCai, job_id, job_name, "1");
        try {
            JSONObject jsonObject = JSONObject.parseObject(json);
            //如果resumeid错误 resumeCount = 0
            JSONObject page = jsonObject.getJSONObject("res").getJSONObject("page");
            int resumeCount = page.getIntValue("total");
            jonResume.setResumeCount(resumeCount);
            JSONArray jsonArray = jsonObject.getJSONObject("res").getJSONArray("resumeList");
            addJobResume(jsonArray, jonResume);
            for (int i = 2; i <= (int)Math.ceil(resumeCount / 15.0); i++) {
                json = getJobListJson(zhongHuaYingCai, job_id, job_name, "" + i);
                jsonObject = JSONObject.parseObject(json);
                jsonArray = jsonObject.getJSONObject("res").getJSONArray("resumeList");
                addJobResume(jsonArray, jonResume);
            }
        } catch (JSONException e) {
            jonResume.setCode(StatuCodes.STRING_TO_JSON_ERROR);
            return;
        }
    }

    private String getJobListJson(ZhongHuaYingCaiLogin zhongHuaYingCai, String job_id, String job_name, String page) throws IOException {
        HttpPost pullJobresume = new HttpPost(job_list_url);
        List<NameValuePair> params = new ArrayList<NameValuePair>(2);
        params.add(new BasicNameValuePair("src", "1"));
        params.add(new BasicNameValuePair("jobId", job_id));
        params.add(new BasicNameValuePair("jobName", job_name));
        params.add(new BasicNameValuePair("flag", "1"));
        params.add(new BasicNameValuePair("matchLevel", "1,2"));
        params.add(new BasicNameValuePair("page", page));
        pullJobresume.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
        HttpResponse postResponse = zhongHuaYingCai.getHttpclient().execute(pullJobresume);
        String json = ZhongHuaYingCaiLogin.getHtml(postResponse);
        pullJobresume.releaseConnection();
        return json;
    }


    private void addJobResume(JSONArray jsonArray, ZhongHuaYingCaiJobResume jonResume) {
        Iterator<Object> iterator = jsonArray.iterator();
        while (iterator.hasNext()) {
            JSONObject resume = (JSONObject) iterator.next();
            jonResume.getResumeIds().add(new JobResume(resume.getString("cvId"), resume.getString("addTime")));
        }
    }

}
