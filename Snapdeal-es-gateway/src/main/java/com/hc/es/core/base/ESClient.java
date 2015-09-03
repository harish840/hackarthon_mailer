/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hc.es.core.base;

/**
 *
 * @author root
 */
public interface ESClient {
    /**
     * function definition to get search service
     * @return Search service
     */
    public SearchService getSearchService();
    
    /**
     * Function definition to get admin service
     * @return Admin service
     */
    public AdminService getAdminService();
    
    /**
     * Function definition to get analysis service
     * @return-Analysis Service
     */
    public AnalysisService getAnalysisService();
   
    /**
     * Function definition to get indexing service
     * @return-Indexing Service
     */
    public IndexingService getIndexingService();
}
