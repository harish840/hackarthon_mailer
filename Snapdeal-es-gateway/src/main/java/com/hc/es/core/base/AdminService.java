/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hc.es.core.base;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.indices.IndexAlreadyExistsException;
import org.elasticsearch.indices.IndicesLifecycle;

import com.hc.es.core.utils.FieldInfo;

/**
 *
 * @author root
 */
public interface AdminService {

    /**
     * @param indexName name of index
     * @param typeName type
     * @param listeners list of listeners to be added
     * @throws IndexAlreadyExistsException
     */
    public void createIndex(String indexName, String typeName, List<ActionListener> listeners) throws IndexAlreadyExistsException;

    
    /**
     * @param indexName name of index
     * @param typeName type
     * @param listeners list of listeners to be added
     * @param settings create an  implementation of settings.
     * @throws IndexAlreadyExistsException
     */
    public void createIndex(String indexName, String typeName, List<ActionListener> listeners, Settings settings) throws IndexAlreadyExistsException;

    /**
     * @param indexName name of index
     * @param typeName type
     * @param mappings mappings to be added to index
     * @throws IOException
     */
    public void addMapping(String indexName, String typeName, List<FieldInfo> mappings) throws IOException;

    /**
     * @param indexName name of index
     * @param typeName type
     * @param listeners list of listeners to be added
     */
    public void deleteIndex(String indexName, String typeName, List<IndicesLifecycle.Listener> listeners);
    
    /**
     * @param indexName name of index
     * @param type type
     * @param docId id of document to be deleted
     * @throws Exception
     */
    public void deleteIndex(String indexName,String type,String docId) throws Exception;

    /**
     * @param indexName  name of index
     * @param typeName type
     */
    public void deleteMapping(String indexName, String typeName);
    
    
    /**
     * @param indexName name of index
     * @param typeName type
     * @param id document id
     * @param updateFields
     * @throws java.io.IOException
     */
    public void updateIndex(String indexName,String typeName,String id,HashMap<String,String> updateFields)throws ElasticsearchException, IOException;
    
    
    public Boolean isIndexExist(String indexName, String typeName);
}
