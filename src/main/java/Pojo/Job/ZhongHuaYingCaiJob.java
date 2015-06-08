package Pojo.Job;

import Pojo.StatuCodes;

/**
 * Created by Administrator on 2015/6/3.
 */
public class ZhongHuaYingCaiJob {
    String[] values;
    String jobId;
    int code = 0;

    public String[] getValues() {
        return values;
    }

    public void setValues(String[] values) {
        this.values = values;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        if(code== StatuCodes.PUSH_JOB_SUCCESS_CODE)
            return "发布成功";
        else if(code == StatuCodes.PUSH_JOB_ERROR_CODE)
            return "发布失败";
        else if(code == StatuCodes.LOGIN_ERROR_CODE)
            return "登陆失败";
        else if(code == StatuCodes.GET_JOB_ID_ERROR)
            return "获取职位ID失败";
        else if(code == StatuCodes.JOB_COUNT_NOT_ENOUGH)
            return "职位点数不足";
        else if(code == StatuCodes.GET_JOB_ID_JSON_ERROR)
            return "POST之后的返回值JSON解析出错，重试";
        else
            return "error";
    }
}
