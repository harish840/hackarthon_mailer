package com.hc.es.core.api.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import com.hc.es.core.api.AdminServiceImpl;
import com.hc.es.core.api.IndexingServiceImpl;
import com.hc.es.core.api.SearchServiceImpl;
import com.hc.es.core.api.builder.AnalyzerBuilder;
import com.hc.es.core.base.AdminService;
import com.hc.es.core.base.IndexingService;
import com.hc.es.core.base.SearchService;
import com.hc.es.core.utils.Document;
import com.hc.es.core.utils.FieldInfo;

import java.util.Map;

public class Main {
    private static final boolean createIndex = true;
    private static final boolean indexDocument = true;
    private static final String HOST = "127.0.0.1";
    private static final Integer PORT = 9300;
    
    public static void main(String arg[]) throws IOException, InterruptedException, ExecutionException {
        TransportClient transportClient = getTransportClient();

        AdminService adminService = new AdminServiceImpl(transportClient);
        IndexingService indexingService = new IndexingServiceImpl(transportClient);
        SearchService searchService = new SearchServiceImpl(transportClient);
        
        if (!adminService.isIndexExist("my-index", "my-type")) {
            Settings settings = createCustomAnalyzer();
            createIndex(adminService, settings);
            addMapping(adminService);
        }
        if(indexDocument)
        {
            indexDocument(indexingService);
        }
        List<Document> documents = searchDocument(searchService);
        display(documents);
    }

    public static TransportClient getTransportClient() {
        Settings settings = ImmutableSettings.settingsBuilder()
                .put("client.transport.sniff", true)
                //.put("cluster.name", "cstelk-es-1.3.6")elasticsearch
                .put("cluster.name", "elasticsearch")
                .build();
        TransportClient transportClient = new TransportClient(settings);
        transportClient.addTransportAddress(new InetSocketTransportAddress(HOST, PORT));
        return transportClient;
    }

    public static Settings createCustomAnalyzer() throws IOException {
        AnalyzerBuilder ab = new AnalyzerBuilder("custom-analyzer", "standard");
        List<String> words = new ArrayList<>();
        words.add("this");
        Settings settings = ab.addStopWords("stop-token-filter", words).addCharFilter("html_strip").build();
        return settings;
    }

    public static void createIndex(AdminService adminService, Settings settings) {
            adminService.createIndex("my-index", "my-type", null, settings);
    }

    public static void addMapping(AdminService adminService) throws IOException {
        FieldInfo fieldInfo = new FieldInfo("content", "string", "custom-analyzer");
        List<FieldInfo> fieldList = new ArrayList<>();
        fieldList.add(fieldInfo);
        adminService.addMapping("my-index", "my-type", fieldList);
    }

    public static void indexDocument(IndexingService indexingService) throws IOException {
        HashMap<String, Object> fields = new HashMap<>();
        fields.put("content", "this is he not me");
        fields.put("id", "1");
        Document document = new Document(fields);

        List<Document> documents = new ArrayList<>();
        documents.add(document);
        indexingService.index("my-index", "my-type", documents, null);
    }

    public static List<Document> searchDocument(SearchService searchService) {
        List<Document> searchResponse = searchService.search("my-index", "my-type", "this");
        return searchResponse;
    }

    private static void display(List<Document> documents) {
        for(Document document: documents)
        {
            for(Map.Entry<String, Object> entry : document.getFields().entrySet())
            {
                System.out.println(entry.getKey() +"=>"+ entry.getValue());
            }
        }
    }
}
