package Utils;

import Pojo.KeyWord;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.Row;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by Administrator on 2015/4/27.
 */
public class KeyWordsManager {

    private final String keywordsXls = "keywords.xls";
    private final String skipkeywordsXls = "skipkeywords.xls";


    public void writeKeyWord(KeyWord keyWord) {
        //读取文件
        InputStream skipin = null;
        try {
            skipin = new FileInputStream(new File("./"+skipkeywordsXls));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("未找到:" + skipkeywordsXls + " 请自行创建");
        }

        HSSFWorkbook skipWork = null;
        try {
            skipWork = new HSSFWorkbook(skipin);
            HSSFSheet skipSheet = skipWork.getSheetAt(0);
            HSSFRow row = skipSheet.getRow(keyWord.getRowNum());
            try {
                HSSFCell cell = row.createCell(keyWord.getCellNum());
                cell.setCellValue(keyWord.getSecondlevel());
            }catch (NullPointerException e){
                row = skipSheet.createRow(keyWord.getRowNum());
                HSSFCell cell = row.createCell(keyWord.getCellNum());
                cell.setCellValue(keyWord.getSecondlevel());
            }

            //写入文件
            FileOutputStream fileoutputstream = new FileOutputStream("./"+skipkeywordsXls);
            skipWork.write(fileoutputstream);
            fileoutputstream.close();
            skipin.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

    }

    public ArrayList<KeyWord> getKeyWordsList() {

        InputStream basein = ClassLoader.getSystemResourceAsStream(keywordsXls);
        if (basein == null) {
            System.out.println("未找到:" + keywordsXls + " 请自行创建");
            return null;
        }
        InputStream skipin = null;
        try {
            skipin = new FileInputStream(new File("./"+skipkeywordsXls));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("未找到:" + skipkeywordsXls + " 请自行创建");
            return null;
        }

        HSSFWorkbook baseWork = null;
        HSSFWorkbook skipWork = null;

        try {
            baseWork = new HSSFWorkbook(basein);

            skipWork = new HSSFWorkbook(skipin);
            // 在Excel文档中，第一张工作表的缺省索引是0，
            HSSFSheet baseSheet = baseWork.getSheetAt(0);
            HSSFSheet skipSheet = skipWork.getSheetAt(0);

            int lastRowNum = baseSheet.getLastRowNum();
            int firstRowNum = baseSheet.getFirstRowNum();//获得第一行的行号
            ArrayList<KeyWord> list = new ArrayList<KeyWord>();
            for (int i = firstRowNum; i <= lastRowNum; i++) {//访问每一行
                Row baserow = baseSheet.getRow(i);
                Row skiprow = skipSheet.getRow(i);
                String firstKeyword = null;
                try {
                    firstKeyword = baserow.getCell(0).getStringCellValue();
                }catch (NullPointerException e ){
                    continue;
                }
                int cellNum = baserow.getLastCellNum();
                for (int j = 0; j < cellNum; j++) {
                    String s = baserow.getCell(j).getStringCellValue();
                    try {
                        skiprow.getCell(j).getStringCellValue();//在skip中找到了，则不加入
                        continue;
                    } catch (NullPointerException e) {//在skip中没找到，则加入
                        if (!s.equals("")) {
                            KeyWord keyWord = new KeyWord(i, j);
                            keyWord.setSecondlevel(baserow.getCell(j).getStringCellValue());//填写子类名
                            keyWord.setFirstlevel(firstKeyword);
                            list.add(keyWord);
                        }
                    }
                }
            }

            basein.close();
            skipin.close();
            return list;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        ArrayList<KeyWord> keyWordsList = new KeyWordsManager().getKeyWordsList();
    }
}
