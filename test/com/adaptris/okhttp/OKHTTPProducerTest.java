package com.adaptris.okhttp;

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

	@Test
	public void testRequestGet() throws Exception
	{
		final ProduceDestination destination = new ConfiguredProduceDestination("https://www.example.com/");
		final AdaptrisMessage message = new DefaultMessageFactory().newMessage();
		final OKHTTPProducer producer = new OKHTTPProducer(destination);

		producer.setMethodProvider(new ConfiguredRequestMethodProvider(RequestMethodProvider.RequestMethod.GET));
		producer.setRequestBody(new StringPayloadDataInputParameter());

		producer.produce(message, destination);
	}

	@Test
	public void testRequestPost() throws Exception
	{
		final ProduceDestination destination = new ConfiguredProduceDestination("https://www.example.com/");
		final AdaptrisMessage message = new DefaultMessageFactory().newMessage();
		final OKHTTPProducer producer = new OKHTTPProducer(destination);

		producer.setMethodProvider(new ConfiguredRequestMethodProvider(RequestMethodProvider.RequestMethod.POST));
		producer.setRequestBody(new StringPayloadDataInputParameter());

		producer.produce(message, destination);
	}
}