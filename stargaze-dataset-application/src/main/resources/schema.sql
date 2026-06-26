-- stargaze-dataset 元数据库完整建表脚本
-- 数据库: PostgreSQL, schema: stargaze_dataset
-- 用途: 删除旧表后全量重建(对应 DO/Domain 最终字段定义)
--
-- 约定:
--   主键 BIGINT 由应用层(MyBatis-Plus 雪花 ASSIGN_ID)赋值, 不自增
--   时间列 TIMESTAMPTZ 对齐 OffsetDateTime
--   config_enc 存 AES-256-GCM 密文(text, 非 jsonb)
--   definition/refresh_config/accelerations/pool_config/levels/config/semantic_type/format
--     均存 JSON 字符串(text), 由应用层 fastjson2 序列化
--   逻辑删除: deleted_at, 应用层 @TableLogic(value=null, delval=now())

-- 清理旧表(注意外键依赖顺序: 子表先删)
DROP TABLE IF EXISTS stargaze_dataset.dataset_field;
DROP TABLE IF EXISTS stargaze_dataset.dataset_parameter;
DROP TABLE IF EXISTS stargaze_dataset.dataset_hierarchy;
DROP TABLE IF EXISTS stargaze_dataset.materialized_view;
DROP TABLE IF EXISTS stargaze_dataset.dataset_file;
DROP TABLE IF EXISTS stargaze_dataset.dataset;
DROP TABLE IF EXISTS stargaze_dataset.data_source;

-- ==================== 数据源 ====================
CREATE TABLE stargaze_dataset.data_source (
    id            BIGINT       PRIMARY KEY,
    name          VARCHAR(128) NOT NULL,
    type          VARCHAR(32)  NOT NULL,
    config_enc    TEXT,
    pool_config   TEXT,
    status        VARCHAR(16)  NOT NULL DEFAULT 'active',
    created_by    BIGINT,
    updated_by    BIGINT,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted_at    TIMESTAMPTZ
);
CREATE INDEX idx_data_source_name      ON stargaze_dataset.data_source (name);

-- ==================== 数据集 ====================
CREATE TABLE stargaze_dataset.dataset (
    id            BIGINT       PRIMARY KEY,
    name          VARCHAR(128) NOT NULL,
    description   VARCHAR(500),
    source_type   VARCHAR(16)  NOT NULL,
    data_source_id BIGINT,
    definition    TEXT,
    refresh_config TEXT,
    accelerations TEXT,
    status        VARCHAR(16)  NOT NULL DEFAULT 'published',
    version       INTEGER       NOT NULL DEFAULT 1,
    created_by    BIGINT,
    updated_by    BIGINT,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted_at    TIMESTAMPTZ
);
CREATE INDEX idx_dataset_name      ON stargaze_dataset.dataset (name);
CREATE INDEX idx_dataset_source    ON stargaze_dataset.dataset (source_type);

-- ==================== 数据集字段 ====================
CREATE TABLE stargaze_dataset.dataset_field (
    id            BIGINT       PRIMARY KEY,
    dataset_id    BIGINT       NOT NULL,
    origin_name   VARCHAR(128) NOT NULL,
    alias         VARCHAR(128),
    display_name  VARCHAR(200),
    field_type    VARCHAR(16)  NOT NULL,
    data_type     VARCHAR(64)   NOT NULL,
    aggregation   VARCHAR(20),
    source_table  VARCHAR(200),
    is_enabled    BOOLEAN      NOT NULL DEFAULT TRUE,
    semantic_type TEXT,
    format        TEXT,
    dictionary_id BIGINT,
    hidden        BOOLEAN      DEFAULT FALSE,
    ord           INTEGER      NOT NULL DEFAULT 0,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted_at    TIMESTAMPTZ
);
CREATE INDEX idx_dataset_field_dataset ON stargaze_dataset.dataset_field (dataset_id);

-- ==================== 数据集参数 ====================
CREATE TABLE stargaze_dataset.dataset_parameter (
    id            BIGINT       PRIMARY KEY,
    dataset_id    BIGINT       NOT NULL,
    name          VARCHAR(64)  NOT NULL,
    alias         VARCHAR(128),
    data_type     VARCHAR(32)  NOT NULL,
    default_value TEXT,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted_at    TIMESTAMPTZ
);
CREATE INDEX idx_dataset_parameter_dataset ON stargaze_dataset.dataset_parameter (dataset_id);

-- ==================== 数据集维度层级 ====================
CREATE TABLE stargaze_dataset.dataset_hierarchy (
    id            BIGINT       PRIMARY KEY,
    dataset_id    BIGINT       NOT NULL,
    name          VARCHAR(128) NOT NULL,
    levels        TEXT,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted_at    TIMESTAMPTZ
);
CREATE INDEX idx_dataset_hierarchy_dataset ON stargaze_dataset.dataset_hierarchy (dataset_id);

-- ==================== 物化加速配置 ====================
CREATE TABLE stargaze_dataset.materialized_view (
    id            BIGINT       PRIMARY KEY,
    dataset_id    BIGINT       NOT NULL,
    name          VARCHAR(128) NOT NULL,
    target_engine VARCHAR(32),
    refresh_strategy VARCHAR(16),
    refresh_cron  VARCHAR(64),
    last_sync_at  TIMESTAMPTZ,
    status        VARCHAR(16),
    config        TEXT,
    created_by    BIGINT,
    updated_by    BIGINT,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted_at    TIMESTAMPTZ
);
CREATE INDEX idx_materialized_view_dataset ON stargaze_dataset.materialized_view (dataset_id);

-- ==================== 数据集文件(Excel/CSV 上传) ====================
CREATE TABLE stargaze_dataset.dataset_file (
    id            BIGINT       PRIMARY KEY,
    file_name     VARCHAR(255) NOT NULL,
    object_key    VARCHAR(512) NOT NULL,
    content_type  VARCHAR(128),
    size          BIGINT,
    status        VARCHAR(32)  NOT NULL DEFAULT 'active',
    created_by    BIGINT,
    updated_by    BIGINT,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted_at    TIMESTAMPTZ
);
CREATE INDEX idx_dataset_file_object_key ON stargaze_dataset.dataset_file (object_key);
