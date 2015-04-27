package Utils;

import Pojo.Resume;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.*;

import java.io.IOException;
import java.net.UnknownHostException;

/**
 * Created by Administrator on 2015/4/22.
 */
public class mongohelper {
    private static MongoClient mongoClient;
    private static DB db;
    public static final String mongoDBname = "yingcai";
    static int crawled_count_min = 0;

    static {
        try {
//			mongoClient = new MongoClient(new ServerAddress("218.244.136.200", 27017));
            mongoClient = new MongoClient(new ServerAddress("127.0.0.1", 27017));
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

	public void upsertResumInfo(Resume resume){
		BasicDBObject query = new BasicDBObject("resumeID",resume.getResumeID());
		BasicDBObject user = new BasicDBObject("$set", resume.getSimpleResume());
		getColl(mongoDBname,"yingcai_resume").update(query,user,true,false);
	}
    public void inserResumeInfo(Resume resume){
        BasicDBObject query = new BasicDBObject("resumeID",resume.getResumeID());
        DBObject object = new BasicDBObject("$set",JSONObject.toJSON(resume.getSimpleResume()));
        getColl(mongoDBname,"yingcai_resume").update(query, object, true, false);
    }


    public static void main(String[] args) {

    }
}
