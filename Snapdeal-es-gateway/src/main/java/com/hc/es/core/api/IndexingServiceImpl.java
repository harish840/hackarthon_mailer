/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hc.es.core.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hc.es.core.base.IndexingService;
import com.hc.es.core.utils.Document;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.ListenableActionFuture;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;

//import com.s2g.profile.core.type.Candidate;

/**
 *
 * @author root
 */
public class IndexingServiceImpl implements IndexingService {

    private final Client client;

    /**
     * Contructor to initialize the client
     *
     * @param client
     */
    public IndexingServiceImpl(Client client) {
        this.client = client;
    }

    /**
     * This function indexes a list of documents
     *
     * @param indexName index name
     * @param typeName type
     * @param documents List of documents to be indexed
     * @return List list of IndexResponse
     * @throws java.io.IOException
     */
    @Override
    public List<IndexResponse> index(String indexName, String typeName, List<Document> documents, List<ActionListener> listeners) throws IOException {
        List<IndexResponse> indexResponse = new ArrayList<>();
        for (Document document : documents) {
            indexResponse.add(index(indexName, typeName, document, listeners));
        }
        return indexResponse;
    }

    /**
     * This function indexes a single document
     *
     * @param indexName index name
     * @param typeName type
     * @param document
     * @param listeners
     * @return IndexResponse
     * @throws IOException
     */
    @Override
    public IndexResponse index(String indexName, String typeName, Document document, List<ActionListener> listeners) throws IOException {
        String id = document.getFields().get("id");
    	IndexRequestBuilder indexRequestBuilder
                = id != null ? client.prepareIndex(indexName, typeName, id) : client.prepareIndex(indexName, typeName);
        XContentBuilder contentBuilder = jsonBuilder().startObject().prettyPrint();
        for (Map.Entry<String, String> entry : document.getFields().entrySet()) {
            contentBuilder.field(entry.getKey(), entry.getValue());
        }
        contentBuilder.endObject();
        indexRequestBuilder.setSource(contentBuilder);
        ListenableActionFuture<IndexResponse> actionFuture = indexRequestBuilder
                .execute();

        if (listeners != null && !listeners.isEmpty()) {
            for (ActionListener actionListener : listeners) {
                actionFuture.addListener(actionListener);
            }
        }

        return actionFuture.actionGet();
    }

    private IndexResponse index(String indexName, String typeName, String json, String profileId, List<ActionListener> listeners) throws IOException {
        IndexRequestBuilder indexRequestBuilder
                = (profileId == null) 
                    ? client.prepareIndex(indexName, typeName) 
                    : client.prepareIndex(indexName, typeName, profileId);

        indexRequestBuilder.setSource(json);
        ListenableActionFuture<IndexResponse> actionFuture = indexRequestBuilder
                .execute();

        if (listeners != null && !listeners.isEmpty()) {
            for (ActionListener actionListener : listeners) {
                actionFuture.addListener(actionListener);
            }
        }

        return actionFuture.actionGet();
    }

   /* @Override
    public IndexResponse index(String indexName, String typeName, Candidate candidate, List<ActionListener> listeners) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(candidate);
        return index(indexName, typeName, json, candidate.getProfileId(), listeners);
    }*/

}
