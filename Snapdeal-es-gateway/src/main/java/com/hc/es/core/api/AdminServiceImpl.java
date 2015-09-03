/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hc.es.core.api;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.ListenableActionFuture;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.indices.IndexAlreadyExistsException;
import org.elasticsearch.indices.IndicesLifecycle;

import com.hc.es.core.base.AdminService;
import com.hc.es.core.utils.ESConstants;
import com.hc.es.core.utils.FieldInfo;

import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;

/**
 *
 * @author root
 */
public class AdminServiceImpl implements AdminService {

    private final Client client;

    /**
     * @param client Constructor to initialize the client
     */
    public AdminServiceImpl(Client client) {
        this.client = client;
    }

    /**
     * This function creates an index for given index name and type.Function
     * also accepts implementation of setting as parameter for custom index
     * creation.
     *
     * @param indexName index name
     * @param typeName type
     * @param actionListeners list of listeners
     * @param settings create an implementation of settings.
     */
    @Override
    public void createIndex(String indexName, String typeName, List<ActionListener> actionListeners, Settings settings) throws IndexAlreadyExistsException {
        CreateIndexRequestBuilder createIndexRequestBuilder = client
                .admin()
                .indices()
                .prepareCreate(indexName);

        if (settings != null) {
            createIndexRequestBuilder.setSettings(settings);
        }

        ListenableActionFuture<CreateIndexResponse> actionFuture = createIndexRequestBuilder
                .execute();

        if (actionListeners != null && !actionListeners.isEmpty()) {
            for (ActionListener actionListener : actionListeners) {
                actionFuture.addListener(actionListener);
            }
        }

        actionFuture.actionGet();
    }

    /**
     * This function creates an index for given index name and type.
     *
     * @param indexName index name
     * @param typeName type
     * @param actionListeners list of listeners
     */
    @Override
    public void createIndex(String indexName, String typeName, List<ActionListener> actionListeners) throws IndexAlreadyExistsException {
        createIndex(indexName, typeName, actionListeners, null);
    }

    /**
     * This function adds mapping to a given index.
     *
     * @param indexName index name
     * @param typeName type
     * @param mappings list of mappings
     */
    @Override
    public void addMapping(String indexName, String typeName, List<FieldInfo> mappings) throws IOException {
        XContentBuilder xContentBuilder = jsonBuilder()
                .startObject()
                .startObject(typeName)
                .startObject(ESConstants.PROPERTIES);

        for (FieldInfo entry : mappings) {
            xContentBuilder.startObject(entry.getName())
                    .field(ESConstants.TYPE, entry.getType())
                    .field(ESConstants.ANALYZER, entry.getAnalyser())
                    .endObject();
        }

        xContentBuilder
                .endObject()
                .endObject()
                .endObject();

        client.admin()
                .indices()
                .preparePutMapping(indexName)
                .setType(typeName)
                .setSource(xContentBuilder)
                .execute().actionGet();
    }

    @Override
    public void deleteIndex(String indexName, String typeName, List<IndicesLifecycle.Listener> listeners) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void deleteMapping(String indexName, String typeName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * This function deletes a document from an index.
     *
     * @param indexName index name
     * @param type type
     * @param docId document id
     *
     */
    @Override
    public void deleteIndex(String indexName, String type, String docId) throws Exception {
        DeleteResponse response = client.prepareDelete(indexName, type, docId)
                .execute()
                .actionGet();
        if (!response.isFound()) {
            throw new Exception("Document not found");
        }
    }

    /**
     * This function updates a document from an index.
     *
     * @param indexName index name
     * @param typeName type
     * @param id document id
     * @param updateObject
     * @throws java.io.IOException
     */
    @Override
    public void updateIndex(String indexName, String typeName, String id, HashMap<String, String> updateObject) throws ElasticsearchException, IOException {
        XContentBuilder xContentBuilder = jsonBuilder();
        for (Map.Entry<String, String> entry : updateObject.entrySet()) {
            xContentBuilder.startObject()
                    .field(entry.getKey(), entry.getValue())
                    .endObject();
        }
        client.prepareUpdate(indexName, typeName, id)
                .setDoc(xContentBuilder)
                .get();

    }

    @Override
    public Boolean isIndexExist(String indexName, String typeName) {
        client.admin().cluster().prepareHealth().setWaitForYellowStatus().execute().actionGet();
        ClusterStateResponse response
                = client.admin().cluster().prepareState().execute().actionGet();
        Boolean hasIndex = response.getState().metaData().hasIndex(indexName);
        return hasIndex;
    }

}
