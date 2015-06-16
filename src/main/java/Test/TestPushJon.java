package Test;

import Main.ZhongHuaYingCaiPushJob;
import Pojo.Job.ZhongHuaYingCaiJob;
import Utils.ZhongHuaYingCaiLogin;

import java.io.IOException;

/**
 * Created by Administrator on 2015/6/4.
 */
public class TestPushJon {
    public static void main(String[] args) {
        ZhongHuaYingCaiLogin zhongHuaYingCai = new ZhongHuaYingCaiLogin();
        try {
            zhongHuaYingCai.login("vipcdylf","longhu123");
            zhongHuaYingCai.loginRedirect();
            ZhongHuaYingCaiPushJob zhongHuaYingCaiPushJob = new ZhongHuaYingCaiPushJob();
            ZhongHuaYingCaiJob zhongHuaYingCaiJob = new ZhongHuaYingCaiJob();
            zhongHuaYingCaiPushJob.getNewJobId(zhongHuaYingCai, zhongHuaYingCaiJob, "网络爬虫工程师");
            System.out.println(zhongHuaYingCaiJob);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
