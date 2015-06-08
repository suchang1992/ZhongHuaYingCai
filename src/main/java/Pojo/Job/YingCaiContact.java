package Pojo.Job;

import Pojo.StatuCodes;

/**
 * Created by Administrator on 2015/6/5.
 */
public class YingCaiContact {
    String msg;
    int code;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        if(code== StatuCodes.GET_RESUME_CONTACT_SUCCESS_CODE)
            return "获取成功";
        else
            return "error";
    }
}
