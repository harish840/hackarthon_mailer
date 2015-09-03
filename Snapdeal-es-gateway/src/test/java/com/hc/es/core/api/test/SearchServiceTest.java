package com.hc.es.core.api.test;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.ElasticsearchException;
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

public class SearchServiceTest extends AbstractElasticsearchIntegrationTest{

    @Test
    public void filterTest() throws ElasticsearchException, IOException {

        Client client = getClient();
        client.prepareIndex("test-nimesh", "test-nimesh", "1")
                .setSource(
                        jsonBuilder().startObject().field("test", "123")
                        .endObject()).execute().actionGet();

        client.prepareIndex("test-nimesh", "test-nimesh", "2")
                .setSource(
                        jsonBuilder().startObject().field("test2", "345")
                        .endObject()).execute().actionGet();
        List<String> idsList = new ArrayList<>();
        idsList.add("1");
        idsList.add("2");

        SearchServiceImpl searchServiceImpl = new SearchServiceImpl(client);
        List<Document> responseDocuments = searchServiceImpl.filter("test-nimesh",
                "test-nimesh", idsList);
        Assert.assertEquals(responseDocuments.size(), 2);
    }

    /*@Test
    public void searchTest() throws ElasticsearchException, IOException {
        Client client = getClient();
        client
                .prepareIndex("test-index", "test-index-type", "1")
                .setSource(
                        jsonBuilder().startObject().field("test", "test")
                        .endObject()).execute().actionGet();

    	
    	Client client = getClient();
        IndexingServiceImpl impl = new IndexingServiceImpl(client);
        Map<String, String> docMap = new HashMap<>();
        docMap.put("test", "test123");
        docMap.put("id", "1");
        Document doc = new Document(docMap);
        List<Document> docList = new ArrayList<>();
        docList.add(doc);
        try {
            impl.index("test-harish", "test-harish12345", docList, null);
        } catch (IOException e) {
        }
        SearchServiceImpl searchServiceImpl = new SearchServiceImpl(client);
        List<Document> responseDocuments = searchServiceImpl.search("test-harish", "test-harish12345", "test");
        Assert.assertNotNull(responseDocuments);
        Assert.assertEquals(responseDocuments.size(), 1);

    }
*/
}
