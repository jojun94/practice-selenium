package Excel;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by JOJUN on 2020-10-31
 */
public class ExcelManager {
    public static void writeExcelFile(ArrayList<HashMap<String, String>> arr) throws IOException {
        String filePath = "output.xlsx";
        FileOutputStream fos = new FileOutputStream(filePath);
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Result");    // sheet 생성

        XSSFRow curRow;
        int row = arr.size();    // list 크기

        curRow = sheet.createRow(0);
        curRow.createCell(0).setCellValue("순번");
        curRow.createCell(1).setCellValue("이름");
        curRow.createCell(2).setCellValue("전화번호");

        for (int i = 0; i < row; i++) {
            curRow = sheet.createRow(i+1);    // row 생성
            curRow.createCell(0).setCellValue(i+1);    // row에 각 cell 저장
            curRow.createCell(1).setCellValue(arr.get(i).get("title"));
            curRow.createCell(2).setCellValue(arr.get(i).get("phone"));
        }

        workbook.write(fos);
        fos.close();
    }
}
