/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hc.es.core.base;

import java.io.IOException;
import java.util.List;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.index.IndexResponse;

import com.hc.es.core.utils.Document;

/**
 *
 * @author root
 */
public interface IndexingService {

    /**
     * @param indexName index name
     * @param typeName type
     * @param documents List of documents to be indexed
     * @param listeners
     * @return
     * @throws IOException
     */
    public List<IndexResponse> index(String indexName, String typeName, List<Document> documents, List<ActionListener> listeners) throws IOException;

    public IndexResponse index(String indexName, String typeName, Document document, List<ActionListener> listeners) throws IOException;

}
