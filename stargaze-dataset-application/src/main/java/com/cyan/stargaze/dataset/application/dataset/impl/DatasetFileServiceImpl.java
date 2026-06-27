package com.cyan.stargaze.dataset.application.dataset.impl;

import com.cyan.arch.common.api.Assert;
import com.cyan.arch.common.api.SilentException;
import com.cyan.stargaze.dataset.application.dataset.DatasetFileService;
import com.cyan.stargaze.dataset.application.dataset.bo.ColumnBO;
import com.cyan.stargaze.dataset.application.dataset.bo.DatasetFileBO;
import com.cyan.stargaze.dataset.application.dataset.bo.ExcelPreviewBO;
import com.cyan.stargaze.dataset.application.dataset.bo.ExcelSheetBO;
import com.cyan.stargaze.dataset.application.dataset.bo.TableSampleBO;
import com.cyan.stargaze.dataset.application.dataset.bo.TableSchemaBO;
import com.cyan.stargaze.dataset.application.dataset.convert.DatasetFileAppConvert;
import com.cyan.stargaze.dataset.domain.dataset.DatasetFile;
import com.cyan.stargaze.dataset.domain.dataset.repository.DatasetFileRepository;
import com.cyan.stargaze.dataset.infra.config.DatasetExcelProperties;
import com.cyan.stargaze.dataset.infra.excel.ExcelFileParser;
import com.cyan.stargaze.dataset.infra.storage.ObjectStorageClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 数据集文件应用服务实现。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class DatasetFileServiceImpl implements DatasetFileService {

    private final DatasetFileRepository datasetFileRepository;
    private final ObjectStorageClient objectStorageClient;
    private final ExcelFileParser excelFileParser;
    private final DatasetExcelProperties excelProperties;
    private final DatasetFileAppConvert convert;

    @Override
    public DatasetFileBO upload(MultipartFile file) {
        Assert.notNull(file, new SilentException("文件不能为空"));
        Assert.isTrue(!file.isEmpty(), new SilentException("文件不能为空"));
        long size = file.getSize();
        Assert.isTrue(size <= excelProperties.getMaxFileSize(),
                new SilentException("文件大小超过上限 " + excelProperties.getMaxFileSize() + " 字节"));
        String fileName = file.getOriginalFilename();
        Assert.notBlank(fileName, new SilentException("文件名不能为空"));
        String contentType = file.getContentType();
        String objectKey = buildObjectKey(fileName);
        try (InputStream input = file.getInputStream()) {
            objectStorageClient.putObject(objectKey, input, contentType, size);
        } catch (IOException e) {
            throw new SilentException("读取上传文件失败: " + e.getMessage());
        }
        DatasetFile datasetFile = new DatasetFile()
                .setFileName(fileName)
                .setObjectKey(objectKey)
                .setContentType(contentType)
                .setSize(size);
        datasetFile = datasetFile.save(datasetFileRepository);
        return convert.toDatasetFileBO(datasetFile);
    }

    @Override
    public List<ExcelSheetBO> listSheets(String fileId) {
        DatasetFile file = loadFile(fileId);
        File local = download(file);
        try {
            return convert.toExcelSheetBOList(excelFileParser.listSheets(local));
        } finally {
            deleteQuietly(local);
        }
    }

    @Override
    public TableSchemaBO schema(String fileId, String sheetName, Integer headerRow) {
        DatasetFile file = loadFile(fileId);
        File local = download(file);
        try {
            return convert.toTableSchemaBO(excelFileParser.parseSchema(local, sheetName, headerRow));
        } finally {
            deleteQuietly(local);
        }
    }

    @Override
    public TableSampleBO sample(String fileId, String sheetName, Integer headerRow, int limit) {
        DatasetFile file = loadFile(fileId);
        File local = download(file);
        try {
            return convert.toTableSampleBO(excelFileParser.sample(local, sheetName, headerRow, limit));
        } finally {
            deleteQuietly(local);
        }
    }

    @Override
    public ExcelPreviewBO preview(String fileId, String sheetName, Integer headerRow, int limit) {
        DatasetFile file = loadFile(fileId);
        File local = download(file);
        try {
            TableSchemaBO schema = convert.toTableSchemaBO(excelFileParser.parseSchema(local, sheetName, headerRow));
            TableSampleBO sample = convert.toTableSampleBO(excelFileParser.sample(local, sheetName, headerRow, limit));
            // 列: index/name/suggestedType
            List<ExcelPreviewBO.ExcelColumnBO> columns = new ArrayList<>();
            List<String> colNames = new ArrayList<>();
            int idx = 0;
            for (ColumnBO c : schema.getColumns()) {
                columns.add(new ExcelPreviewBO.ExcelColumnBO()
                        .setIndex(idx)
                        .setName(c.getName())
                        .setSuggestedType(c.getSuggestedType()));
                colNames.add(c.getName());
                idx++;
            }
            // 行: Map -> List<Object>(按列顺序)
            List<List<Object>> rows = new ArrayList<>();
            for (Map<String, Object> row : sample.getRows()) {
                List<Object> values = new ArrayList<>(colNames.size());
                for (String name : colNames) {
                    values.add(row.get(name));
                }
                rows.add(values);
            }
            return new ExcelPreviewBO()
                    .setFileId(fileId)
                    .setFileName(file.getFileName())
                    .setSheetName(sheetName)
                    .setTotalRows(schema.getRowCount())
                    .setColumns(columns)
                    .setRows(rows);
        } finally {
            deleteQuietly(local);
        }
    }

    @Override
    public DatasetFileBO getFile(String fileId) {
        return convert.toDatasetFileBO(datasetFileRepository.findById(fileId));
    }

    /**
     * 加载文件登记记录
     */
    private DatasetFile loadFile(String fileId) {
        DatasetFile file = datasetFileRepository.findById(fileId);
        Assert.notNull(file, new SilentException("文件不存在"));
        return file;
    }

    /**
     * 下载到本地临时文件
     */
    private File download(DatasetFile file) {
        Path path = objectStorageClient.getObject(file.getObjectKey());
        return path.toFile();
    }

    private void deleteQuietly(File file) {
        try {
            Files.deleteIfExists(file.toPath());
        } catch (IOException ignored) {
            // 临时文件清理失败不影响业务
        }
    }

    /**
     * 构造对象 key: dataset-files/{ts}{ext}
     */
    private String buildObjectKey(String fileName) {
        String ext = "";
        int dot = fileName.lastIndexOf('.');
        if (dot >= 0) {
            ext = fileName.substring(dot);
        }
        return "dataset-files/" + System.currentTimeMillis() + ext;
    }
}
