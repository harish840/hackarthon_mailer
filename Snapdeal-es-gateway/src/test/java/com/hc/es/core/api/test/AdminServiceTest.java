package com.hc.es.core.api.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsRequest;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.hppc.cursors.ObjectObjectCursor;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.hc.es.core.api.AdminServiceImpl;
import com.hc.es.core.api.IndexingServiceImpl;
import com.hc.es.core.utils.Document;
import com.hc.es.core.utils.FieldInfo;

public class AdminServiceTest extends AbstractElasticsearchIntegrationTest{
	
	public void creteTestIndex(Client client){
		try{
			DeleteIndexRequestBuilder delIdx = client.admin().indices()
	                .prepareDelete("test-harish");
			 delIdx.execute().actionGet();
		}catch(Exception e){
			
		}
        AdminServiceImpl adminClient = new AdminServiceImpl(client);
        adminClient.createIndex("test-harish", "test-harish", null);
	}

    @Test
    public void testCreateIndex() {
    	Client client = getClient();
    	creteTestIndex(client);
        final IndicesExistsResponse res = getClient().admin().indices()
                .prepareExists("test-harish").execute().actionGet();
        Assert.assertEquals(res.isExists(), true);
    }

    @Test
    public void addMappingTest() throws InterruptedException,
            ExecutionException, IOException {
    	
    	Client client = getClient();
        FieldInfo fieldInfo = new FieldInfo("test", "string", "standard");
        List<FieldInfo> fieldList = new ArrayList<>();
        fieldList.add(fieldInfo);
        creteTestIndex(client);
        AdminServiceImpl adminClient = new AdminServiceImpl(client);
        try {
            adminClient.addMapping("test-harish", "test-harish", fieldList);
        } catch (IOException e) {

        }
        boolean mappingFound = false;
        GetMappingsResponse res = client.admin().indices()
                .getMappings(new GetMappingsRequest().indices("test-harish"))
                .get();
        ImmutableOpenMap<String, MappingMetaData> mapping = res.mappings().get(
                "test-harish");
        for (ObjectObjectCursor<String, MappingMetaData> c : mapping) {
            if (c.key.equalsIgnoreCase("test-harish")) {
                Map<String, Object> m = c.value.getSourceAsMap();
                for (Map.Entry<String, Object> entry : m.entrySet()) {
                    if (entry.getKey().equalsIgnoreCase("properties")) {
                        Map<String, String> propertiesMap = (Map) entry
                                .getValue();
                        if (propertiesMap.containsKey("test")) {
                            mappingFound = true;
                        }
                    }
                }
            }
        }

        Assert.assertTrue(mappingFound);
    }

    @Test
    public void updateIndexTest() throws IOException {
    	Client client = getClient();
    	creteTestIndex(client);
    	
        Map<String, String> m = new HashMap<>();
        m.put("school", "carmel");
        m.put("id", "24");
        Document document2 = new Document(m);
        List<Document> docs1 = new ArrayList<>();
        docs1.add(document2);
        IndexingServiceImpl in = new IndexingServiceImpl(client);
        in.index("test-harish", "test-harish", docs1, null);
        HashMap<String, String> fieldsMap = new HashMap<>();
        fieldsMap.put("school", "kv");
        AdminServiceImpl adminService = new AdminServiceImpl(client);
        adminService.updateIndex("test-harish", "test-harish", "24", fieldsMap);

        GetResponse fields = client.prepareGet("test-harish", "test-harish", "24")
                .execute().actionGet();
        Assert.assertEquals(fields.getSource().get("school"), "kv");

    }

}
