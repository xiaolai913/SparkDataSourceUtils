# SparkDataSourceUtils
为Spark提供常见数据源的读写工具类

## DataSource List
- MySQL
  - 使用[Dbutils](https://commons.apache.org/proper/commons-dbutils/)实现轻量级的数据库读写访问
  - 如需使用datasource连接池方式加载，可在Dbutils基础上结合[DBCP](https://commons.apache.org/proper/commons-dbcp/)实现
- ElasticSearch
  - 目前主要提供了ES 写的Java API实现方法，读取/查询ES的方法可参考[ES JAVA API](https://www.elastic.co/guide/en/elasticsearch/client/java-api/current/java-api.html)
  - REST FUL方式读写ES`待补充`
- Redis
  - 提供Redis连接池对象的创建/释放静态类方法，及读写示例 
- HBase
  - 可直接使用社区提供的三方连接器[spark-hbase-connector](https://github.com/nerdammer/spark-hbase-connector) 
- HDFS
  - 可直接调用Spark的原生API进行HDFS的读写操作
  - DataFrame也提供了方便的[读写parquet](http://spark.apache.org/docs/latest/sql-programming-guide.html#loading-data-programmatically)文件的API
