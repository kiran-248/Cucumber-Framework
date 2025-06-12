package com.orangehrm.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ExcelReader {
    private Workbook workbook;
    private Sheet sheet;

    // Constructor to load the Excel file
    public ExcelReader(String filePath, String sheetName) {
        try {
            FileInputStream file = new FileInputStream(new File(filePath));
            workbook = new XSSFWorkbook(file);
            sheet = workbook.getSheet(sheetName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to get data from Excel based on row and column index
    public String getCellData(int rowNum, int colNum) {
        Row row = sheet.getRow(rowNum);
        if (row != null) {
            Cell cell = row.getCell(colNum);
            if (cell != null) {
                return getCellValueAsString(cell);
            }
        }
        return "";
    }

    // Method to get total number of rows
    public int getRowCount() {
        return sheet.getPhysicalNumberOfRows();
    }

    // Method to get total number of columns in a row
    public int getColumnCount(int rowNum) {
        Row row = sheet.getRow(rowNum);
        return (row != null) ? row.getPhysicalNumberOfCells() : 0;
    }

    public Object[][] getExcelData() {
        int rowCount = getRowCount();
        int colCount = getColumnCount(0);
        Object[][] data = new Object[rowCount - 1][colCount]; // Skipping header row

        for (int i = 1; i < rowCount; i++) { // Start from 1 to skip headers
            for (int j = 0; j < colCount; j++)  //this is to extract total columns in every row
            {

                data[i - 1][j] = getCellData(i, j); //Since i starts from 1, but our array index starts from 0, we adjust it by subtracting 1
            }
        }
        return data;
    }




    // Helper method to convert cell values to String
    private String getCellValueAsString(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }

    // Method to close the workbook
    public void closeWorkbook() {
        try {
            if (workbook != null) {
                workbook.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
