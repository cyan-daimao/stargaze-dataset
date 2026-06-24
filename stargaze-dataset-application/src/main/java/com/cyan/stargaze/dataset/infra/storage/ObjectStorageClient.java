package com.cyan.stargaze.dataset.infra.storage;

import com.cyan.arch.common.api.SilentException;
import com.cyan.stargaze.dataset.infra.config.S3Properties;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * S3 兼容对象存储客户端(rustfs/MinIO)。
 * <p>
 * 封装上传/下载/删除;下载落到临时文件供 POI 解析,避免内存双份持有。
 *
 * @author cy.Y
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ObjectStorageClient {

    private final S3Properties properties;
    private S3Client s3Client;

    @PostConstruct
    public void init() {
        S3Properties p = properties;
        S3Configuration.Builder cfgBuilder = S3Configuration.builder()
                .pathStyleAccessEnabled(p.isPathStyleAccess());
        s3Client = S3Client.builder()
                .endpointOverride(URI.create(p.getEndpoint()))
                .region(Region.of(p.getRegion() == null ? "us-east-1" : p.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(p.getAccessKey(), p.getSecretKey())))
                .serviceConfiguration(cfgBuilder.build())
                .build();
    }

    @PreDestroy
    public void destroy() {
        if (s3Client != null) {
            s3Client.close();
        }
    }

    /**
     * 上传对象
     *
     * @param key         对象 key
     * @param input       输入流
     * @param contentType 文件类型
     * @param size        字节大小(可空)
     */
    public void putObject(String key, InputStream input, String contentType, Long size) {
        try {
            PutObjectRequest.Builder reqBuilder = PutObjectRequest.builder()
                    .bucket(properties.getBucket())
                    .key(key)
                    .contentType(contentType);
            PutObjectRequest request = reqBuilder.build();
            RequestBody body = size != null
                    ? RequestBody.fromInputStream(input, size)
                    : RequestBody.fromInputStream(input, availableOrFallback(input));
            s3Client.putObject(request, body);
        } catch (Exception e) {
            throw new SilentException("对象存储上传失败: " + e.getMessage());
        }
    }

    /**
     * 下载对象到临时文件
     */
    public Path getObject(String key) {
        try {
            Path temp = Files.createTempFile("dataset-file-", ".tmp");
            try (InputStream is = s3Client.getObjectAsBytes(build -> build.bucket(properties.getBucket()).key(key)).asInputStream()) {
                Files.copy(is, temp, StandardCopyOption.REPLACE_EXISTING);
            }
            temp.toFile().deleteOnExit();
            return temp;
        } catch (Exception e) {
            throw new SilentException("对象存储下载失败: " + e.getMessage());
        }
    }

    /**
     * 删除对象
     */
    public void deleteObject(String key) {
        try {
            s3Client.deleteObject(b -> b.bucket(properties.getBucket()).key(key));
        } catch (Exception e) {
            log.warn("对象存储删除失败, key={}, err={}", key, e.getMessage());
        }
    }

    private long availableOrFallback(InputStream input) {
        try {
            long n = input.available();
            return n > 0 ? n : 0L;
        } catch (Exception e) {
            return 0L;
        }
    }
}
