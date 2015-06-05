package Pojo.Job;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/6/3.
 */
public class ZhongHuaYingCaiJobResume {
    String jobId;
    ArrayList<JobResume> resumeIds = new ArrayList<>();
    int resumeCount;
    int code = 0;


    public int getResumeCount() {
        return resumeCount;
    }

    public void setResumeCount(int resumeCount) {
        this.resumeCount = resumeCount;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public ArrayList<JobResume> getResumeIds() {
        return resumeIds;
    }

    public void setResumeIds(ArrayList<JobResume> resumeIds) {
        this.resumeIds = resumeIds;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
