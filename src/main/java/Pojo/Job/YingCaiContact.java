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
        else if(code == StatuCodes.LOGIN_ERROR_CODE)
            return "登陆失败";
        else if(code == StatuCodes.RESUME_COUNT_NOT_ENOUGH)
            return "可购买职位数不足";
        else if(code == StatuCodes.RESUME_ARE_ERROR)
            return "账号地区和简历地区不对应";
        else if(code == StatuCodes.GET_RESUME_CONTACT_ERROR)
            return "获取联系方式失败";
        else
            return "error";
    }
}
