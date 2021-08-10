# MongoDB 入门

## 1. 应用场景

传统的关系型数据库在数据操作"三高"需求以及应对Web2.0的网站需求面前显得力不从心

其中三高为：

* High Performance：对数据库高并发的读写需求
* Huge Storage：对海量数据的高效率存储和访问的需求
* High Scalability && High Availability：对数据库的高可扩展性和高可用的需求

而 MongoDB 可应对三高需求，具体应用场景例如

1. 社交场景：使用 MongoDB 存储用户信息，以及用户发表的朋友圈信息，通过地理位置索引实现附近的人、地点等功能
2. 游戏场景，使用 MongoDB 存储游戏用户信息，装备，积分等直接以内嵌文档的形式存储，方便查询、高效率存储和访问
3. 物流场景：使用 MongoDB 存储订单信息，订单状态在运送过程中不断更新，以 MongoDB 内嵌数组的形式来存储，一次查询就能将订单所有的变更查询出来
4. 物联网场景：使用 MongoDB 存储所有接入的智能设备信息，以及设备汇报的日志信息，并对这些信息进行多维度的分析
5. 视频直播：使用 MongoDB 存储用户信息，点赞互动信息等

这些应用场景对数据操作的共同特点是：

1. 数据量大
2. 写入操作频繁（读写操作频繁）
3. 价值较低的数据，对事务性要求不高

对于这样的数据，MongoDB 更适合来实现数据的存储

## 2. 简介

MongoDB 是一个开源的高性能、无模式的**文档型数据库**，当初的设计就是用于简化开发和方便扩展，是 NoSQL 数据库的一种，最像关系型数据库的非关系型数据库。

它支持的数据结构非常松散，是一种类似 JSON 的格式叫 BSON，所以它既可以存储比较复杂的数据类型，又相当的灵活。

MongoDB 中的记录是一个文档，它是由字段和值对（field : value）组成的键值对数据结构。MongoDB 文档类似于 JSON 对象，即一个文档认为就是一个对象。字段的数据类型是字符型，它的值除了使用基本的一些类型外，还可以包括其他文档、普通数组和文档数组

## 3. 数据类型

MongoDB 的最小存储单位就是文档对象，文档对象对应于关系型数据库的行。数据在 MongoDB 中以 BSON（Binary-JSON）文档的格式存储在磁盘上。

BSON 是一种类 JSON 的二进制形式的存储格式，BSON 和 JSON 一样，支持内嵌的文档对象和数组对象，但是BSON 有 JSON 没有的一些数据类型，例如 Date 和 BinData 类型

BSON 采用了类似于 C 结构体的名称、对表示方法，支持内嵌的文档对象和数组对象、具有轻量性，可遍历性，高效性三个特点，可以有效描述非结构化数据和结构化数据。这种格式的优点是灵活性高，缺点是空间利用率不是很理想。

BSON 中，除了基本的 JSON 类型：string, Integer, boolean, double, null, array 和 object，Mongo 还使用了特殊的数据类型，包括 date, object id, binary data, regular expression 和 code。每一个驱动都以特定语言的方式实现了这些类型。

## 4. 特点

MongoDB 主要有以下特典

1. 高性能

MongoDB 提供高性能的数据持久化，特别是对嵌入式数据模型的支持减少了数据库系统上的 I/O 操作

索引支持更快的查询，并且可以包含来自嵌入式文档和数组的键（文本索引解决搜索需求，TTL 索引解决历史数据自动过期需求，地理位置索引可用于构建各种 O2O 应用）

mmapv1, wiredtiger, mongorocks（rocksdb）, in-memory 等多引擎都支持满足各种场景需求

2. 高可用性

MongoDB 的复制工具称为副本集（replica set），它可提供自动故障转移和数据冗余

3. 高扩展性

MongoDB 提供了水平可扩展性作为其核心功能的一部分，分片将数据分布在一组集群上。

从 3.4 开始，MongoDB 支持基于片键创建数据区域。在一个平衡的集群中，MongoDB 将一个区域所覆盖的读写定向到该区域的片上

# MongoDB 安装

使用 docker 安装，方便又快捷~

```bash
docker-compose up -d
```

# MongoDB 常用命令

## 1. 需求

存放文章评论的数据到 MongoDB 中，数据结构参考如下：

数据库：articledb

| 文章评论    | comment        |                    |                           |
| ----------- | -------------- | ------------------ | ------------------------- |
| 字段名称    | 字段含义       | 字段类型           | 备注                      |
| _id         | ID             | ObjectId or String | Mongo 的主键字段          |
| article_id  | 文章ID         | String             |                           |
| content     | 评论内容       | String             |                           |
| userid      | 评论人ID       | String             |                           |
| nickname    | 评论人昵称     | String             |                           |
| create_time | 评论的日期时间 | Date               |                           |
| like_num    | 点赞数         | Int32              |                           |
| replynum    | 回复数         | Int32              |                           |
| state       | 状态           | String             | 0: 不可见, 1: 可见        |
| parent_id   | 上级ID         | String             | 如果为0表示文章的顶级评论 |

## 2. 数据库操作

### 2.1 创建数据库

> 注意：在 MongoDB 中，集合只有在内容插入后才会创建，也就是说，创建集合（数据库）后要再插入一个文档才会创建

```bash
# 选择和创建数据库
use DATABASE_NAME
# 查看有权限查看的数据库命令
show dbs
or
show databases
```

有一些数据库名是保留的，可以直接访问这些有特殊作用的数据库

* **admin**: 从权限的角度来看，这是 root 数据库。要是将一个用户添加到这个数据库，这个用户自动继承所有数据库权限。一些特定的服务器端命令也只能从这个数据库运行，比如列出所有数据库或者关闭服务器
* **local**: 这个数据永远不会被复制，可以用来存储限于本地单台服务器的任何集合
* **config**: 当 Mongo 永远分片设置时，config 数据库在内部使用，用于保存分片的相关信息

### 2.2 删除数据库

主要用来删除已经持久化了的数据库

```bash
db.dropdatabase()
```

## 3. 集合操作

Mongo 中的集合类似数据库中的表

可以显示的创建，也可以隐式的创建

### 3.1 显示创建

```bash
# 基本语法
db.createCollection(NAME)
# 查看当前库中的表
show collections
or
show tables
```

### 3.2 隐式创建

当向集合中插入一个文档时，如果集合不存在，则会自动创建集合

> 一般使用隐式创建即可

### 3.3 删除集合

```shell
db.COLLECTION.drop()
```

返回值：如果成功删除，drop() 方法返回 true，否则返回 false

## 4. 文档 CRUD

文档的数据结构和 JSON 基本一样，所有存储在集合中的数据都是 BSON 格式

### 4.1 插入

1. 单个文档插入

使用 insert() 或者 save() 方法向集合中插入文档

```json
db.COLLECTION.insert (
    <document or array of document>
    {
        writeConcern: <document>
        ordered: <boolean>
    }
)
# 示例
db.comment.insert({"article_id": "100000", "content": "it's ok", "user_id": "1001", "nickname": "Mary", "create_time": new Date(), "like_num": NumberInt(10), "state": null})
```

注意：

如果 comment 集合不存在，则会隐式创建

Mongo 中的数字，默认情况是 double 类型，如果要存整型，必须使用函数 NumberInt(NUM)

插入当前日期使用 new Date()

插入的数据没有指定 _id，会自动生成主键

如果某字段没值，可以复制为 null 或者不写

2. 批量插入

```json
db.COLLECTION.insertMany (
    [<document 1>, <document 2>, ...]
    {
        writeConcern: <document>
        ordered: <boolean>
    }
)
# 示例
db.comment.insertMany([
    {"article_id": "100001", "content": "a", "user_id": "1002", "nickname": "Ma", "create_time": new Date(), "like_num": NumberInt(10), "state": null},
    {"article_id": "100002", "content": "b", "user_id": "1003", "nickname": "Mi", "create_time": new Date(), "like_num": NumberInt(15), "state": null},
    {"article_id": "100003", "content": "c", "user_id": "1004", "nickname": "Mo", "create_time": new Date(), "like_num": NumberInt(1), "state": null},
])
```

### 4.2 查询

```bash
db.COLLECTION.find(<query>, [projection])
# 示例
db.comment.find({article_id: "100001"}, {article_id: 1, _id: 0})
```

| Parameter  | Type     | Description                                                  |
| ---------- | -------- | ------------------------------------------------------------ |
| query      | document | 可选。使用查询运算符指定选择筛选器，若要返回集合中的所有文档，则省略该参数或者传递空文档({}) |
| projection | document | 可选。指定要在与查询筛选器匹配的文档中返回的字段（投影）。若要返回匹配文档中的所有字段则省略 |

### 4.3 更新

```json
db.COLLECTION.update(query, update, options)
or
db.COLLECTION.update (
	<query>,
	<update>,
	{
		upsert: <boolean>,
    	multi: <boolean>,
    	writeConcern: <document>,
    	collation: <document>,
    	arrayFilters: [<filterdocument1>, ...],
		hint: <document | string>
	}
)

# 示例
# 覆盖修改，会覆盖掉整个文档
db.comment.update({article_id: "100000"}, {like_num: NumberInt(1005)})
# 局部修改，解决覆盖修改修改其他字段的问题，使用修改器 $set
db.comment.update({article_id: "100001"}, {$set: {like_num: NumberInt(1005)}})
# 批量修改，默认只修改第一条数据
db.comment.update({article_id: "100001"}, {$set: {like_num: NumberInt(1006)}}, {multi: true})
# 递增/递减
db.comment.update({article_id: "100001"}, {$inc: {like_num: NumberInt(1)}})
```

| Parameter    | Type                    | Description                                                  |
| ------------ | ----------------------- | ------------------------------------------------------------ |
| query        | document                | 更新的选择条件，可以使用与find()方法中相同的查询选择器，类似 sql update 查询的 where |
| update       | document                | 要应用的修改，可以理解为 sql update 内 set 后面的值          |
| upsert       | boolean                 | 可选。如果为 true，则在没有查询条件匹配的文档时创建新文档，默认为 false |
| multi        | boolean                 | 可选。如果为 true，则更新符合查询条件的多个文档，默认为 false |
| writeConcern | document                | 可选。表示写问题的文档。抛出异常的级别。                     |
| collation    | document                | 可选。指定用于操作的校对规则                                 |
| arrayFilters | array                   | 可选。筛选文档的数组，用于确定要为数组字段上的更新操作修改哪些数组元素。 |
| hint         | document<br />or string | 可选。指定用于支持查询谓词的索引的文档或者字符串             |

### 4.4 删除

```json
db.COLLECTION.remove(CONDITION)
# 删除所有数据
db.comment.remove({})
# 删除article_id为100001的数据
db.comment.remove({article_id: "100001"})
```

## 5. 高级查询

### 5.1 统计查询

统计查询使用 count() 方法

```json
db.COLLECTION.count(query, options)
# 示例
db.comment.count({article_id: "100001"})
```

| Parameter | Type     | Description                  |
| --------- | -------- | ---------------------------- |
| query     | document | 查询选择条件                 |
| options   | document | 可选。用于修改计数的额外条件 |

### 5.2 分页列表查询

可以使用 limit() 方法读取指定数量的数据，使用 skip() 方法跳过指定数量的数据

```json
db.COLLECTION.find().skip(NUM).limit(NUM)
# 示例
# 第一页
db.comment.find().skip(0).limit(2)
# 第二页
db.comment.find().skip(2).limit(2)
```

### 5.3 排序查询

sort() 方法对数据进行排序，通过参数指定排序的字段和方式，其中 1 为升序，-1 为降序

```json
db.COLLECTION.find().sort({KEY: 1})
# 示例
db.comment.find().sort({article_id: 1, like_num: 1})
```

### 5.4 比较查询

```json
db.COLLECTION.find({KEY: {$gt: value}}) # KEY > value
db.COLLECTION.find({KEY: {$gte: value}}) # KEY >= value
db.COLLECTION.find({KEY: {$lt: value}}) # KEY < value
db.COLLECTION.find({KEY: {$nt: value}}) # KEY != value
```

### 5.5 包含查询

```json
db.COLLECTION.find({KEY: {$in: ["1", "2"]}}) # KEY里包含1和2的
```

### 5.6 条件连接查询

```json
# and
db.COLLECTION.find({$and: [{query1}, {query2}, ...]})
# or
db.COLLECTION.find({$or: [{query1}, {query2}, ...]})
```

# MongoDB 索引

## 1. 概述

索引支持 Mongo 中高效的执行查询。如果没有索引，Mongo 必须执行全集合扫描，即扫描集合中每个文档，以选择与查询语句匹配的文档。这种扫描全集合的查询效率是非常低的，特别在处理大量的数据时，查询可能花费很长时间。如果查询存在适当的索引，Mongo 可以使用该索引限制必须检查的文档数。

MongoDB 使用 B-tree 作索引

## 2. 索引的类型

### 2.1 单文档索引

Mongo 支持在文档的单个字段上创建用户定义的排序索引，称为单文档索引（Single Field Index）。

对于单个字段索引和排序操作，索引键的排序顺序并不重要，因为 Mongo 可以在任何方向上遍历索引

### 2.2 复合索引

Mongo 还支持多个字段的复合索引

复合索引中列出的字段顺序具有重要意义。例如，如果复合索引由（user_id: 1, score: -1）组成，则索引首先按 user_id 正序排序，然后在每个 user_id 的值内按 score 倒序排序。

### 2.3 其他索引

Mongo 还支持地理空间索引（Geospatial Index），文本索引（Test Index），哈希索引（Hashed Index）

1. 地理空间索引

为了支持对地理空间坐标数据的有效查询，Mongo 提供了两种特殊的索引：返回结果时使用平面几何的二维索引，和返回结果时使用球面几何的二维球面索引。

2. 文本索引

Mongo 提供了一种文本索引类型，支持在集合中搜索字符串内容。这些文本索引不存储特定于语言的停止词（the, a, or 等），而是将集合中的词作为词干，只存储根词

3. 哈希索引

Mongo 也支持基于哈希的索引，它对字段值的哈希值进行索引。

## 3. 索引的管理操作

### 3.1 查看索引

```json
db.COLLECTION.getIndexes()
```

### 3.2 创建索引

```json
db.COLLECTION.createIndex(keys, options)
# 示例
db.comment.createIndex({article_id: 1}, {name: "article_id_idx"})
```

| Parameter | Type     | Description                                              |
| --------- | -------- | -------------------------------------------------------- |
| keys      | document | 包含字段和值对的文档，其中字段是索引键，值是指定升序降序 |
| options   | document | 可选。包含一组控制索引创建的选项文档。                   |

`options`：

| Parameter          | Type     | Description                                                  |
| ------------------ | -------- | ------------------------------------------------------------ |
| background         | boolean  | 建立索引过程需要阻塞其它操作，background 设置为 true 可以指定为后台建立索引 |
| unique             | boolean  | 是否唯一                                                     |
| name               | string   | 索引名称，如果没未指定，Mongo 会通过连接索引的字段名和排序顺序生成 |
| sparse             | Boolean  | 对文档中不存在的字段数据不启动索引，如果为 true，**索引字段不会查询出不包含对应字段的文档** |
| expireAfterSeconds | integer  | 指定一个以秒为单位的数值，设置集合的生存时间                 |
| weights            | document | 索引权重值                                                   |
| default_language   | string   | 对于文本索引，该参数决定了停用词以及词干和词器的规则的列表，默认为英语 |
| language_override  | string   | 对于文本索引，该参数指定了包含在文档中的字段名，语言覆盖默认的language |

### 3.3 删除索引

```json
db.COLLECTION.dropIndex(INDEX) # 删除单个索引
db.COLLECTION.dropIndexes()    # 删除所有索引
# 示例
db.comment.dropIndex("article_id_idx")
```

## 4. 索引的使用

### 4.1 执行计划

分析查询性能通常使用执行计划来查看查询的情况，例如查询的耗时，是否使用索引等

```json
db.COLLECTION.find(query, options).explain(options)
```

# MongoDB 副本集

## 1. 简介

MongoDB 的副本集是一组维护相同数据集的 mongod 服务。副本集可提供冗余和高可用性，是所有生产部署的基础

也可以说，副本集类似于有自动故障恢复功能的主从集群。通俗的讲就是用多台机器进行同一数据的异步同步，从而使多台机器拥有同一数据的多个副本，并且当主库宕机时不需要用户干预的情况下自动切换其他备份服务器做主库。而且还可以利用副本服务器做只读服务器，实现读写分离。

1. 冗余和数据可用性

复制提供冗余并提高数据可用性。通过在不同数据库服务器上提供多个数据副本，复制可提供一定级别的容错功能，以防止丢失单个数据库服务器。

在某些情况下，复制可以提供增加读取性能，因为客户端可以将读取操作发送到不同服务商，在不同数据中心维护数据副本可以增加分布式应用程序的数据位置和可用性。您还可以为专用目的维护其他副本，例如容灾备份

2. MongoDB 中的复制

副本集是一组维护相同数据集的 mongod 实例。副本集包含多个数据承载节点和可选的一个仲裁节点。在承载数据的节点中，一个且仅一个成员被视为主节点，而其他节点被视为从节点。

主节点接收所有写操作。副本集只能有一个主要能够确认具有 {w: "most"} 写入关注的写入。虽然在某些情况下，另一个 mongod 实例可能暂时认为自己也是主要的。主要记录其操作日志中的数据集的所有更改，即 oplog

副本节点复制主节点的 oplog 并将操作应用于其数据集，以使副本节点的数据集反映主节点的数据集。如果主节点挂了，则会重新选举。

3. 主从复制和副本集的区别

主从集群和副本集最大的区别就是副本集没有固定的主节点，整个集群会选出一个主节点，其挂掉后，又在剩下的节点中选出其他节点为主节点。副本集有一个主节点和多个从节点

## 2. 副本集的角色

副本集有三种角色

1. 主要成员：主要接受所有写操作，也就是主节点
2. 副本成员：从主节点通过复制操作维护相同的数据集，即备份数据。不可进行写操作，但可以进行读操作（需要配置），是默认的一种节点类型
3. 仲裁者：不保留任何数据的副本，只有投票选举作用。也可以将仲裁服务器维护为副本集的一部分，即副本成员同时也可以是仲裁者。