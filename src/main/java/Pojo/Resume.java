package Pojo;

/**
 * Created by Administrator on 2015/4/24.
 */
public class Resume {
    String resumeID;
    String simpleResume;
    String resumeDetil;




    public Resume(String resumeID) {
        this.resumeID = resumeID;
    }

    public String getResumeID() {
        return resumeID;
    }

    public void setResumeID(String resumeID) {
        this.resumeID = resumeID;
    }

    public String getSimpleResume() {
        return simpleResume;
    }

    public void setSimpleResume(String simpleResume) {
        this.simpleResume = simpleResume;
    }

    public String getResumeDetil() {
        return resumeDetil;
    }

    public void setResumeDetil(String resumeDetil) {
        this.resumeDetil = resumeDetil;
    }
}
