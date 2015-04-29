package Pojo;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Administrator on 2015/4/27.
 */
public class KeyWord {
    String firstlevel;
    String secondlevel;
    int rowNum;
    int cellNum;

    public KeyWord() {
    }

    public KeyWord(int rowNum, int cellNum) {
        this.rowNum = rowNum;
        this.cellNum = cellNum;
    }

    public KeyWord(String firstlevel, String secondlevel, int rowNum, int cellNum) {
        this.firstlevel = firstlevel;
        this.secondlevel = secondlevel;
        this.rowNum = rowNum;
        this.cellNum = cellNum;
    }

    public String getFirstlevel() {
        return firstlevel;
    }

    public void setFirstlevel(String firstlevel) {
        this.firstlevel = firstlevel;
    }

    public String getSecondlevel() {
        return secondlevel;
    }

    public void setSecondlevel(String secondlevel) {
        this.secondlevel = secondlevel;
    }

    public int getRowNum() {
        return rowNum;
    }

    public void setRowNum(int rowNum) {
        this.rowNum = rowNum;
    }

    public int getCellNum() {
        return cellNum;
    }

    public void setCellNum(int cellNum) {
        this.cellNum = cellNum;
    }
}
