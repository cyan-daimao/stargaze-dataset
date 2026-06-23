# stargaze-dataset

> 观星(Stargaze)数据集平台 —— 数据源接入 + 数据集建模 + 字段定义 + 物化加速 + 元数据刷新

数据集是指标的物理来源。本服务只负责定义**物理字段与基础语义**,业务口径(派生计算、脱敏)上移到 metric 层;
被 metric / query 等服务通过 `/rpc/v1/dataset` 契约引用。

- 技术栈:Spring Boot 3 · Java 21 · MyBatis-Plus · MapStruct · Lombok · fastjson2
- 架构:DDD 四层(adapter / application / domain / infra)
- 元数据库:PostgreSQL(逻辑删除 + jsonb)　物化加速引擎:StarRocks　文件存储:MinIO
- 注册发现 / 配置中心:Nacos　认证:复用公司 `cyan-employee-login`
- 基座:继承公司 `arch` 聚合 pom

## 模块划分

| 模块 | 职责 |
| --- | --- |
| `stargaze-dataset-client` | 对外契约:枚举、DTO、Feign 接口(`/rpc/`),供其他微服务依赖,不引运行时 |
| `stargaze-dataset-application` | 平台实现:四层 + 启动类 `Application`,打包为可执行 jar |

`stargaze` 目录仅为工作目录,聚合 pom 直接继承公司 `arch` 基座,无工程含义。

## 工程结构(application 模块)

```
adapter/        适配层 — HTTP Controller (/api) + RPC Controller (/rpc)
  ├── datasource/http   数据源 Controller、DTO、Convert
  └── dataset/          数据集 Controller(http + rpc)、DTO、Convert
application/    应用层 — Service / BO / Cmd / Convert / Scheduler
  ├── datasource/       数据源应用服务
  └── dataset/          数据集、字段、层级、参数、物化加速应用服务
domain/         领域层 — 充血实体 / 值对象 / 仓储接口 / Query
  ├── datasource/       DataSource + valobj(Column/Database/TableSchema/PoolConfig)
  └── dataset/          Dataset(聚合字段) / DatasetField / Hierarchy / Parameter / MaterializedView
infra/          基础设施 — 持久化(DO/Mapper/Repository 实现) + 连接器 + 配置 + 工具
  ├── connector/        DataSourceConnector 抽象 + MySQL/PG/StarRocks/ClickHouse/Doris 实现 + 工厂
  ├── persistence/      DO / Mapper(MyBatis-Plus) / RepositoryImpl / Convert
  ├── config/           MybatisPlusConfig、DatasetCryptoProperties
  └── util/             AesCryptoUtil、IdUtil
```

领域对象采用充血模型:校验、保存、更新、删除、状态流转(`markError/markActive/markSyncing/...`)均收敛在实体内部,应用层只做编排。

## 核心能力

### 1. 数据源(Datasource)

- CRUD + 连接测试 + 探查(库/schema 列表 → 表列表 → 表结构 → 表采样)
- 适配器工厂 `DataSourceConnectorFactory` 按 `DatasourceType` 分发;`AbstractJdbcConnector` 复用 `information_schema` 模板,子类仅提供驱动与 URL 构造
- 一期支持类型(`DatasourceType`):MySQL、PostgreSQL、StarRocks、ClickHouse、Doris、MaxCompute、Excel、CSV、API
  (其中 MySQL/StarRocks/Doris 共用 MySQL 协议驱动)
- 连接密码内存中为明文,持久化时由 `AesCryptoUtil` 以 **AES-256-GCM** 加密为 `config_enc`,密钥由 KMS / 环境变量 `DATASET_CRYPTO_AES_KEY` 注入

### 2. 数据集(Dataset)

- 来源类型(`DatasetSourceType`):单表 `table` / 自定义 `sql` / 多表 `join` / `excel` / `union`(二期);来源定义以 jsonb 存 `definition`
- CRUD + 元数据刷新(重新采集表结构,新增字段加入、删除字段标记失效)+ 数据预览(采样)
- 版本号自增,逻辑删除连带字段
- 聚合子实体 `DatasetField`:纯物理字段(`origin_name` / `field_type` / `data_type` / 语义标注 / 字典 / 隐藏 / 排序),不含派生计算与脱敏(上移 metric)

### 3. 辅助建模

- **维度层级**(`DatasetHierarchy`):钻取层级定义
- **参数字段**(`DatasetParameter`):查询参数定义
- **物化加速**(`MaterializedView`):物化表存 StarRocks,本对象仅存加速配置;刷新策略 `full`/`incremental` + cron,同步状态机 `idle/syncing/error`
- **元数据刷新调度**(`DatasetRefreshScheduler`):服务内置轻量定时(`@Scheduled`),每日凌晨全量同步结构变更;生产应配合分布式锁避免多实例重复执行

### 4. 对外 RPC 契约

`stargaze-dataset-client` 暴露 Feign 接口 `DatasetClient`(`/rpc/v1/dataset`),不依赖登录态:

| 方法 | 用途 |
| --- | --- |
| `GET /{datasetId}/fields` | 查询数据集全部字段(供 metric 绑定) |
| `GET /{datasetId}/fields/{fieldId}/resolve` | 解析字段(存在性 + 类型推断 + 数据源 ID) |
| `GET /{datasetId}/exists` | 校验数据集是否存在且可用 |

application 模块的 `DatasetRpcController` 实现该契约。

## HTTP 接口一览

| 模块 | 前缀 | 主要操作 |
| --- | --- | --- |
| 数据源 | `/api/v1/datasources` | CRUD、`/{id}/test`、`/{id}/schemas`、`/{id}/schemas/{schema}/tables`、`.../tables/{table}`、`.../tables/{table}/sample` |
| 数据集 | `/api/v1/datasets` | CRUD、`/{id}/refresh`、`/{id}/preview`;`/{id}/hierarchies`、`/{id}/parameters`、`/{id}/materialized-views` |

所有 `/api` 接口统一返回 `com.cyan.arch.common.api.Response`,创建/修改人由 `UserContextHolder` 从登录上下文透传。

## 配置

配置以 `bootstrap*.yml` 引导,运行期配置托管在 Nacos(`arch-base.yaml` + `stargaze-dataset.yaml`,namespace `dev`)。
关键外部依赖(dev profile,均为 `10.0.0.2`):

| 依赖 | 用途 | 默认 |
| --- | --- | --- |
| PostgreSQL | 元数据库 | `10.0.0.2:5432/stargaze` |
| StarRocks | 物化加速目标引擎 | `jdbc:mysql://10.0.0.2:32132` |
| Redis | 缓存 | `10.0.0.2:6379` |
| MinIO | Excel/CSV 文件存储 | `http://10.0.0.2:9000`,bucket `stargaze-dataset` |
| Nacos | 注册发现 + 配置 | `10.0.0.2:8848` |

可覆盖项:`dataset.crypto.aes-key`(`DATASET_CRYPTO_AES_KEY`)、`dataset-refresh.cron`(默认 `0 30 1 * * ?`)。

## 构建与运行

```bash
# 编译打包(application 模块产出可执行 jar:finalName=stargaze-dataset)
mvn clean package -DskipTests

# 本地启动(dev profile)
java -jar stargaze-dataset-application/target/stargaze-dataset.jar

# 监控端点
curl http://localhost:port/actuator/health
```

依赖内网 Nexus(`nexus.cyan.com`)拉取 `arch` 基座与 `cyan-employee-login` 等公司私服制品,首次构建需保证内网连通。

## 数据库表(application 模块对应)

逻辑删除列统一为 `deleted_at`(`null` 存活 / `now()` 删除),枚举以 `@EnumValue` 入库:

| 表 | DO | 说明 |
| --- | --- | --- |
| `datasource` | `DataSourceDO` | 数据源(`config_enc` 加密) |
| `dataset` | `DatasetDO` | 数据集(`definition`/`refresh_config`/`accelerations` jsonb) |
| `dataset_field` | `DatasetFieldDO` | 数据集字段(聚合子表) |
| `dataset_hierarchy` | `DatasetHierarchyDO` | 维度层级 |
| `dataset_parameter` | `DatasetParameterDO` | 参数字段 |
| `materialized_view` | `MaterializedViewDO` | 物化加速配置 |

> DDL 由元数据库统一维护,本仓库不内置 schema 脚本。

## 路线

- 一期(当前):数据源接入 / 数据集建模 / 字段定义 / 物化加速配置 / 元数据定时刷新 / 对外 RPC 契约
- 二期:`union` 数据集组合、MaxCompute / Excel / CSV / API 数据源落地、物化表实际同步调度
