package com.hc.es.core.api.test;

import org.apache.commons.io.FileUtils;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.node.Node;

import java.io.File;
import java.io.IOException;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

public class EmbeddedElasticSearchServer {

    private static final String DEFAULT_DATA_DIRECTORY = "target/elasticsearch-data";
    //private static final String DEFAULT_DATA_DIRECTORY = "/home/harishchauhan/workspace/elasticsearch-data";

    private final Node node;
    private final String dataDirectory;

    public EmbeddedElasticSearchServer() {
        this(DEFAULT_DATA_DIRECTORY);
    }

    public EmbeddedElasticSearchServer(String dataDirectory) {
        this.dataDirectory = dataDirectory;

        ImmutableSettings.Builder elasticsearchSettings = ImmutableSettings.settingsBuilder()
                .put("http.enabled", "false")
                .put("path.data", dataDirectory);

        node = nodeBuilder()
                .local(true)
                .settings(elasticsearchSettings.build())
                .node();
    }

    public Client getClient() {
        return node.client();
    }

    public void shutdown() {
        node.close();
        deleteDataDirectory();
    }

    private void deleteDataDirectory() {
        try {
            FileUtils.deleteDirectory(new File(dataDirectory));
        } catch (IOException e) {
            throw new RuntimeException("Could not delete data directory of embedded elasticsearch server", e);
        }
    }

    public static void main(String[] args) {
        EmbeddedElasticSearchServer obj = new EmbeddedElasticSearchServer();
        System.out.println("==================================");
        System.out.println(obj.getClient());
        obj.shutdown();

    }
}
