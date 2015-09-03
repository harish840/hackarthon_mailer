package com.hc.es.core.api.test;

import org.elasticsearch.client.Client;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

public abstract class AbstractElasticsearchIntegrationTest {

    private EmbeddedElasticSearchServer embeddedElasticsearchServer;

    @BeforeClass
    public void startEmbeddedElasticsearchServer() {
        embeddedElasticsearchServer = new EmbeddedElasticSearchServer();
    }

    @AfterClass
    public void shutdownEmbeddedElasticsearchServer() {
        embeddedElasticsearchServer.shutdown();
    }

    /**
     * By using this method you can access the embedded server.
     *
     * @return
     */
    protected Client getClient() {
        return embeddedElasticsearchServer.getClient();
    }

}
