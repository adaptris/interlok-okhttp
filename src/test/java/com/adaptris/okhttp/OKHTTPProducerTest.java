package com.adaptris.okhttp;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.ConfiguredProduceDestination;
import com.adaptris.core.DefaultMessageFactory;
import com.adaptris.core.ProduceDestination;
import com.adaptris.core.ServiceException;
import com.adaptris.core.StandaloneProducer;
import com.adaptris.core.common.StringPayloadDataInputParameter;
import com.adaptris.core.http.client.ConfiguredRequestMethodProvider;
import com.adaptris.core.http.client.RequestMethodProvider;
import com.adaptris.core.stubs.DefectiveMessageFactory;
import com.adaptris.core.stubs.DefectiveMessageFactory.WhenToBreak;
import com.adaptris.core.util.LifecycleHelper;
import com.adaptris.okhttp.headers.request.MetadataRequestHeaders;
import com.adaptris.okhttp.headers.response.CompositeResponseHeaders;
import com.adaptris.okhttp.headers.response.MetadataResponseHeaders;

public class OKHTTPProducerTest {
  private static final String URL = "http://ptsv2.com/t/t5zm1-1537779838/post";
  private static final String PAYLOAD = "Spicy jalapeno bacon ipsum dolor amet tenderloin doner hamburger";
  private static final String RESPONSE = "Thank you for this dump. I hope you have a lovely day!";
  private static final String INVALID = "Only GET and POST methods are supported";

  @Test
  public void testRequestGet() throws Exception {
    final ProduceDestination destination = new ConfiguredProduceDestination(URL);
    final AdaptrisMessage message = new DefaultMessageFactory().newMessage();
    final OKHTTPProducer producer = new OKHTTPProducer(destination);

    producer.setMethodProvider(new ConfiguredRequestMethodProvider(RequestMethodProvider.RequestMethod.GET));
    producer.setRequestBody(new StringPayloadDataInputParameter());
    producer.setResponseHeaderHandler(new MetadataResponseHeaders(""));

    StandaloneProducer sp = new StandaloneProducer(producer);
    try {
      LifecycleHelper.initAndStart(sp);
      sp.doService(message);
      assertEquals(RESPONSE, message.getContent());
    } finally {
      LifecycleHelper.stopAndClose(sp);

    }

  }

  @Test
  public void testRequestPost() throws Exception {
    final ProduceDestination destination = new ConfiguredProduceDestination(URL);
    final AdaptrisMessage message = new DefaultMessageFactory().newMessage();
    message.setContent(PAYLOAD, "UTF-8");
    final OKHTTPProducer producer = new OKHTTPProducer(destination);

    producer.setMethodProvider(new ConfiguredRequestMethodProvider(RequestMethodProvider.RequestMethod.POST));
    producer.setRequestBody(new StringPayloadDataInputParameter());
    producer.setResponseHeaderHandler(new CompositeResponseHeaders(new MetadataResponseHeaders("")));

    StandaloneProducer sp = new StandaloneProducer(producer);
    try {
      LifecycleHelper.initAndStart(sp);
      sp.doService(message);
      assertEquals(RESPONSE, message.getContent());
    } finally {
      LifecycleHelper.stopAndClose(sp);
    }
  }

  @Test
  public void testRequestPut() throws Exception {
    final ProduceDestination destination = new ConfiguredProduceDestination(URL);
    final AdaptrisMessage message = new DefaultMessageFactory().newMessage();
    message.setContent(PAYLOAD, "UTF-8");
    final OKHTTPProducer producer = new OKHTTPProducer(destination);

    producer.setMethodProvider(new ConfiguredRequestMethodProvider(RequestMethodProvider.RequestMethod.PUT));
    producer.setRequestBody(new StringPayloadDataInputParameter());
    StandaloneProducer sp = new StandaloneProducer(producer);
    try {
      LifecycleHelper.initAndStart(sp);
      sp.doService(message);
      assertEquals(INVALID, message.getContent().trim());
    } finally {
      LifecycleHelper.stopAndClose(sp);

    }
  }

  @Test(expected = ServiceException.class)
  public void testBroken() throws Exception {
    final ProduceDestination destination = new ConfiguredProduceDestination(URL);
    final AdaptrisMessage message = new DefectiveMessageFactory(WhenToBreak.METADATA_GET).newMessage();
    message.setContent(PAYLOAD, "UTF-8");
    final OKHTTPProducer producer = new OKHTTPProducer(destination);

    producer.setMethodProvider(new ConfiguredRequestMethodProvider(RequestMethodProvider.RequestMethod.POST));
    producer.setRequestBody(new StringPayloadDataInputParameter());
    producer.setRequestHeaderProvider(new MetadataRequestHeaders());
    producer.setResponseHeaderHandler(new CompositeResponseHeaders(new MetadataResponseHeaders("")));

    StandaloneProducer sp = new StandaloneProducer(producer);
    try {
      LifecycleHelper.initAndStart(sp);
      sp.doService(message);
    } finally {
      LifecycleHelper.stopAndClose(sp);
    }


  }
}
