package com.cyan.stargaze.dataset.infra.excel;

import com.cyan.arch.common.api.Assert;
import com.cyan.arch.common.api.SilentException;
import com.cyan.stargaze.dataset.domain.dataset.valobj.ExcelSheetValObj;
import com.cyan.stargaze.dataset.domain.datasource.valobj.ColumnValObj;
import com.cyan.stargaze.dataset.domain.datasource.valobj.TableSampleValObj;
import com.cyan.stargaze.dataset.domain.datasource.valobj.TableSchemaValObj;
import com.cyan.stargaze.dataset.enums.DataType;
import com.cyan.stargaze.dataset.infra.config.DatasetExcelProperties;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Excel 文件解析器(POI file-based)。
 * <p>
 * 提供 sheet 列表 / schema 解析(表头规范化+类型推断) / 数据采样。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
public class ExcelFileParser {

    private final DatasetExcelProperties properties;

    /**
     * 列出所有 sheet 及行数
     */
    public List<ExcelSheetValObj> listSheets(File file) {
        List<ExcelSheetValObj> sheets = new ArrayList<>();
        try (Workbook workbook = WorkbookFactory.create(file)) {
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                int rowCount = Math.min(sheet.getLastRowNum() + 1, properties.getMaxRows());
                sheets.add(new ExcelSheetValObj().setName(sheet.getSheetName()).setRowCount(rowCount));
            }
        } catch (Exception e) {
            throw new SilentException("解析 Excel sheet 列表失败: " + e.getMessage());
        }
        return sheets;
    }

    /**
     * 解析 sheet 字段结构(表头规范化 + 类型推断)
     *
     * @param headerRow 表头所在行(1 起始)
     */
    public TableSchemaValObj parseSchema(File file, String sheetName, Integer headerRow) {
        int headerIdx = headerRow == null || headerRow < 1 ? 0 : headerRow - 1;
        try (Workbook workbook = WorkbookFactory.create(file)) {
            Sheet sheet = workbook.getSheet(sheetName);
            Assert.notNull(sheet, new SilentException("sheet 不存在: " + sheetName));
            Row header = sheet.getRow(headerIdx);
            Assert.notNull(header, new SilentException("表头行不存在"));
            List<String> headers = normalizeHeaders(header);
            List<DataType> types = inferTypes(sheet, headerIdx, headers.size());
            List<ColumnValObj> columns = new ArrayList<>();
            for (int i = 0; i < headers.size(); i++) {
                columns.add(new ColumnValObj()
                        .setName(headers.get(i))
                        .setDataType(toSourceTypeName(types.get(i)))
                        .setNullable(true)
                        .setPrimaryKey(false));
            }
            int rowCount = Math.min(sheet.getLastRowNum() - headerIdx, properties.getMaxRows());
            return new TableSchemaValObj()
                    .setTableName(sheetName)
                    .setRowCount((long) Math.max(rowCount, 0))
                    .setColumns(columns);
        } catch (Exception e) {
            if (e instanceof SilentException se) {
                throw se;
            }
            throw new SilentException("解析 Excel schema 失败: " + e.getMessage());
        }
    }

    /**
     * 采样 sheet 数据
     */
    public TableSampleValObj sample(File file, String sheetName, Integer headerRow, int limit) {
        int headerIdx = headerRow == null || headerRow < 1 ? 0 : headerRow - 1;
        int capped = Math.min(limit, properties.getSampleRows());
        try (Workbook workbook = WorkbookFactory.create(file)) {
            Sheet sheet = workbook.getSheet(sheetName);
            Assert.notNull(sheet, new SilentException("sheet 不存在: " + sheetName));
            Row header = sheet.getRow(headerIdx);
            Assert.notNull(header, new SilentException("表头行不存在"));
            List<String> headers = normalizeHeaders(header);
            List<String> columns = new ArrayList<>(headers);
            List<Map<String, Object>> rows = new ArrayList<>();
            for (int r = headerIdx + 1; r <= sheet.getLastRowNum() && rows.size() < capped; r++) {
                Row row = sheet.getRow(r);
                if (row == null) {
                    continue;
                }
                Map<String, Object> rowMap = new LinkedHashMap<>();
                for (int c = 0; c < columns.size(); c++) {
                    rowMap.put(columns.get(c), readCell(row.getCell(c)));
                }
                rows.add(rowMap);
            }
            return new TableSampleValObj().setColumns(columns).setRows(rows);
        } catch (Exception e) {
            if (e instanceof SilentException se) {
                throw se;
            }
            throw new SilentException("采样 Excel 数据失败: " + e.getMessage());
        }
    }

    /**
     * 表头规范化:空 -> column_&lt;idx&gt;,重复 -> name_n
     */
    private List<String> normalizeHeaders(Row header) {
        List<String> headers = new ArrayList<>();
        Map<String, Integer> counter = new HashMap<>();
        int lastCol = header.getLastCellNum();
        Assert.isTrue(lastCol > 0, new SilentException("表头为空"));
        Assert.isTrue(lastCol <= properties.getMaxColumns(),
                new SilentException("列数超过上限 " + properties.getMaxColumns()));
        for (int i = 0; i < lastCol; i++) {
            Cell cell = header.getCell(i);
            String raw = cell == null ? "" : readCellAsString(cell).trim();
            String name = raw.isEmpty() ? "column_" + (i + 1) : raw;
            int cnt = counter.getOrDefault(name, 0) + 1;
            counter.put(name, cnt);
            headers.add(cnt == 1 ? name : name + "_" + cnt);
        }
        return headers;
    }

    /**
     * 用前 inferRows 行推断每列类型
     */
    private List<DataType> inferTypes(Sheet sheet, int headerIdx, int colCount) {
        List<DataType> types = new ArrayList<>();
        for (int c = 0; c < colCount; c++) {
            types.add(inferColumnType(sheet, headerIdx, c));
        }
        return types;
    }

    private DataType inferColumnType(Sheet sheet, int headerIdx, int col) {
        boolean allInt = true;
        boolean allDecimal = true;
        boolean allDate = true;
        boolean allBlank = true;
        int scanned = 0;
        for (int r = headerIdx + 1; r <= sheet.getLastRowNum() && scanned < properties.getInferRows(); r++) {
            Row row = sheet.getRow(r);
            if (row == null) {
                continue;
            }
            Cell cell = row.getCell(col);
            if (cell == null || cell.getCellType() == CellType.BLANK) {
                // 空单元格不计入采样预算,避免稀疏列前 N 行全空被误判为 STRING
                continue;
            }
            scanned++;
            allBlank = false;
            CellType type = cell.getCellType();
            if (type == CellType.NUMERIC) {
                if (DateUtil.isCellDateFormatted(cell)) {
                    allInt = false;
                    allDecimal = false;
                } else {
                    double v = cell.getNumericCellValue();
                    if (v != Math.floor(v)) {
                        allInt = false;
                    }
                    allDate = false;
                }
            } else if (type == CellType.STRING) {
                String s = cell.getStringCellValue().trim();
                if (!isInteger(s)) {
                    allInt = false;
                }
                if (!isDecimal(s)) {
                    allDecimal = false;
                }
                allDate = false;
            } else {
                allInt = false;
                allDecimal = false;
                allDate = false;
            }
        }
        if (allBlank) {
            return DataType.STRING;
        }
        if (allInt) {
            return DataType.INT;
        }
        if (allDecimal) {
            return DataType.DECIMAL;
        }
        if (allDate) {
            return DataType.DATETIME;
        }
        return DataType.STRING;
    }

    private Object readCell(Cell cell) {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return null;
        }
        return switch (cell.getCellType()) {
            case NUMERIC -> DateUtil.isCellDateFormatted(cell) ? cell.getDateCellValue() : cell.getNumericCellValue();
            case STRING -> cell.getStringCellValue();
            case BOOLEAN -> cell.getBooleanCellValue();
            case FORMULA -> cell.getCellFormula();
            default -> null;
        };
    }

    private String readCellAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        return switch (cell.getCellType()) {
            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
            case STRING -> cell.getStringCellValue();
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            default -> "";
        };
    }

    private String toSourceTypeName(DataType type) {
        return type == null ? "string" : type.getCode();
    }

    private boolean isInteger(String s) {
        try {
            Long.parseLong(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isDecimal(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
