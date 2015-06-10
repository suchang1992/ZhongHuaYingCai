package Utils;

import Pojo.KeyWord;
import Pojo.Resume;
import Pojo.SpiderConfig;
import com.mongodb.*;
import org.apache.log4j.Logger;

import java.net.UnknownHostException;
import java.util.Date;

/**
 * Created by Administrator on 2015/6/9.
 */
public class MongoDBHelper {
    private static Logger logger = Logger.getLogger(MongoDBHelper.class.getName());
    private SpiderConfig spiderConfig;
    private MongoClient mongoClient;
    private DB db;
    private DBCollection collection;

    public static final int INSERT = 1;
    public static final int UPDATE_FIRST_AND_SECOND = 2;
    public static final int UPDATE_FIRST = 3;
    public static final int UPDATE_SECOND = 4;
    public static final int ERROR = 5;
    public static final int JSON_ERROR = 6;


    public MongoDBHelper(SpiderConfig spiderConfig) {
        this.spiderConfig = spiderConfig;
        try {
            this.mongoClient = new MongoClient(new ServerAddress(spiderConfig.getDb_ip(), spiderConfig.getPort()));
            this.mongoClient.setWriteConcern(WriteConcern.SAFE);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        this.db = mongoClient.getDB(spiderConfig.getDbName());
        if (spiderConfig.getMongodbUserName() != null && spiderConfig.getMongodbPassword() != null)
            this.db.authenticate(spiderConfig.getMongodbUserName(), spiderConfig.getMongodbPassword().toCharArray());
        this.collection = this.db.getCollection(spiderConfig.getCollectionName());
    }

    /**
     * 判断该简历是在库中
     * @param resumeID
     * @return 找到：true 未找到：false
     */
    public boolean isInMongoSearchById(String resumeID){
        BasicDBObject query = new BasicDBObject("resumeID", resumeID);
        DBObject yingcai_resume = this.db.getCollection(spiderConfig.getCollectionName()).findOne(query);
        if (yingcai_resume == null){
            return false;
        }
        return true;
    }

    /**
     * 更新或插入操作
     * @param resume
     * @param keyWord
     */
    public int upsertResumInfo(Resume resume, KeyWord keyWord) {
        BasicDBObject query = new BasicDBObject("resumeID", resume.getResumeID());
        DBObject yingcai_resume = this.db.getCollection(spiderConfig.getCollectionName()).findOne(query);
        try {
            if (yingcai_resume == null) {
                BasicDBObject obj = new BasicDBObject("first_level_keywords", keyWord.getFirstlevel())
                        .append("second_level_keywords",keyWord.getSecondlevel())
                        .append("resumeID", resume.getResumeID())
                        .append("simple_resume", resume.getSimpleResume())
                        .append("resume_detil", resume.getResumeDetil())
                        .append("crawled_time", new Date().getTime());
                this.collection.update(query, obj, true, false);
                logger.info("存入:"+resume.getResumeID());
                return INSERT;
            } else {
                String first_level_keywords = (String) yingcai_resume.get("first_level_keywords");
                String second_level_keywords = (String) yingcai_resume.get("second_level_keywords");
                BasicDBObject obj = new BasicDBObject();

                if(!first_level_keywords.contains(keyWord.getFirstlevel()) && !second_level_keywords.contains(keyWord.getSecondlevel())){
                    obj.append("first_level_keywords", first_level_keywords + "," + keyWord.getFirstlevel());
                    obj.append("second_level_keywords", second_level_keywords + "," + keyWord.getSecondlevel());
                    this.collection.update(query, new BasicDBObject("$set", obj), true, false);
                    logger.info("更新first key and second key:"+resume.getResumeID());
                    return UPDATE_FIRST_AND_SECOND;
                } else if (!first_level_keywords.contains(keyWord.getFirstlevel())) {
                    obj.append("first_level_keywords", first_level_keywords + "," + keyWord.getFirstlevel());
                    this.collection.update(query, new BasicDBObject("$set", obj), true, false);
                    logger.info("更新first key:"+resume.getResumeID());
                    return UPDATE_FIRST;
                } else if (!second_level_keywords.contains(keyWord.getSecondlevel())) {
                    obj.append("second_level_keywords", second_level_keywords + "," + keyWord.getSecondlevel());
                    this.collection.update(query, new BasicDBObject("$set", obj), true, false);
                    logger.info("更新second key:"+resume.getResumeID());
                    return UPDATE_SECOND;
                } else {
                    return ERROR;
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
            return JSON_ERROR;
        }
    }
}
