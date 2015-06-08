package Pojo;

/**
 * Created by Administrator on 2015/6/4.
 */
public class StatuCodes {
    public static final int PUSH_JOB_SUCCESS_CODE = 1;   //发布职位成功
    public static final int PULL_RESUME_SUCCESS_CODE = 1;   //收取简历成功
    public static final int GET_RESUME_SUCCESS_CODE = 1;   //收取简历成功
    public static final int GET_RESUME_CONTACT_SUCCESS_CODE = 1;   //获取联系方式成功
    public static final int PUSH_JOB_ERROR_CODE = -1;   //发布职位失败
    public static final int LOGIN_ERROR_CODE = -2;   //登陆失败
    public static final int GET_JOB_ID_ERROR = -3;   //获取职位id失败
    public static final int GET_JOB_ID_JSON_ERROR = -4;   //获取职位id时的json转换失败
    public static final int JOB_COUNT_NOT_ENOUGH = -5;   //可发布的职位数不足
    public static final int STRING_TO_JSON_ERROR = -6;   //string转json失败
    public static final int RESUME_COUNT_NOT_ENOUGH = -7;   //可购买职位数不足
    public static final int RESUME_ARE_ERROR = -8;   //账号地区和简历地区不对应
    public static final int GET_RESUME_CONTACT_ERROR = -9;   //获取联系方式失败

}
