package com.hc.es.core.api.test;

import java.io.IOException;

import org.testng.Assert;
import org.testng.annotations.Test;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import org.elasticsearch.action.get.GetResponse;

public class SimpleElasticsearchTest extends AbstractElasticsearchIntegrationTest {

    @Test
    public void indexSimpleDocument() throws IOException {
        getClient().prepareIndex("myindex", "document", "1")
                .setSource(jsonBuilder().startObject().field("test", "123").endObject())
                .execute()
                .actionGet();

        GetResponse fields = getClient().prepareGet("myindex", "document", "1").execute().actionGet();
        Assert.assertEquals(fields.getSource().get("test"), "123");
    }
}
