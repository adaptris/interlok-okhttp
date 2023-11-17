package com.adaptris.okhttp.headers.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.AdaptrisMessageFactory;
import com.adaptris.util.KeyValuePair;
import com.adaptris.util.KeyValuePairSet;

import okhttp3.Request;

public class ConfiguredRequestHeadersTest {

  @Test
  public void testSetHeaders() throws Exception {
    ConfiguredRequestHeaders header = new ConfiguredRequestHeaders();
    header.setHeaders(new KeyValuePairSet());
    assertEquals(0, header.getHeaders().size());
    header.withHeaders(new KeyValuePair("X-Interlok", "SNAPSHOT"));
    assertEquals(1, header.getHeaders().size());
  }

  @Test
  public void testAddHeaders() throws Exception {
    ConfiguredRequestHeaders header = new ConfiguredRequestHeaders().withHeaders(new KeyValuePair("X-Interlok", "SNAPSHOT"));
    Request.Builder builder = new Request.Builder();
    AdaptrisMessage msg = AdaptrisMessageFactory.getDefaultInstance().newMessage();
    assertSame(builder, header.addHeaders(msg, builder));
  }

}
