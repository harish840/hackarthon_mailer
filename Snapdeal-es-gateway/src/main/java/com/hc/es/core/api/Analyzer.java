/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hc.es.core.api;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author root
 */
public class Analyzer {
    private final String name;
    private final String tokenizer;
    private final List<String> charFilter;
    private final List<String> tokenFilter;

    /**
     * @param name custom analyzer name
     * @param tokenizer  tokenizer to be used for analysis
     * @param charFilter  list of character filters to be used for analysis
     * @param tokenFilter list of tokenfilters to be used for analysis
     */
    public Analyzer(String name, String tokenizer, List<String> charFilter, List<String> tokenFilter) {
        this.name = name;
        this.tokenizer = tokenizer;
        this.charFilter = charFilter == null ? new ArrayList<String>() : charFilter;
        this.tokenFilter = tokenFilter == null ? new ArrayList<String>() : tokenFilter;
    }

    /**
     * @return List of character filters
     */
    public List<String> getCharFilter() {
        return charFilter;
    }

    /**
     * @return List of token filters
     */
    public List<String> getTokenFilter() {
        return tokenFilter;
    }

    /**
     * @return Tokenizer
     */
    public String getTokenizer() {
        return tokenizer;
    }

    /**
     * @return Name of analyzer
     */
    public String getName() {
        return name;
    }
}
