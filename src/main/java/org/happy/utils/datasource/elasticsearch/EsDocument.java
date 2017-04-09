package org.happy.utils.datasource.elasticsearch;

import java.util.HashMap;
import java.util.Map;

/**
 * ES文档类
 *
 * @author happy
 * @version 17/4/08 下午7:00
 */
public class EsDocument {
    private String id;                          //文档ID
    private Map<String, Object> document;       //文档KV对

    public EsDocument() {
        id = null;
        document = new HashMap<String, Object>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Object> getDocument() {
        return document;
    }

    public void putField(String field, Object value) {
        this.document.put(field, value);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("EsDocument{");
        sb.append("id='").append(id).append('\'');
        sb.append(", document=").append(document);
        sb.append('}');
        return sb.toString();
    }
}
