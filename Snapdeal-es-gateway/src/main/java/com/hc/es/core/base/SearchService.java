/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hc.es.core.base;

import java.util.List;

import com.hc.es.core.utils.Document;

/**
 *
 * @author root
 */
public interface SearchService {
	
    /**
     * @param indexName index name
     * @param typeName type
     * @param documentIds list of documents to filter
     * @return
     */
    public List<Document> filter(String indexName, String typeName, List<String> documentIds);


    /**
     * @param indexName index name
     * @param typeName type
     * @param keyword keyword to search
     * @return
     */
    public List<Document> search(String indexName, String typeName, String keyword);
    
}
