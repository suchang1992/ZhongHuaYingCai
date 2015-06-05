package Pojo.Job;

/**
 * Created by Administrator on 2015/6/5.
 */
public class JobResume {
    String resumeId;
    String deliverTime;

    public JobResume() {
    }

    public JobResume(String resumeId, String deliverTime) {
        this.resumeId = resumeId;
        this.deliverTime = deliverTime;
    }

    public String getResumeId() {
        return resumeId;
    }

    public void setResumeId(String resumeId) {
        this.resumeId = resumeId;
    }

    public String getDeliverTime() {
        return deliverTime;
    }

    public void setDeliverTime(String deliverTime) {
        this.deliverTime = deliverTime;
    }
}
