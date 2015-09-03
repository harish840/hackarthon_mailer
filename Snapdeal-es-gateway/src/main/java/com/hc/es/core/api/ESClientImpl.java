/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hc.es.core.api;

import org.elasticsearch.client.Client;

import com.hc.es.core.base.AdminService;
import com.hc.es.core.base.AnalysisService;
import com.hc.es.core.base.ESClient;
import com.hc.es.core.base.IndexingService;
import com.hc.es.core.base.SearchService;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

/**
 *
 * @author root
 */
public class ESClientImpl implements ESClient {

    private final Client client;

    /**
     * constructor to initialise client
     * @param client
     */
    public ESClientImpl(Client client) {
        this.client = client;
    }

    public ESClientImpl(String hostName, Integer port, String clusterName) {
        this.client = createClient(hostName, port, clusterName);
    }
   
    @Override
    public SearchService getSearchService() {
        return new SearchServiceImpl(client);
    }

    @Override
    public AdminService getAdminService() {
        return new AdminServiceImpl(client);
    }

    @Override
    public AnalysisService getAnalysisService() {
        return new AnalysisServiceImpl(client);
    }

    @Override
    public IndexingService getIndexingService() {
        return new IndexingServiceImpl(client);
    }

    private TransportClient createClient(String hostName, Integer port, String clusterName) {
        Settings settings = ImmutableSettings.settingsBuilder()
                .put("client.transport.sniff", true)
                .put("cluster.name", clusterName)
                .build();
        TransportClient transportClient = new TransportClient(settings);
        transportClient.addTransportAddress(new InetSocketTransportAddress(hostName, port));
        return transportClient;
    }
}
