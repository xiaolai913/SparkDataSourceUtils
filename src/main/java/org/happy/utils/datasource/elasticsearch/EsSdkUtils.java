package org.happy.utils.datasource.elasticsearch;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;


/**
 * ES工具类
 * <p>
 * 主要利用ES JAVA API, 其他CRUD接口详见: https://www.elastic.co/guide/en/elasticsearch/client/java-api/current/java-api.html
 *
 * @author happy
 * @version 17/4/08 下午7:00
 */
public class EsSdkUtils {

    private static Logger logger = LoggerFactory.getLogger(EsSdkUtils.class);

    private static Client client = null;

    private static String CLUSTER_NAME = "elasticsearch";

    private static String ES_SERVER_HOST = "localhost";

    private static int ES_SERVER_PORT = 9300;

    static {
        try {
            Settings settings = Settings.builder()
                    .put("cluster.name", CLUSTER_NAME)
                    .put("client.transport.sniff", true)
                    .build();

            client = new PreBuiltTransportClient(settings)
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(ES_SERVER_HOST), ES_SERVER_PORT));
            //.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("ES_SERVER_HOST2"), ES_SERVER_PORT2));
        } catch (UnknownHostException e) {
            logger.error("init ES client error: " + e.getMessage());
        }
    }

    /**
     * 批量插入ES文档数据
     *
     * @param index
     * @param type
     * @param docs
     */
    public static void batchInsert(String index,
                                   String type,
                                   List<EsDocument> docs) {
        try {
            if (null == docs || docs.size() == 0) {
                return;
            }
            BulkProcessor bulkProcessor = getBulkProcessor();
            for (EsDocument doc : docs) {
                XContentBuilder builder = jsonBuilder().startObject();
                for (String field : doc.getDocument().keySet()) {
                    builder.field(field, doc.getDocument().get(field));
                }
                builder.endObject();
                if (doc.getDocument().keySet().size() > 0) {
                    if (doc.getId() == null) {
                        bulkProcessor.add(new IndexRequest(index, type).source(builder));
                    } else {
                        bulkProcessor.add(new IndexRequest(index, type, doc.getId()).source(builder));
                    }
                }
            }
            bulkProcessor.awaitClose(5, TimeUnit.MINUTES);     //所有文档都载入bulkprocessor后等待5分钟关闭
        } catch (Exception e) {
            logger.error("insert Es Error: " + ExceptionUtils.getRootCauseMessage(e));
        }
    }

    /**
     * 批量删除带ID的文档数据
     *
     * @param index
     * @param type
     * @param docs
     */
    public static void batchDelete(String index,
                                   String type,
                                   List<EsDocument> docs) {
        try {
            if (null == docs || docs.size() == 0) {
                return;
            }
            BulkProcessor bulkProcessor = getBulkProcessor();
            for (EsDocument doc : docs) {
                if (doc.getId() != null) {
                    bulkProcessor.add(new DeleteRequest(index, type, doc.getId()));
                }
            }
            bulkProcessor.awaitClose(5, TimeUnit.MINUTES);      //等待异步批处理任务完成再关闭bulk
        } catch (Exception e) {
            logger.error("delete Es Error: " + ExceptionUtils.getRootCauseMessage(e));
        }
    }

    /**
     * 获取批量处理器
     *
     * @return
     */
    private static BulkProcessor getBulkProcessor() {
        return BulkProcessor.builder(client,
                new BulkProcessor.Listener() {

                    public void beforeBulk(long executionId,
                                           BulkRequest request) {
                    }

                    public void afterBulk(long executionId,
                                          BulkRequest request,
                                          BulkResponse response) {
                        if (response.hasFailures()) {
                            StringBuilder sb = new StringBuilder();
                            for (BulkItemResponse aResponse : response)
                                sb.append(aResponse.getFailureMessage()).append("\n");
                            logger.error(sb.toString());
                        }
                    }

                    public void afterBulk(long executionId,
                                          BulkRequest request,
                                          Throwable failure) {
                        logger.error("throw failure: " + failure.getMessage());
                    }
                })
                .setBulkActions(200)                                    //每多少个请求批处理一次
                .setBulkSize(new ByteSizeValue(1, ByteSizeUnit.MB))     //多大数据量flush一次bulk
                .setFlushInterval(TimeValue.timeValueSeconds(5))        //不管请求次数有没满,每隔5s批处理一次
                .setConcurrentRequests(1)                               //并发请求数,0代表单一线程处理即同步处理
                .build();
    }

    public static void main(String[] argv) {

        logger.error("test");

        EsDocument esDoc1 = new EsDocument();
        esDoc1.putField("name", "Lucy");
        esDoc1.putField("age", 18);

        EsDocument esDoc2 = new EsDocument();
        esDoc2.putField("name", "Li Lei");
        esDoc2.putField("age", 22);


        List<EsDocument> docs = new LinkedList<EsDocument>();
        docs.add(esDoc1);
        docs.add(esDoc2);

        EsSdkUtils.batchInsert("customer", "vip", docs);

    }
}
