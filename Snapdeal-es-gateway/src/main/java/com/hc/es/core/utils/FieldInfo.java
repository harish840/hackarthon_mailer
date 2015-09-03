/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hc.es.core.utils;

/**
 *
 * @author root
 */
public class FieldInfo {
    String name;
    String type;
    String analyser;

    /**
     * @param name field info name
     * @param type type
     * @param analyser analyser
     */
    public FieldInfo(String name, String type, String analyser) {
        this.name = name;
        this.type = type;
        this.analyser = analyser;
    }

    /**
     * @return String name of field
     */
    public String getName() {
        return name;
    }

    /**
     * @return String type of field
     */
    public String getType() {
        return type;
    }

    /**
     * @return String analyser for field
     */
    public String getAnalyser() {
        return analyser;
    }
    
    
}
