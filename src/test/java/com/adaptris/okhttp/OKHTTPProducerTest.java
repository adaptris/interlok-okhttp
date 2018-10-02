package com.adaptris.okhttp;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.ConfiguredProduceDestination;
import com.adaptris.core.DefaultMessageFactory;
import com.adaptris.core.ProduceDestination;
import com.adaptris.core.common.StringPayloadDataInputParameter;
import com.adaptris.core.http.client.ConfiguredRequestMethodProvider;
import com.adaptris.core.http.client.RequestMethodProvider;

public class OKHTTPProducerTest
{
	private static final String URL = "http://ptsv2.com/t/t5zm1-1537779838/post";
	private static final String PAYLOAD = "Spicy jalapeno bacon ipsum dolor amet tenderloin doner hamburger";
	private static final String RESPONSE = "Thank you for this dump. I hope you have a lovely day!";
	private static final String INVALID = "Only GET and POST methods are supported";

	@Test
	public void testRequestGet() throws Exception
	{
		final ProduceDestination destination = new ConfiguredProduceDestination(URL);
		final AdaptrisMessage message = new DefaultMessageFactory().newMessage();
		final OKHTTPProducer producer = new OKHTTPProducer(destination);

		producer.setMethodProvider(new ConfiguredRequestMethodProvider(RequestMethodProvider.RequestMethod.GET));
		producer.setRequestBody(new StringPayloadDataInputParameter());

		producer.produce(message, destination);

		assertEquals(RESPONSE, message.getContent());
	}

	@Test
	public void testRequestPost() throws Exception
	{
		final ProduceDestination destination = new ConfiguredProduceDestination(URL);
		final AdaptrisMessage message = new DefaultMessageFactory().newMessage();
		message.setContent(PAYLOAD, "UTF-8");
		final OKHTTPProducer producer = new OKHTTPProducer(destination);

		producer.setMethodProvider(new ConfiguredRequestMethodProvider(RequestMethodProvider.RequestMethod.POST));
		producer.setRequestBody(new StringPayloadDataInputParameter());

		producer.produce(message, destination);

		assertEquals(RESPONSE, message.getContent());
	}

	@Test
	public void testRequestPut() throws Exception
	{
		final ProduceDestination destination = new ConfiguredProduceDestination(URL);
		final AdaptrisMessage message = new DefaultMessageFactory().newMessage();
		message.setContent(PAYLOAD, "UTF-8");
		final OKHTTPProducer producer = new OKHTTPProducer(destination);

		producer.setMethodProvider(new ConfiguredRequestMethodProvider(RequestMethodProvider.RequestMethod.PUT));
		producer.setRequestBody(new StringPayloadDataInputParameter());

		producer.produce(message, destination);

		assertEquals(INVALID, message.getContent().trim());
	}
}
