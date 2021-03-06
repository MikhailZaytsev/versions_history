package ru.plantarum.core.service;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.plantarum.core.uploading.excel.EntityFields;
import ru.plantarum.core.uploading.excel.ExcelEntity;
import ru.plantarum.core.uploading.response.InvalidParse;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ExcelBookService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelBookService.class);

    public final String EXCEL_FORMAT = "xlsx";
    public final String EMPTY_FILE = "Файл не загружен";
    public final String WRONG_EXTENSION = "Загруженный файл не xlsx";
    public final String SMALL_FILE = "В загруженнном файле мало данных";
    public final String HIDDEN_SHEET = "В загруженном файле есть скрытый лист";
    public final String EMPTY_BOOK = "Загруженная книга пуста";

    private final int TOTAL_ROWS = 5;
    private final int CURRENT_SHEET = 0;

    public final String SOURCE_SHEET_NAME = "исходники";
    public final String EMPTY_CELL = "пустая клетка";
    public final String NOT_A_VALUE = "не значение";

    @Value("${temp.excel.directory}")
    private String TEMP_FILE_DIR;

    public ExcelEntity setHeaders(ExcelEntity excelEntity) {
        XSSFWorkbook book = getBook(excelEntity.getTempFileName());
        if (book != null) {
            Row row = book.getSheet(SOURCE_SHEET_NAME).getRow(0);
            for (int i = 0; i < excelEntity.getHeaders().size(); i++) {
                row.getCell(i).setCellValue(excelEntity.getHeaders().get(i));
                if (!excelEntity.getHeadersToCols().containsKey(EntityFields.valueOf(getStringFromCell(row, i)))) {
                    switch (EntityFields.valueOf(getStringFromCell(row, i))) {
                        case NUMBER_IN_PACK:
                            excelEntity.getHeadersToCols().put(EntityFields.NUMBER_IN_PACK, i);
                            break;
                        case PRODUCT_NAME:
                            excelEntity.getHeadersToCols().put(EntityFields.PRODUCT_NAME, i);
                            break;
                        case ORGAN_TYPE:
                            excelEntity.getHeadersToCols().put(EntityFields.ORGAN_TYPE, i);
                            break;
                        case TRADEMARK:
                            excelEntity.getHeadersToCols().put(EntityFields.TRADEMARK, i);
                            break;
                        case PRICE_OUT:
                            excelEntity.getHeadersToCols().put(EntityFields.PRICE_OUT, i);
                            break;
                        case PRICE_IN:
                            excelEntity.getHeadersToCols().put(EntityFields.PRICE_IN, i);
                            break;
                        case EAN13:
                            excelEntity.getHeadersToCols().put(EntityFields.EAN13, i);
                            break;
                        default:
                            break;
                    }
                } else {
                    excelEntity.getErrors().add(new InvalidParse(0, "ExcelBook", "Были выбраны одинаковые заголовки для разных столбцов"));
                    LOGGER.error(String.format("%s, ошибка - %s", "ExcelBook", "Были выбраны одинаковые заголовки для разных столбцов"));
                    break;
                }
            }
            saveBook(book, excelEntity.getTempFileName());
        }
        return excelEntity;
    }

    public String loadBook(MultipartFile file) {
        String tempFileName = new SimpleDateFormat("yyyy_MM_dd_HH-mm-ss_").format(new Date());
        if (file.isEmpty()) {
            LOGGER.error(String.format("Выбранный файл - пуст: %s", file.getOriginalFilename()));
            return EMPTY_FILE;
        } else {
            tempFileName = tempFileName + file.getOriginalFilename();
        }

        if (!FilenameUtils.getExtension(tempFileName).equals(EXCEL_FORMAT)) {
            LOGGER.error("Неверный формат файла");
            return WRONG_EXTENSION;
        }

        XSSFWorkbook book = getBook(file);

        if (book == null) {
            LOGGER.error("Книга в указанном файле пустая");
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

    public String getStringFromCell(Row row, int i) {
        if (row.getCell(i) != null) {
            Cell cell = row.getCell(i);
            switch (cell.getCellType()) {
                case FORMULA:
                    switch (cell.getCachedFormulaResultType()) {
                        case BOOLEAN:
                            return Boolean.toString(cell.getBooleanCellValue());
                        case STRING:
                            return cell.getStringCellValue();
                        case NUMERIC:
                            return BigDecimal.valueOf(cell.getNumericCellValue()).toString();
                        default:
                            return NOT_A_VALUE;
                    }
                case BLANK:
                    return EMPTY_CELL;
                case STRING:
                    return cell.getStringCellValue();
                case NUMERIC:
                    return BigDecimal.valueOf(cell.getNumericCellValue()).toPlainString();
                case BOOLEAN:
                    return Boolean.toString(cell.getBooleanCellValue());
                default:
                    return NOT_A_VALUE;
            }
        } else {
            return EMPTY_CELL;
        }
    }

    public void saveBook(XSSFWorkbook book, String fileName) {
        try {
            FileOutputStream output = new FileOutputStream(TEMP_FILE_DIR + fileName);
            book.write(output);
            output.close();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        }
    }

    private XSSFWorkbook getBook(MultipartFile file) {
        try {
            InputStream inputStream = file.getInputStream();
            XSSFWorkbook book = new XSSFWorkbook(inputStream);
            inputStream.close();
            return book;
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public XSSFWorkbook getBook(String fileName) {
        try {
            FileInputStream input = new FileInputStream(TEMP_FILE_DIR + fileName);
            XSSFWorkbook book = new XSSFWorkbook(input);
            input.close();
            return book;
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    public void deleteBook(String fileName) {
        File file = new File(TEMP_FILE_DIR + fileName);
        try {
            if (Files.deleteIfExists(file.toPath())) {
                LOGGER.info("Временный файл был удалён");
            } else {
                LOGGER.error(String.format("Временный файл не был удалён: %s", fileName));
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        }
    }
}
