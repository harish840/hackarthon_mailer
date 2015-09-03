/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hc.es.core.api;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;
import java.util.HashMap;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.Index;
import org.elasticsearch.indices.IndexAlreadyExistsException;

import com.hc.es.core.base.AnalysisService;
import com.hc.es.core.utils.ESConstants;

import java.util.Arrays;

/**
 *
 * @author root
 */
public class AnalysisServiceImpl implements AnalysisService {

    private final Client client;

    /**
     * Constructor to initialise client.
     *
     * @param client
     */
    public AnalysisServiceImpl(Client client) {
        this.client = client;
    }

    /**
     * This function adds a custom analyzer under an index.
     *
     * @param indexName index name
     * @param analyzer custom analyzer
     */
    @Override
    public boolean addAnalyzer(String indexName, Analyzer analyzer) {
        boolean isAnalyzerAdded = true;
        IndicesExistsResponse res = client.admin().indices().prepareExists(indexName).execute().actionGet();
        if (res.isExists()) {
            throw new IndexAlreadyExistsException(new Index(indexName));
        }
        try {
            final CreateIndexRequestBuilder createIndexRequestBuilder = client.admin().indices().prepareCreate(indexName);

            final XContentBuilder analyzerBuilder = jsonBuilder()
                    .startObject()
                    .startObject(ESConstants.ANALYSIS)
                    .startObject(ESConstants.ANALYZER)
                    .startObject(analyzer.getName())
                    .field(ESConstants.TYPE, ESConstants.CUSTOM)
                    .field(ESConstants.TOKENIZER, analyzer.getTokenizer())
                    .field(ESConstants.FILTER, analyzer.getTokenFilter().toArray())
                    .field(ESConstants.CHAR_FILTER, analyzer.getCharFilter().toArray())
                    .endObject()
                    .endObject()
                    .endObject()
                    .endObject();
            createIndexRequestBuilder.setSettings(analyzerBuilder);
            createIndexRequestBuilder.execute().actionGet();
            AdminServiceImpl adminServiceImpl = new AdminServiceImpl(client);
            try {
                adminServiceImpl.createIndex("META-DATA", "indexInfo", null, null);
            } catch (Exception e) {
                System.out.println("Exception while creating index");
            }
            System.out.println("updating meta data");
            HashMap<String, String> analysisInfo = new HashMap<>();
            analysisInfo.put("indexName", indexName);
            analysisInfo.put("analyzerName", analyzer.getName());
            analysisInfo.put(ESConstants.TYPE, ESConstants.CUSTOM);
            analysisInfo.put(ESConstants.TOKENIZER, analyzer.getTokenizer());
            analysisInfo.put(ESConstants.FILTER, Arrays.toString(analyzer.getTokenFilter().toArray()));
            analysisInfo.put(ESConstants.CHAR_FILTER, Arrays.toString(analyzer.getCharFilter().toArray()));
            adminServiceImpl.updateIndex("META-DATA", "indexInfo", "1", analysisInfo);

        } catch (ElasticsearchException | IOException e) {
            isAnalyzerAdded = false;
        }

        return isAnalyzerAdded;
    }

}
