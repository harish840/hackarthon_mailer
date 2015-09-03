package com.hc.es.core.api.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.junit.BeforeClass;

import com.hc.es.core.api.AdminServiceImpl;
import com.hc.es.core.api.AnalysisServiceImpl;
import com.hc.es.core.api.Analyzer;
import com.hc.es.core.api.builder.AnalyzerBuilder;
import com.hc.es.core.utils.ESConstants;

public class AnalysisServiceTest extends AbstractElasticsearchIntegrationTest{

    public void testAnalyzer() throws IOException {

        Client client = getClient();
        Analyzer customAnalyzer = new Analyzer("custom-analyzer", "standard", null, null);
        AnalysisServiceImpl azmpl = new AnalysisServiceImpl(client);
        azmpl.addAnalyzer("test-harish", customAnalyzer);

        AnalyzerBuilder ab = new AnalyzerBuilder("my-custom-analyzer", "custom");
        List<String> words = new ArrayList<>();
        words.add("stop");
        Settings settings = ab.addStopWords("stop-token-filter", words).addCharFilter("standard").build();
        AdminServiceImpl adminser = new AdminServiceImpl(client);
        adminser.createIndex("test-index23", "test-type", null, settings);
    }

}
