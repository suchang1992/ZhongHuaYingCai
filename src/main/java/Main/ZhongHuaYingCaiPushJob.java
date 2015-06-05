package Main;

import Pojo.Job.ZhongHuaYingCaiJob;
import Pojo.StatuCodes;
import Utils.ZhongHuaYingCaiLogin;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2015/6/2.
 */
public class ZhongHuaYingCaiPushJob {

    private static final String push_new_job_url = "http://www.chinahr.com/modules/hmrecruit/index.php?c=managejob&m=insert_hrm&noblock=1";
    private static final String online_job_list_url = "http://www.chinahr.com/modules/hmrecruit/?c=job_list&new=managejob&src=managejob";

    public static void main(String[] args) {

//        String[] values = {/* oldWorkPlace */ "27,312",/* oldJobName */ "",/* oldClassify */ "",/* company_id */ "",
//        /* comName */ "",/* jobId */ "",/* jobName */ "全栈工程师",/* workPlace */ "27,312",
//        /* jobType */ "1001,1004,1053;1001,1004,1056;1001,1004,1059",/* endTime */ "2015-07-28",
//        /* depmId */ "33dcae848afdd6531da23327j",/* number */ "",/* workType */ "1",
//        /* jobDesc */ "岗位职责：全栈工程师",/* degId */ "5",/* degAbove */ "1",/* minAge */ "",/* maxAge */ "",
//        /* gender */ "N",/* expId */ "3",/* expAbove */ "1",/* driverSkill */ "0",
//        /* langSkills[typeId][] */ "0",
//        /* langSkills[langId][] */ "0",
//        /* langSkills[levelId][] */ "0",/* condition */ "",/* minSalary */ "3333",/* maxSalary */ "4444",
//        /* isNegotiate */ "0",/* benefits */ "包住宿",/* upComContact */ "0",/* contact */ "肖琴",
//        /* jobEmail[] */ "hr@cdecube.com",/* email[] */ "resume_test@qq.com",/* mobile[] */ "",
//        /* phoneArea[] */ "028",/* phoneNo[] */ "61837805",/* phoneExt[] */ "",/* faxArea[] */ "",
//        /* faxNo[] */ "",/* faxExt[] */ "",/* ivAddr */ "成都市高新西区合作路89号龙湖时代天街19栋0914",
//        /* zipCode */ "",/* markerId */ "",/* markerLat */ "",/* markerLng */ "",/* markerDetail */ "",/* cal */ "",
//        /* isAutoRep */ "0",/* isSendApp */ "0",/* isSendCS */ "0",/* isSendSys */ "0",/* appEmail[] */ "",
//        /* csEmail[] */ "",/* sysEmail[] */ "",/* insertJobPoints */ "1",/* classify */ "1"};

         String[] values = {
         /* oldWorkPlace */ "27,312",
         /* oldJobName */ "",
         /* oldClassify */ "",
         /* company_id */ "",
         /* comName */ "",
         /* jobId */ "",
         /* jobName */ "运维工程师",
         /* workPlace */ "27,312",
         /* jobType */ "1001,1005,1063;1001,1005,1062",
         /* endTime */ "2015-08-04",
         /* depmId */ "33dcae848afdd6531da23327j",
         /* number */ "",
         /* workType */ "1",
         /* jobDesc */ "1. 主要负责 数据清洗、整合和加工\n" +
                 "2. 参与爬虫架构设计和开发\n" +
                 "3. 协助进行运维事务\n",
         /* degId */ "5",
         /* degAbove */ "1",
         /* minAge */ "",
         /* maxAge */ "",
         /* gender */ "N",
         /* expId */ "3",
         /* expAbove */ "1",
         /* driverSkill */ "0",
        /* langSkills[typeId][] */ "0",
        /* langSkills[langId][] */ "0",
        /* langSkills[levelId][] */ "0",
        /* condition */ "1. 熟悉Java或者Python\n" +
                 "2. 熟悉Json、html、xml结构\n" +
                 "3. 能熟练操作linux系统，了解shell编程，\n" +
                 "4. 有较强动手能力，熟悉各种硬件相关的安装、配置（网卡、硬盘、路由、交换机等）\n" +
                 "5. 有较强的逻辑思维能力，拥有良好的沟通技巧、团队协作精神\n",
        /* minSalary */ "4000",
        /* maxSalary */ "6000",
        /* isNegotiate */ "0",
        /* benefits */ "",
        /* upComContact */ "0",
        /* contact */ "林小姐",
        /* jobEmail[] */ "xiaohua.lin@hirebigdata.cn",
        /* email[] */ "chang.su@hirebigdata.cn",
        /* mobile[] */ "",
        /* phoneArea[] */ "028",
        /* phoneNo[] */ "87860519",
        /* phoneExt[] */ "",
        /* faxArea[] */ "",
        /* faxNo[] */ "",
        /* faxExt[] */ "",
        /* ivAddr */ "成都市高新西区合作路89号龙湖时代天街19栋0916",
        /* zipCode */ "",
        /* markerId */ "",
        /* markerLat */ "",
        /* markerLng */ "",
        /* markerDetail */ "",
        /* cal */ "",
        /* isAutoRep */ "0",
        /* isSendApp */ "0",
        /* isSendCS */ "0",
        /* isSendSys */ "0",
        /* appEmail[] */ "",
        /* csEmail[] */ "",
        /* sysEmail[] */ "",
        /* insertJobPoints */ "1",
        /* classify */ "1"};
        ZhongHuaYingCaiPushJob pushJob = new ZhongHuaYingCaiPushJob();
        try {
            pushJob.publishNewJob("vipcdylf","longhu123", values);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *接口
     * @param username
     * @param password
     * @param values
     * @return
     * @throws Exception
     */
    public ZhongHuaYingCaiJob publishNewJob(String username, String password, String[] values) throws Exception{
        ZhongHuaYingCaiLogin zhongHuaYingCai = new ZhongHuaYingCaiLogin();
        zhongHuaYingCai.login(username,password);
        zhongHuaYingCai.loginRedirect();
        ZhongHuaYingCaiJob zhongHuaYingCaiJob = new ZhongHuaYingCaiJob();
        //验证是否登陆成功
        String s = zhongHuaYingCai.testLogin("http://www.chinahr.com/modules/feedback/index.php?m=userinfo&noblock=1",zhongHuaYingCai.getHeaderString());
        //{"type":"","cs_tel":"400-706-4000"}  //登陆失败
        //{"type":"hrmanagers","name":"\u6210\u90fd\u6613\u7acb\u65b9\u4fe1\u606f\u6280\u672f\u6709\u9650\u516c\u53f8","tel":"028-61837805","email":"hr@cdecube.com","cs_tel":"400-706-4000"} //登陆成功
        JSONObject jsonObject = JSONObject.parseObject(s);
        if(jsonObject.getString("type").equals("")){
            zhongHuaYingCaiJob.setCode(StatuCodes.LOGIN_ERROR_CODE);
            return zhongHuaYingCaiJob;
        }
        //判断职位个数
        s = zhongHuaYingCai.testLogin("http://www.chinahr.com/modules/hmcompanyx/index.php?c=home&m=statistics&noblock=1",zhongHuaYingCai.getHeaderString());
//        System.out.println(s);//{"jobCount":{"invite":2,"pause":5,"archive":25},"cvCount":{"accept":1,"system":0,"recommend":0},"accountInfo":{"account":{"status":1,"endTime":1433779199},"limit":{"onLineJobs":-1,"useOnLineJobs":2,"monthDownCVs":-1,"useMonthDownCVs":4,"monthDownCVsRemain":-1,"subAccounts":0,"createSubAccounts":0,"currentSubAccounts":0},"point":{"validCommJobPoints":5,"validAreaJobPoints":0,"validCommCVPoints":46,"validAreaCVPoints":0,"validGoldJobPoints":0,"validSMSPoints":0,"validTempPoints":0,"validCustPageDays":0,"validSonNum":0,"validUniversalPoints":0}}}
        jsonObject = JSONObject.parseObject(s);
        int ramin_count = jsonObject.getJSONObject("point").getIntValue("validCommJobPoints");
        if (ramin_count <= 0 ){
            zhongHuaYingCaiJob.setCode(StatuCodes.JOB_COUNT_NOT_ENOUGH);
            return zhongHuaYingCaiJob;
        }
        publishNewJob(zhongHuaYingCai, values, zhongHuaYingCaiJob);
        return zhongHuaYingCaiJob;
    }

    /**
     * post完成提交 并返回Job相关信息
     * @param zhongHuaYingCai
     * @param values
     * @return
     */
    private ZhongHuaYingCaiJob publishNewJob(ZhongHuaYingCaiLogin zhongHuaYingCai, String[] values, ZhongHuaYingCaiJob zhongHuaYingCaiJob){
        zhongHuaYingCaiJob.setValues(values);
        HttpPost postNewJob = new HttpPost(push_new_job_url);
        List<NameValuePair> params = new ArrayList<>(2);
        String[] keys = {"oldWorkPlace", "oldJobName", "oldClassify", "company_id", "comName", "jobId", "jobName",
                "workPlace", "jobType", "endTime", "depmId", "number", "workType", "jobDesc", "degId", "degAbove",
                "minAge", "maxAge", "gender", "expId", "expAbove", "driverSkill",
                "langSkills[typeId][]", "langSkills[langId][]", "langSkills[levelId][]", "condition", "minSalary",
                "maxSalary", "isNegotiate", "benefits", "upComContact", "contact", "jobEmail[]", "email[]", "mobile[]",
                "phoneArea[]", "phoneNo[]", "phoneExt[]", "faxArea[]", "faxNo[]", "faxExt[]", "ivAddr", "zipCode",
                "markerId", "markerLat", "markerLng", "markerDetail", "cal", "isAutoRep", "isSendApp", "isSendCS",
                "isSendSys", "appEmail[]", "csEmail[]", "sysEmail[]", "insertJobPoints", "classify"};
        for(int i=0; i< keys.length; i++){
            params.add(new BasicNameValuePair(keys[i], values[i]));
        }
        try {
            postNewJob.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
        } catch (UnsupportedEncodingException ue) {
            ue.printStackTrace();
        }
        HttpResponse postResponse = null;
        try {
            postResponse = zhongHuaYingCai.getHttpclient().execute(postNewJob);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        System.out.println(ZhongHuaYingCaiLogin.getHtml(postResponse));
        try {
            JSONObject jsonObject = JSONObject.parseObject(ZhongHuaYingCaiLogin.getHtml(postResponse));
            postNewJob.releaseConnection();
            if(jsonObject.getBooleanValue("success")) {//{"success":true,"url":"\/modules\/hmrecruit\/index.php?c=job_list&classify=1","msgNo":0,"msg":"","title":"\u53d1\u5e03\u6210\u529f"}
                getNewJobId(zhongHuaYingCai, zhongHuaYingCaiJob, values[6]);
                return zhongHuaYingCaiJob;
            }
        }catch (JSONException e){
            zhongHuaYingCaiJob.setCode(StatuCodes.GET_JOB_ID_JSON_ERROR);
            return zhongHuaYingCaiJob;
        }
        zhongHuaYingCaiJob.setCode(StatuCodes.PUSH_JOB_ERROR_CODE);
        return zhongHuaYingCaiJob;
    }

    public boolean getNewJobId(ZhongHuaYingCaiLogin zhongHuaYingCai, ZhongHuaYingCaiJob zhongHuaYingCaiJob, String jobName){
        HttpGet httpget = new HttpGet(online_job_list_url);
        try {
            HttpResponse getResponse = zhongHuaYingCai.getHttpclient().execute(httpget);
            Document doc = Jsoup.parse(ZhongHuaYingCaiLogin.getHtml(getResponse));
            Element first_job_info = doc.getElementsByAttributeValue("class", "jobListItem").first();
            String job_name = first_job_info.getElementsByAttributeValue("class","fl jobName jobName2 alrTxtLef").first().child(0).attr("title");
            String job_time = first_job_info.getElementsByAttributeValue("class","fl timeEfc").first().text();
            if(job_name.equals(jobName) && new SimpleDateFormat("yyyy-MM-dd").format(new Date()).equals(job_time)) {
                zhongHuaYingCaiJob.setJobId(first_job_info.attr("rel"));
                zhongHuaYingCaiJob.setCode(StatuCodes.PUSH_JOB_SUCCESS_CODE);
                return true;
            }else {
                zhongHuaYingCaiJob.setCode(StatuCodes.GET_JOB_ID_ERROR);
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
