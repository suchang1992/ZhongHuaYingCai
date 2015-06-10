package Callable;

import Pojo.KeyWord;
import Pojo.Resume;
import Pojo.SpiderConfig;
import Utils.CommonParameter;
import Utils.MongoDBHelper;
import Utils.SpiderGetResume;
import Utils.ZhongHuaYingCaiLogin;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.Callable;

/**
 * Created by Administrator on 2015/6/10.
 */
public class CallableGetResumeV2 implements Callable{
    private static Logger logger = Logger.getLogger(CallableGetResumeV2.class.getName());
    String cookie;
    SpiderConfig spiderConfig;
    MongoDBHelper mongoDBHelper;
    KeyWord keyWord;
    int pageNum;
    int delay;
    private int count = 0;


    public CallableGetResumeV2(String cookie, SpiderConfig spiderConfig, MongoDBHelper mongoDBHelper, KeyWord keyWord, int pageNum, int delay) {
        this.cookie = cookie;
        this.spiderConfig = spiderConfig;
        this.mongoDBHelper = mongoDBHelper;
        this.keyWord = keyWord;
        this.pageNum = pageNum;
        this.delay = delay;
    }

    @Override
    public Integer call() throws Exception {
        HashMap<String, String> formData = new HashMap<>();
        formData.put("flag", "1");
        if (this.spiderConfig.isLimitArea())
            formData.put("recruitRangeSelector312result", "27,312");
        formData.put("wishPlacesId", "27,312");
        formData.put("keywordSelect1", "0");
        formData.put("fuzzyWishPlace", "1");
        formData.put("matchLevel", "1,2");
        formData.put("searcherCount", "0");
        formData.put("used", "0");
        formData.put("allKeyword", "0");
        formData.put("allKeyword2", "0");
        formData.put("keyword", this.keyWord.getSecondlevel());
        formData.put("keywordSelect", "0");
        formData.put("page", ""+pageNum);
        String json = SpiderGetResume.doPostToGetList(CommonParameter.resume_list_url, formData, this.cookie);
        JSONObject jsonObject = JSON.parseObject(json);

        JSONArray jsonArray = jsonObject.getJSONObject("res").getJSONArray("resumeList");
        Iterator<Object> iterator = jsonArray.iterator();
        while (iterator.hasNext()) {
            JSONObject obj = (JSONObject) iterator.next();
            Resume resume = SpiderGetResume.getResumeDetil(obj, this.keyWord, this.cookie, this.mongoDBHelper, 1000);
            if (resume != null) {
                int i = this.mongoDBHelper.upsertResumInfo(resume, keyWord);
                if (i == MongoDBHelper.INSERT)
                    count++;
            }
        }
        return count;
    }
}
