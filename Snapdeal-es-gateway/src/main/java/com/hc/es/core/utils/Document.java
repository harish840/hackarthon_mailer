/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hc.es.core.utils;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author root
 */
public class Document {
    private final Map<String, String> fields;

    /**
     * Initializes an empty hashmap as document
     */
    public Document() {
        this.fields = new HashMap<>();
    }

    /**
     * @param fields Map to initialise fields
     */
    public Document(Map<String, String> fields) {
        this.fields = fields;
    }

    /**
     * @return fields
     */
    public Map<String, String> getFields() {
        return fields;
    }
}
