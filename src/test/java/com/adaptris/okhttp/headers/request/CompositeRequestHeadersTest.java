package com.adaptris.okhttp.headers.request;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import org.junit.Test;
import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.AdaptrisMessageFactory;
import com.adaptris.util.KeyValuePair;
import okhttp3.Request;

public class CompositeRequestHeadersTest {

  @Test
  public void testConstructor() throws Exception {
    CompositeRequestHeaders header = new CompositeRequestHeaders(new ConfiguredRequestHeaders());
    assertEquals(1, header.getProviders().size());
    assertEquals(ConfiguredRequestHeaders.class, header.getProviders().get(0).getClass());
  }


  @Test
  public void testAddProvider() throws Exception {
    CompositeRequestHeaders header = new CompositeRequestHeaders();
    header.addProvider(new ConfiguredRequestHeaders());
    assertEquals(1, header.getProviders().size());
    assertEquals(ConfiguredRequestHeaders.class, header.getProviders().get(0).getClass());
  }

  @Test
  public void testAddHeaders() throws Exception {
    CompositeRequestHeaders header = new CompositeRequestHeaders();
    header.addProvider(new ConfiguredRequestHeaders().withHeaders(new KeyValuePair("X-Interlok", "SNAPSHOT")));
    Request.Builder builder = new Request.Builder();
    AdaptrisMessage msg = AdaptrisMessageFactory.getDefaultInstance().newMessage();
    assertSame(builder, header.addHeaders(msg, builder));
  }

}
