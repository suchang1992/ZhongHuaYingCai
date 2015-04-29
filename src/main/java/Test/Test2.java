package Test;


import org.apache.log4j.Logger;

/**
 * Created by Administrator on 2015/4/28.
 */
public class Test2 {
    private static Logger logger = Logger.getLogger(Test2.class.getName());

    public static void asda(){
        logger.info("123");
        logger.debug("456");
        logger.error("789");
        logger.fatal("10");
    }

    public static void main(String[] args) {
        Test2.asda();

    }
}
