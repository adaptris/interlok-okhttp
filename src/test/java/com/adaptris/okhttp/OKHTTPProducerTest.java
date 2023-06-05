package com.adaptris.okhttp;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.DefaultMessageFactory;
import com.adaptris.core.ServiceException;
import com.adaptris.core.StandaloneProducer;
import com.adaptris.core.common.StringPayloadDataInputParameter;
import com.adaptris.core.http.client.ConfiguredRequestMethodProvider;
import com.adaptris.core.http.client.RequestMethodProvider;
import com.adaptris.core.metadata.RegexMetadataFilter;
import com.adaptris.core.stubs.DefectiveMessageFactory;
import com.adaptris.core.stubs.DefectiveMessageFactory.WhenToBreak;
import com.adaptris.core.util.LifecycleHelper;
import com.adaptris.interlok.junit.scaffolding.BaseCase;
import com.adaptris.okhttp.headers.request.MetadataRequestHeaders;
import com.adaptris.okhttp.headers.response.CompositeResponseHeaders;
import com.adaptris.okhttp.headers.response.MetadataResponseHeaders;

public class OKHTTPProducerTest {

  // private static final String URL = "http://ptsv2.com/t/t5zm1-1537779838/post";
  // private static final String RESPONSE = "Thank you for this dump. I hope you have a lovely
  // day!";
  // private static final String INVALID = "Only GET and POST methods are supported";
  // private static final String PAYLOAD = "Spicy jalapeno bacon ipsum dolor amet tenderloin doner
  // hamburger";

  private static String TEST_GET_URL = "okhttp.get.url";
  private static String TEST_GET_EXPECTED = "okhttp.get.expected";

  private static String TEST_POST_URL = "okhttp.post.url";
  private static String TEST_POST_PAYLOAD = "okhttp.post.payload";
  private static String TEST_POST_EXPECTED = "okhttp.post.expected";

  private static String TEST_PUT_URL = "okhttp.put.url";
  private static String TEST_PUT_PAYLOAD = "okhttp.put.payload";
  private static String TEST_PUT_EXPECTED = "okhttp.put.expected";

  @Test
  public void testRequestGet() throws Exception {

    final String url = getConfig(TEST_GET_URL);
    final String expected = getConfig(TEST_GET_EXPECTED);

    final AdaptrisMessage message = new DefaultMessageFactory().newMessage();
    final OKHTTPProducer producer = new OKHTTPProducer().withURL(url);
    message.addMessageHeader("accept", "application/json");

    producer.setMethodProvider(new ConfiguredRequestMethodProvider(RequestMethodProvider.RequestMethod.GET));
    producer.setRequestHeaderProvider(new MetadataRequestHeaders(new RegexMetadataFilter().withIncludePatterns("accept")));
    producer.setRequestBody(new StringPayloadDataInputParameter());
    producer.setResponseHeaderHandler(new MetadataResponseHeaders(""));

    StandaloneProducer sp = new StandaloneProducer(producer);
    try {
      LifecycleHelper.initAndStart(sp);
      sp.doService(message);
      assertTrue(message.getContent().matches(expected));
    } finally {
      LifecycleHelper.stopAndClose(sp);
    }
  }

  @Test
  public void testRequestPost() throws Exception {
    final String url = getConfig(TEST_POST_URL);
    final String expected = getConfig(TEST_POST_EXPECTED);
    final String payload = getConfig(TEST_POST_PAYLOAD);
    final AdaptrisMessage message = new DefaultMessageFactory().newMessage();
    message.setContent(payload, StandardCharsets.UTF_8.name());
    final OKHTTPProducer producer = new OKHTTPProducer().withURL(url);

    producer.setMethodProvider(new ConfiguredRequestMethodProvider(RequestMethodProvider.RequestMethod.POST));
    producer.setRequestBody(new StringPayloadDataInputParameter());
    producer.setResponseHeaderHandler(new CompositeResponseHeaders(new MetadataResponseHeaders("")));

    StandaloneProducer sp = new StandaloneProducer(producer);
    try {
      LifecycleHelper.initAndStart(sp);
      sp.doService(message);
      assertTrue(message.getContent().matches(expected));
    } finally {
      LifecycleHelper.stopAndClose(sp);
    }
  }

  @Test
  public void testRequestPut() throws Exception {
    final String url = getConfig(TEST_PUT_URL);
    final String expected = getConfig(TEST_PUT_EXPECTED);
    final String payload = getConfig(TEST_PUT_PAYLOAD);
    final AdaptrisMessage message = new DefaultMessageFactory().newMessage();
    message.setContent(payload, StandardCharsets.UTF_8.name());
    final OKHTTPProducer producer = new OKHTTPProducer().withURL(url);
    producer.setMethodProvider(new ConfiguredRequestMethodProvider(RequestMethodProvider.RequestMethod.PUT));
    producer.setRequestBody(new StringPayloadDataInputParameter());

    StandaloneProducer sp = new StandaloneProducer(producer);
    try {
      LifecycleHelper.initAndStart(sp);
      sp.doService(message);
      assertTrue(message.getContent().matches(expected));
    } finally {
      LifecycleHelper.stopAndClose(sp);
    }
  }

  @Test
  public void testBroken() throws Exception {
    final String url = getConfig(TEST_POST_URL);
    final String payload = getConfig(TEST_POST_PAYLOAD);

    final AdaptrisMessage message = new DefectiveMessageFactory(WhenToBreak.METADATA_GET).newMessage();
    message.setContent(payload, StandardCharsets.UTF_8.name());
    final OKHTTPProducer producer = new OKHTTPProducer().withURL(url);

    producer.setMethodProvider(new ConfiguredRequestMethodProvider(RequestMethodProvider.RequestMethod.POST));
    producer.setRequestBody(new StringPayloadDataInputParameter());
    producer.setRequestHeaderProvider(new MetadataRequestHeaders());
    producer.setResponseHeaderHandler(new CompositeResponseHeaders(new MetadataResponseHeaders("")));

    StandaloneProducer sp = new StandaloneProducer(producer);
    try {
      LifecycleHelper.initAndStart(sp);

      assertThrows(ServiceException.class, () -> sp.doService(message));
    } finally {
      LifecycleHelper.stopAndClose(sp);
    }
  }

  private String getConfig(String key) {
    return BaseCase.PROPERTIES.getProperty(key);
  }
}
