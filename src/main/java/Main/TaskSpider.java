package Main;

/**
 * Created by Administrator on 2015/6/10.
 */
public class TaskSpider {
    public static void main(String[] args) {
        long l = System.currentTimeMillis();
        ZhongHuaYingCaiSpider zhongHuaYingCaiSpider = new ZhongHuaYingCaiSpider();
        zhongHuaYingCaiSpider.init("yingcai","192.168.3.222",27017,
                "yingcai_resume_6_9","vipcdylf","longhu123","27,312", null,1000,
                "./keywords.xls","./","skipkeywords.xls");
        zhongHuaYingCaiSpider.start();
        System.out.println(l-System.currentTimeMillis());
    }
}
