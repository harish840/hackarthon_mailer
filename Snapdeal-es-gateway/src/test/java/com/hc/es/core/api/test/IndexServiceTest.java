package com.hc.es.core.api.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.junit.BeforeClass;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.hc.es.core.api.IndexingServiceImpl;
import com.hc.es.core.api.SearchServiceImpl;
import com.hc.es.core.utils.Document;
import com.hc.es.core.utils.ESConstants;
//import com.s2g.profile.core.type.Candidate;

public class IndexServiceTest extends AbstractElasticsearchIntegrationTest{


    @Test
    public void testIndex() {
    	Client client = getClient();
        IndexingServiceImpl impl = new IndexingServiceImpl(getClient());
        Map<String, Object> docMap = new HashMap<>();
        docMap.put("test", "test123");
        docMap.put("id", "1");
        Document doc = new Document(docMap);
        List<Document> docList = new ArrayList<>();
        docList.add(doc);
        try {
            impl.index("test-harish", "test-harish12345", docList, null);
        } catch (IOException e) {
        }
        GetResponse fields = getClient().prepareGet("test-harish", "test-harish12345", "1")
                .execute().actionGet();
       Assert.assertEquals(fields.getSource().get("test"), "test123");
    }

    
}
