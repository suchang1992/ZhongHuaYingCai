package Pojo;

import com.mongodb.DB;
import com.mongodb.MongoClient;

/**
 * Created by Administrator on 2015/6/9.
 */
public class SpiderConfig {
    private String username;
    private String password;
    private boolean isLimitArea = false;
    private String area_id;// = "27,312";
    private int delay_time = 1000;
    private String dbName;// = "yingcai";
    private String db_ip;// = "192.168.3.222";
    private int port = 27017;
    private String collectionName;// = "yingcai_resume";
    private String mongodbUserName = null;
    private String mongodbPassword = null;
    private int account_status;// = 3;//1:在线企业  2:  3:非在线企业
    private String version = "";

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getAccount_status() {
        return account_status;
    }

    public void setAccount_status(int account_status) {
        this.account_status = account_status;
    }

    public String getMongodbUserName() {
        return mongodbUserName;
    }

    public void setMongodbUserName(String mongodbUserName) {
        this.mongodbUserName = mongodbUserName;
    }

    public String getMongodbPassword() {
        return mongodbPassword;
    }

    public void setMongodbPassword(String mongodbPassword) {
        this.mongodbPassword = mongodbPassword;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isLimitArea() {
        return isLimitArea;
    }

    public void setLimitArea(boolean isLimitArea) {
        this.isLimitArea = isLimitArea;
    }

    public String getArea_id() {
        return area_id;
    }

    public void setArea_id(String area_id) {
        this.area_id = area_id;
    }

    public int getDelay_time() {
        return delay_time;
    }

    public void setDelay_time(int delay_time) {
        this.delay_time = delay_time;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getDb_ip() {
        return db_ip;
    }

    public void setDb_ip(String db_ip) {
        this.db_ip = db_ip;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }
}
