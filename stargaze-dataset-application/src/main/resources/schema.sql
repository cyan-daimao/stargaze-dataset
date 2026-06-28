-- ============================================================================
-- stargaze-dataset 元数据库 Schema (PostgreSQL 15+)
-- ----------------------------------------------------------------------------
-- 服务: stargaze-dataset
-- 说明:
--   1. 对应 DO: DataSourceDO / DatasetDO / DatasetFieldDO / DatasetHierarchyDO
--      / DatasetParameterDO / MaterializedViewDO / DatasetFileDO。
--   2. 主键 id 为 BIGINT,由应用层 MyBatis-Plus IdType.ASSIGN_ID(雪花)赋值,数据库不自增。
--   3. 时间列使用 TIMESTAMPTZ 对齐 OffsetDateTime。
--   4. JSON 字段在 DO 中均为 String 序列化,故使用 TEXT 类型。
--   5. 逻辑删除: deleted_at 为 NULL 表示存活,@TableLogic(value="null", delval="now()")。
--   6. 已移除 workspace / workspace_id 等多租户字段。
-- ============================================================================

CREATE SCHEMA IF NOT EXISTS stargaze_dataset;
SET search_path TO stargaze_dataset;

-- 清理旧表(子表先删)
DROP TABLE IF EXISTS dataset_field        CASCADE;
DROP TABLE IF EXISTS dataset_parameter    CASCADE;
DROP TABLE IF EXISTS dataset_hierarchy    CASCADE;
DROP TABLE IF EXISTS materialized_view    CASCADE;
DROP TABLE IF EXISTS dataset_file         CASCADE;
DROP TABLE IF EXISTS dataset              CASCADE;
DROP TABLE IF EXISTS data_source          CASCADE;

-- ============================================================================
-- 数据源
-- ============================================================================
CREATE TABLE data_source (
    id            BIGINT       PRIMARY KEY,
    name          VARCHAR(128) NOT NULL,
    type          VARCHAR(32)  NOT NULL,
    config_enc    TEXT,                                  -- 连接配置(AES-256-GCM 加密后的 JSON 密文)
    pool_config   TEXT,                                  -- 连接池配置(JSON 序列化字符串)
    status        VARCHAR(16)  NOT NULL DEFAULT 'ACTIVE',
    created_by    BIGINT,
    updated_by    BIGINT,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted_at    TIMESTAMPTZ
);
COMMENT ON TABLE data_source IS '数据源';
COMMENT ON COLUMN data_source.config_enc IS '连接配置(AES-256-GCM 加密后的 JSON 密文)';
COMMENT ON COLUMN data_source.pool_config IS '连接池配置(JSON 序列化字符串)';
COMMENT ON COLUMN data_source.status IS '状态:active/error/inactive';

CREATE UNIQUE INDEX uk_data_source_name ON data_source (name) WHERE deleted_at IS NULL;
CREATE INDEX idx_data_source_status ON data_source (status) WHERE deleted_at IS NULL;

-- ============================================================================
-- 数据集
-- ============================================================================
CREATE TABLE dataset (
    id             BIGINT       PRIMARY KEY,
    name           VARCHAR(128) NOT NULL,
    display_name   VARCHAR(200),                        -- 数据集显示名称
    description    VARCHAR(500),                        -- 描述
    source_type    VARCHAR(16)  NOT NULL,               -- 来源类型: table/sql/join/excel/union
    data_source_id BIGINT,
    definition     TEXT         NOT NULL,               -- 来源定义(JSON 序列化字符串)
    refresh_config TEXT,                                 -- 元数据刷新策略(JSON 序列化字符串)
    accelerations  TEXT,                                 -- 物化加速配置(JSON 序列化字符串)
    status         VARCHAR(16)  NOT NULL DEFAULT 'PUBLISHED',
    version        INT          NOT NULL DEFAULT 1,
    created_by     BIGINT,
    updated_by     BIGINT,
    created_at     TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at     TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted_at     TIMESTAMPTZ
);
COMMENT ON TABLE dataset IS '数据集(分析模型,提供物理字段)';
COMMENT ON COLUMN dataset.display_name IS '数据集显示名称';
COMMENT ON COLUMN dataset.source_type IS '来源类型:table/sql/join/excel/union';
COMMENT ON COLUMN dataset.definition IS '来源定义(JSON 序列化字符串)';
COMMENT ON COLUMN dataset.refresh_config IS '元数据刷新策略(JSON 序列化字符串)';
COMMENT ON COLUMN dataset.accelerations IS '物化加速配置(JSON 序列化字符串)';
COMMENT ON COLUMN dataset.status IS '状态:draft/published/archived';

CREATE UNIQUE INDEX uk_dataset_name ON dataset (name) WHERE deleted_at IS NULL;
CREATE INDEX idx_dataset_source_type ON dataset (source_type) WHERE deleted_at IS NULL;
CREATE INDEX idx_dataset_data_source ON dataset (data_source_id) WHERE deleted_at IS NULL;

-- ============================================================================
-- 数据集字段
-- ============================================================================
CREATE TABLE dataset_field (
    id            BIGINT       PRIMARY KEY,
    dataset_id    BIGINT       NOT NULL,
    origin_name   VARCHAR(128) NOT NULL,               -- 物理字段名
    alias         VARCHAR(128),                        -- 字段别名(SQL 选择别名)
    display_name  VARCHAR(200),                        -- 显示名称
    field_type    VARCHAR(16)  NOT NULL,               -- 字段类型:dimension/measure
    data_type     VARCHAR(64)  NOT NULL,               -- 数据类型(原始 DB 类型串)
    source_table  VARCHAR(200),                        -- 来源表名
    is_enabled    BOOLEAN      NOT NULL DEFAULT TRUE,  -- 是否启用
    semantic_type TEXT,                                -- 基础语义标注(JSON 序列化字符串)
    format        TEXT,                                -- 格式(JSON 序列化字符串)
    dictionary_id BIGINT,
    ord           INT          NOT NULL DEFAULT 0,     -- 排序序号
    created_by    VARCHAR(64),                         -- 创建人
    updated_by    VARCHAR(64),                         -- 修改人
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted_at    TIMESTAMPTZ
);
COMMENT ON TABLE dataset_field IS '数据集字段(维度/度量,物理字段)';
COMMENT ON COLUMN dataset_field.origin_name IS '物理字段名';
COMMENT ON COLUMN dataset_field.field_type IS '字段类型:dimension/measure';
COMMENT ON COLUMN dataset_field.data_type IS '数据类型(原始 DB 类型串)';
COMMENT ON COLUMN dataset_field.is_enabled IS '是否启用';
COMMENT ON COLUMN dataset_field.ord IS '排序序号';

CREATE INDEX idx_dataset_field_dataset ON dataset_field (dataset_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_dataset_field_type ON dataset_field (dataset_id, field_type) WHERE deleted_at IS NULL;

-- ============================================================================
-- 数据集维度层级
-- ============================================================================
CREATE TABLE dataset_hierarchy (
    id         BIGINT       PRIMARY KEY,
    dataset_id BIGINT       NOT NULL,
    name       VARCHAR(128) NOT NULL,                  -- 层级名称
    levels     TEXT         NOT NULL,                  -- 层级定义(JSON 序列化字符串)
    created_by VARCHAR(64),                          -- 创建人
    updated_by VARCHAR(64),                          -- 修改人
    created_at TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted_at TIMESTAMPTZ
);
COMMENT ON TABLE dataset_hierarchy IS '维度层级(钻取)';
COMMENT ON COLUMN dataset_hierarchy.levels IS '层级定义(JSON 序列化字符串:[{field_id,alias}])';

CREATE INDEX idx_dataset_hierarchy_dataset ON dataset_hierarchy (dataset_id) WHERE deleted_at IS NULL;

-- ============================================================================
-- 数据集参数
-- ============================================================================
CREATE TABLE dataset_parameter (
    id            BIGINT       PRIMARY KEY,
    dataset_id    BIGINT       NOT NULL,
    name          VARCHAR(64)  NOT NULL,               -- 参数名
    alias         VARCHAR(128),                        -- 参数别名
    data_type     VARCHAR(32)  NOT NULL,               -- 数据类型
    default_value TEXT,                                -- 默认值
    created_by    VARCHAR(64),                         -- 创建人
    updated_by    VARCHAR(64),                         -- 修改人
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted_at    TIMESTAMPTZ,
    UNIQUE (dataset_id, name)
);
COMMENT ON TABLE dataset_parameter IS '数据集参数字段';
COMMENT ON COLUMN dataset_parameter.name IS '参数名';
COMMENT ON COLUMN dataset_parameter.data_type IS '数据类型';

CREATE INDEX idx_dataset_parameter_dataset ON dataset_parameter (dataset_id) WHERE deleted_at IS NULL;

-- ============================================================================
-- 物化加速配置(物化表实际存 StarRocks)
-- ============================================================================
CREATE TABLE materialized_view (
    id               BIGINT       PRIMARY KEY,
    dataset_id       BIGINT       NOT NULL,
    name             VARCHAR(128) NOT NULL,            -- StarRocks 物化表名
    enabled          BOOLEAN      NOT NULL DEFAULT TRUE,
    target_engine    VARCHAR(32)  NOT NULL DEFAULT 'starrocks',
    target_database  VARCHAR(128),                     -- StarRocks 目标库
    target_table     VARCHAR(128),                     -- StarRocks 目标表
    refresh_strategy VARCHAR(16)  NOT NULL,            -- 刷新策略:full/incremental
    refresh_cron     VARCHAR(64),                      -- 刷新 cron 表达式
    last_sync_at     TIMESTAMPTZ,                      -- 最近同步时间
    status           VARCHAR(16)  NOT NULL DEFAULT 'IDLE', -- 同步状态:IDLE/SYNCING/SUCCESS/ERROR
    config           TEXT,                             -- 配置(JSON 序列化字符串)
    last_error       TEXT,                             -- 最近同步错误
    created_by       BIGINT,
    updated_by       BIGINT,
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted_at       TIMESTAMPTZ
);
COMMENT ON TABLE materialized_view IS '物化加速配置(物化表存 StarRocks)';
COMMENT ON COLUMN materialized_view.enabled IS '是否启用';
COMMENT ON COLUMN materialized_view.target_engine IS '目标引擎,默认 starrocks';
COMMENT ON COLUMN materialized_view.target_database IS 'StarRocks 目标库';
COMMENT ON COLUMN materialized_view.target_table IS 'StarRocks 目标表';
COMMENT ON COLUMN materialized_view.refresh_strategy IS '刷新策略:full/incremental';
COMMENT ON COLUMN materialized_view.status IS '同步状态:IDLE/SYNCING/SUCCESS/ERROR';
COMMENT ON COLUMN materialized_view.config IS '配置(JSON 序列化字符串:字段映射/分区/索引)';
COMMENT ON COLUMN materialized_view.last_error IS '最近同步错误';

CREATE INDEX idx_materialized_view_dataset ON materialized_view (dataset_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_materialized_view_status ON materialized_view (status) WHERE deleted_at IS NULL;

-- ============================================================================
-- 数据集文件(Excel/CSV 上传)
-- ============================================================================
CREATE TABLE dataset_file (
    id            BIGINT       PRIMARY KEY,
    file_name     VARCHAR(255) NOT NULL,               -- 原始文件名
    object_key    VARCHAR(512) NOT NULL,               -- S3 对象 key
    content_type  VARCHAR(128),                        -- 文件类型
    size          BIGINT,                              -- 文件大小(字节)
    status        VARCHAR(16)  NOT NULL DEFAULT 'ACTIVE',
    created_by    BIGINT,
    updated_by    BIGINT,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    deleted_at    TIMESTAMPTZ
);
COMMENT ON TABLE dataset_file IS '数据集文件(Excel/CSV 上传)';
COMMENT ON COLUMN dataset_file.object_key IS 'S3 对象 key';
COMMENT ON COLUMN dataset_file.size IS '文件大小(字节)';

CREATE INDEX idx_dataset_file_object_key ON dataset_file (object_key) WHERE deleted_at IS NULL;
CREATE INDEX idx_dataset_file_status ON dataset_file (status) WHERE deleted_at IS NULL;
