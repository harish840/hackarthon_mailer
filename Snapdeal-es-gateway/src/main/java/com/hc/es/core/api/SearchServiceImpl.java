/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hc.es.core.api;

import com.google.gson.Gson;
import com.hc.es.core.base.SearchService;
import com.hc.es.core.utils.Document;
import com.hc.es.core.utils.ESConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.ElasticsearchParseException;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.get.GetField;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;
import org.elasticsearch.search.SearchHits;

//import com.s2g.profile.core.type.Candidate;

import org.elasticsearch.ElasticsearchException;

/**
 *
 * @author root
 */
public class SearchServiceImpl implements SearchService {

    private final Client client;
    private final Gson gson;

    /**
     * constructor to initialize the client
     *
     * @param client client
     */
    public SearchServiceImpl(Client client) {
        this.client = client;
        this.gson = new Gson();
    }

    /**
     * This function filters and returns a list of documents.
     *
     * @param indexName name of index
     * @param typeName type
     * @param documentIds list of document ids
     * @return List list of documents
     *
     */
    @Override
    public List<Document> filter(String indexName, String typeName, List<String> documentIds) {
        List<Document> documents = new ArrayList<>();
        for (String documentId : documentIds) {
            documents.add(filterForDocument(indexName, typeName, documentId));
        }
        return documents;
    }

    /**
     * This function filters and returns a document
     *
     * @param indexName name of index
     * @param typeName type
     * @param docId document id
     * @return
     */
    private Document filterForDocument(String indexName, String typeName, String docId) {
        GetResponse response = filterForResponse(indexName, typeName, docId);

        Document document = new Document();
        for (Map.Entry<String, GetField> entry : response.getFields().entrySet()) {
            document.getFields().put(entry.getKey(), entry.getValue().getValue().toString());
        }

        return document;
    }

   /* @Override
    public Candidate filter(String indexName, String typeName, String docId) {
        GetResponse response = filterForResponse(indexName, typeName, docId);
        Gson gson = new Gson();
        String json = response.getSourceAsString();
        return gson.fromJson(json, Candidate.class);
    }*/

    private GetResponse filterForResponse(String indexName, String typeName, String docId) {
        GetRequestBuilder getRequestBuilder = client.prepareGet(indexName, typeName, docId);
        GetResponse response = getRequestBuilder.setOperationThreaded(false).execute().actionGet();
        return response;
    }

    /**
     * This function extracts document off the response and returns a list of
     * documents
     *
     * @param indexName name of index
     * @param typeName type
     * @param keyword keyword to be searched
     */
    @Override
    public List<Document> search(String indexName, String typeName, String keyword) {
        SearchResponse response = searchForResponse(indexName, typeName, keyword);
        return extractDocument(response);
    }

   /* @Override
    public List<Candidate> searchForCandidates(String indexName, String typeName, String keyword) {
        SearchResponse response = searchForResponse(indexName, typeName, keyword);
        return extractCandidates(response);
    }*/

    private SearchResponse searchForResponse(String indexName, String typeName, String keyword) throws ElasticsearchException {
        SearchRequestBuilder searchRequestBuilder = new SearchRequestBuilder(client);
        searchRequestBuilder.setIndices(indexName);
        searchRequestBuilder.setTypes(typeName);
        QueryStringQueryBuilder queryStringQueryBuilder = new QueryStringQueryBuilder(keyword);
        searchRequestBuilder.setQuery(queryStringQueryBuilder);
        SearchResponse response = searchRequestBuilder.execute().actionGet();
        return response;
    }

    /**
     * This function extracts document off the response and returns a list of
     * documents
     *
     * @param response -response of search request
     * @return
     * @throws ElasticsearchParseException
     */
    private List<Document> extractDocument(SearchResponse response) throws ElasticsearchParseException {
        List<Document> resultList = new ArrayList<>();
        SearchHits hits = response.getHits();
        if (hits.getTotalHits() > 0) {
            for (int hitsIndex = 0; hitsIndex < hits.getTotalHits(); hitsIndex++) {
                Map<String, Object> fieldMap = new HashMap<>();
                SearchHit hit = hits.getAt(hitsIndex);
                Map<String, SearchHitField> map = hit.fields();
                fieldMap.put(ESConstants.ID, hit.getId());
                fieldMap.put(ESConstants.INDEX, hit.getIndex());
                fieldMap.put(ESConstants.TYPE, hit.getType());
                fieldMap.put(ESConstants.SCORE, hit.getScore() + "");
                Map<String, Object> hitSOurceFIelds = hit.getSource();
                for (Map.Entry<String, Object> entry : hitSOurceFIelds.entrySet()) {
                    fieldMap.put(entry.getKey(), String.valueOf(entry.getValue()));
                }
                Document doc2 = new Document(fieldMap);
                resultList.add(doc2);
            }
        }
        return resultList;
    }

   /* private List<Candidate> extractCandidates(SearchResponse response) throws ElasticsearchParseException {
        List<Candidate> candidates = new ArrayList<>();
        SearchHits hits = response.getHits();
        if (hits.getTotalHits() > 0) {
            for (int hitsIndex = 0; hitsIndex < hits.getTotalHits(); hitsIndex++) {
                SearchHit hit = hits.getAt(hitsIndex);
                Candidate candidate = gson.fromJson(hit.getSourceAsString(), Candidate.class);
                candidates.add(candidate);
            }
        }
        return candidates;
    }*/
}
