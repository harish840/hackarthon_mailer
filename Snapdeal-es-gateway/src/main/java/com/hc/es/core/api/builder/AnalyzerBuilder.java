/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hc.es.core.api.builder;

import static org.elasticsearch.common.settings.ImmutableSettings.settingsBuilder;
import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;

import com.hc.es.core.api.Analyzer;
import com.hc.es.core.utils.ESConstants;

/**
 *
 * @author root
 */
public class AnalyzerBuilder {

    private final Analyzer analyzer ;
    private final Map<String, Map<String, Object>> tokenFiltersDetails;
    private final Map<String, Map<String, Object>> charFiltersDetails;

    /**
     * This function helps build a custom analyzer by accepting custom tokenizer and default tokenfilters and character filters.
     * @param name custom analyzer name
     * @param tokenizer  tokenizer to be used for analyzer
     * @throws IOException
     */
    public AnalyzerBuilder(String name, String tokenizer) throws IOException {
        analyzer = new Analyzer(name, tokenizer, null, null);
        tokenFiltersDetails = new HashMap<>();
        charFiltersDetails = new HashMap<>();
    }

    /**
     * This function helps define custom stopwords while building a custom analyzer
     * @param filterName filter name to build custom analyzer
     * @param stopWords  list of stopwords
     * @return AnalyzerBuilder
     */
    public AnalyzerBuilder addStopWords(String filterName, List<String> stopWords) {
        analyzer.getTokenFilter().add(filterName);
        //TODO: change Object to Generics
        Map<String, Object> map = new HashMap<>();
        map.put(ESConstants.TYPE, "stop");
        map.put("stopwords", stopWords);
        tokenFiltersDetails.put(filterName, map);
        return this;
    }
    
    /**
     * This function helps define custom synonym while building a custom analyzer
     * @param filterName filter name to build custom analyzer.
     * @param synonyms list of synonyms 
     * @return AnalyzerBuilder
     */
    public AnalyzerBuilder addSynonym(String filterName, List<String> synonyms) {
        analyzer.getTokenFilter().add(filterName);
        
        Map<String, Object> map = new HashMap<>();
        map.put(ESConstants.TYPE, "synonym");
        map.put("synonyms", synonyms);
        tokenFiltersDetails.put(filterName, map);
        return this;
    }


    /**
     * This function adds token filter to analyzer builder
     * @param name name of token filter
     * @return
     */
    public AnalyzerBuilder addTokenFilter(String name)
    {
        analyzer.getTokenFilter().add(name);
        return this;
    }

    /**
     * This function adds character filter to analyzer builder
     * @param name name of character filter
     * @return
     */
    public AnalyzerBuilder addCharFilter(String name)
    {
        analyzer.getCharFilter().add(name);
        return this;
    }

    /**
     * This function builds the Setting object to construct the analyzer
     * @return Setting object
     * @throws IOException
     */
    public Settings build() throws IOException {
        XContentBuilder contentBuilder = jsonBuilder()
                .startObject()
                .startObject(ESConstants.ANALYSIS);

        addFilters(contentBuilder);
        addAnalyzer(contentBuilder);

        contentBuilder.endObject()
                .endObject();
        
        Settings settings = settingsBuilder().loadFromSource(contentBuilder.string()).build();
        return settings;
    }

    /**
     * This function adds filter to build a custom analyzer
     * @param contentBuilder
     * @throws IOException
     */
    private void addFilters(XContentBuilder contentBuilder) throws IOException {
        contentBuilder.startObject(ESConstants.FILTER);
        for (Map.Entry<String, Map<String, Object>> entry : tokenFiltersDetails.entrySet()) {
            contentBuilder.startObject(entry.getKey());
            for (Map.Entry<String, Object> entry1 : entry.getValue().entrySet()) {
                if(entry1.getValue() instanceof List)
                {
                    contentBuilder.array(entry1.getKey(), entry1.getValue());
                }
                else
                {
                    contentBuilder.field(entry1.getKey(), entry1.getValue());
                }
            }
            contentBuilder.endObject();
        }
        contentBuilder.endObject();
    }

    /**
     * This funnction makes the json for analyzer build.
     * @param contentBuilder
     * @throws IOException
     * 
     */
    private void addAnalyzer(XContentBuilder contentBuilder) throws IOException {
        contentBuilder
                .startObject(ESConstants.ANALYZER)
                    .startObject(analyzer.getName())
                    	
                        .field(ESConstants.TYPE, ESConstants.CUSTOM)
                        .field(ESConstants.TOKENIZER, analyzer.getTokenizer())
                        .array(ESConstants.FILTER, analyzer.getTokenFilter().toArray())
                        .array(ESConstants.CHAR_FILTER, analyzer.getCharFilter().toArray());
       
        contentBuilder.endObject().endObject();
    }
}