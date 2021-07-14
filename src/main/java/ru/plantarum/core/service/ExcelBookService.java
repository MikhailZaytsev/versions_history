package ru.plantarum.core.service;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class ExcelBookService {

    public final String EXCEL_FORMAT = "xlsx";
    public final String EMPTY_FILE = "Файл не загружен";
    public final String WRONG_EXTENSION = "Загруженный файл не xlsx";
    public final String SMALL_FILE = "В загруженнном файле мало данных";
    public final String HIDDEN_SHEET = "В загруженном файле есть скрытый лист";
    public final String EMPTY_BOOK = "Загруженная книга пуста";

    private final int TOTAL_ROWS = 5;
    private final int CURRENT_SHEET = 0;

    private final String SOURCE_SHEET_NAME = "исходники";
    private final String EMPTY_CELL = "пустая клетка";
    private final String NOT_A_VALUE = "не значение";

    private final String TEMP_FILE_DIR = "D:/test/excel/";

    public String loadBook(MultipartFile file) {
        String tempFileName = new SimpleDateFormat("yyyy_MM_dd_HH-mm-ss_").format(new Date());
        if (file.isEmpty()) {
            return EMPTY_FILE;
        } else  {
            tempFileName = tempFileName + file.getOriginalFilename();
        }

        if (!FilenameUtils.getExtension(tempFileName).equals(EXCEL_FORMAT)) {
            return WRONG_EXTENSION;
        }

        XSSFWorkbook book = getBook(file);

        if (book == null) {
            return EMPTY_BOOK;
        } else {
            if (book.isSheetHidden(CURRENT_SHEET)) {
                return HIDDEN_SHEET;
            } else {
                Sheet sheet = book.getSheetAt(CURRENT_SHEET);
                int rows = sheet.getLastRowNum();
                if (rows < TOTAL_ROWS) {
                    return SMALL_FILE;
                }
            }
        }

        book.setSheetName(CURRENT_SHEET, SOURCE_SHEET_NAME);
        saveBook(book, tempFileName);

        return tempFileName;
    }

    public List<String> getValues(String fileName, int rowNumber) {
        List<String> values = new ArrayList<>();
        XSSFWorkbook book = getBook(fileName);
        if (book != null && book.getSheet(SOURCE_SHEET_NAME) != null) {
            Sheet sheet = book.getSheet(SOURCE_SHEET_NAME);
            Row row = sheet.getRow(rowNumber);
            for (int i = 0; i < row.getLastCellNum(); i++) {
                values.add(getStringFromCell(row, i));
            }
        }
        return values;
    }

    private String getStringFromCell(Row row, int i) {
        if (row.getCell(i) != null) {
            Cell cell = row.getCell(i);
            switch (cell.getCellType()) {
                case FORMULA: switch (cell.getCachedFormulaResultType()) {
                    case BOOLEAN: return Boolean.toString(cell.getBooleanCellValue());
                    case STRING: return cell.getStringCellValue();
                    case NUMERIC: return BigDecimal.valueOf(cell.getNumericCellValue()).toString();
                    default: return NOT_A_VALUE;
                }
                case STRING: return cell.getStringCellValue();
                case NUMERIC: return checkPrecision(cell.getNumericCellValue()).toString();
                case BOOLEAN: return Boolean.toString(cell.getBooleanCellValue());
                default: return NOT_A_VALUE;
            }
        } else {
            return EMPTY_CELL;
        }
    }

    private Number checkPrecision(double value) {
        BigDecimal number = new BigDecimal(String.valueOf(value));
        if (number.toString().length() == 13) {
            return number;
        }
        BigDecimal compared = new BigDecimal("0.0");
        int complete = number.intValue();
        BigDecimal left = new BigDecimal(number.subtract(new BigDecimal(complete)).toPlainString());
        if (left .compareTo(compared) > 0) {
            number = number.setScale(2, RoundingMode.HALF_UP);
            return number;
        } else {
            return number.toBigInteger();
        }
    }

    private void saveBook(XSSFWorkbook book, String fileName) {
        try {
            book.write(new FileOutputStream(TEMP_FILE_DIR + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private XSSFWorkbook getBook(MultipartFile file) {
        try {
            return new XSSFWorkbook(file.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private XSSFWorkbook getBook(String fileName) {
        try {
            return new XSSFWorkbook(new FileInputStream(TEMP_FILE_DIR + fileName));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
