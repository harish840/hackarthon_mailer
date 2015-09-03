/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hc.es.core.base;

import com.hc.es.core.api.Analyzer;

/**
 *
 * @author root
 */
public interface AnalysisService {
	
    /**
     * @param indexName index name
     * @param analyzer custom analyzer
     * @return boolean
     */
    boolean addAnalyzer(String indexName, Analyzer analyzer);
}