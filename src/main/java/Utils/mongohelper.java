package Utils;

import Pojo.KeyWord;
import Pojo.Resume;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.*;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Date;

/**
 * Created by Administrator on 2015/4/22.
 */
public class MongoHelper {
    private static Logger logger = Logger.getLogger(MongoHelper.class.getName());
    public static int newAddResumeCount = 0;
    private static MongoClient mongoClient;
    private static DB db;
    public static final String mongoDBname = "yingcai";
    static int crawled_count_min = 0;

    static {
        try {
//			mongoClient = new MongoClient(new ServerAddress("218.244.136.200", 27017));
            mongoClient = new MongoClient(new ServerAddress("192.168.3.222", 27017));
            mongoClient.setWriteConcern(WriteConcern.SAFE);
        } catch (UnknownHostException e) {
            //
        }
    }

    public DB getDB(String DBName) {
        if (db == null) {
            db = mongoClient.getDB(DBName);
            db.authenticate("sc", "123456".toCharArray());
        }
        return db;
    }

    public MongoClient getMongClient() {
        return mongoClient;
    }

    public DBCollection getColl(String DBName, String collection) {
        return this.getDB(DBName).getCollection(collection);
    }

    public void upsertResumInfo(Resume resume, KeyWord keyWord) {
        BasicDBObject query = new BasicDBObject("resumeID", resume.getResumeID());
        DBObject yingcai_resume = getColl(mongoDBname, "yingcai_resume").findOne(query);
        try {
            if (yingcai_resume == null) {
                BasicDBObject obj = new BasicDBObject("first_level_keywords", keyWord.getFirstlevel())
                        .append("second_level_keywords",keyWord.getSecondlevel())
                        .append("resumeID", resume.getResumeID())
                        .append("simple_resume", resume.getSimpleResume())
                        .append("resume_detil", resume.getResumeDetil())
                        .append("crawled_time", new Date().getTime());
                getColl(mongoDBname, "yingcai_resume").update(query, obj, true, false);
                newAddResumeCount++;
                logger.info("存入:"+resume.getResumeID());
            } else {
                String first_level_keywords = (String) yingcai_resume.get("first_level_keywords");
                String second_level_keywords = (String) yingcai_resume.get("second_level_keywords");
                BasicDBObject obj = new BasicDBObject();

                if(!first_level_keywords.contains(keyWord.getFirstlevel()) && !second_level_keywords.contains(keyWord.getSecondlevel())){
                    obj.append("first_level_keywords", first_level_keywords + "," + keyWord.getFirstlevel());
                    obj.append("second_level_keywords", second_level_keywords + "," + keyWord.getSecondlevel());
                    getColl(mongoDBname, "yingcai_resume").update(query, new BasicDBObject("$set", obj), true, false);
                    logger.info("更新first key and second key:"+resume.getResumeID());
                } else if (!first_level_keywords.contains(keyWord.getFirstlevel())) {
                    obj.append("first_level_keywords", first_level_keywords + "," + keyWord.getFirstlevel());
                    getColl(mongoDBname, "yingcai_resume").update(query, new BasicDBObject("$set", obj), true, false);
                    logger.info("更新first key:"+resume.getResumeID());
                } else if (!second_level_keywords.contains(keyWord.getSecondlevel())) {
                    obj.append("second_level_keywords", second_level_keywords + "," + keyWord.getSecondlevel());
                    getColl(mongoDBname, "yingcai_resume").update(query, new BasicDBObject("$set", obj), true, false);
                    logger.info("更新second key:"+resume.getResumeID());
                } else {
                    return;
                }
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            BasicDBObject obj = new BasicDBObject("first_level_keywords", keyWord.getFirstlevel())
                    .append("second_level_keywords",keyWord.getSecondlevel())
                    .append("resumeID", resume.getResumeID())
                    .append("simple_resume", resume.getSimpleResume())
                    .append("resume_detil", resume.getResumeDetil());
            logger.error(obj.toString());
        }
    }

    /**
     * 判断该简历是在库中
     * @param resumeID
     * @return 找到：true 未找到：false
     */
    public boolean isInMongoSearchById(String resumeID){
        BasicDBObject query = new BasicDBObject("resumeID", resumeID);
        DBObject yingcai_resume = getColl(mongoDBname, "yingcai_resume").findOne(query);
        if (yingcai_resume == null){
            return false;
        }
        return true;
    }

    public static int getNewAddResumeCount() {
        return newAddResumeCount;
    }

    public static void setNewAddResumeCount(int newAddResumeCount) {
        MongoHelper.newAddResumeCount = newAddResumeCount;
    }

    public static void main(String[] args) {

    }
}
