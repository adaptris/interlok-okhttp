package com.adaptris.okhttp;

import com.adaptris.core.ConfiguredProduceDestination;
import com.adaptris.core.StandaloneProducer;
import com.adaptris.core.http.HttpProducerExample;
import com.adaptris.core.metadata.RegexMetadataFilter;
import com.adaptris.okhttp.headers.request.CompositeRequestHeaders;
import com.adaptris.okhttp.headers.request.ConfiguredRequestHeaders;
import com.adaptris.okhttp.headers.request.MetadataRequestHeaders;
import com.adaptris.okhttp.headers.response.CompositeResponseHeaders;
import com.adaptris.okhttp.headers.response.MetadataResponseHeaders;
import com.adaptris.util.KeyValuePair;

public class OKHTTPProducerTest extends HttpProducerExample
{
	public OKHTTPProducerTest(String name)
	{
		super(name);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Object retrieveObjectForSampleConfig()
	{
		OKHTTPProducer producer = new OKHTTPProducer(new ConfiguredProduceDestination("http://myhost.com/url/to/post/to"));

		CompositeRequestHeaders headers = new CompositeRequestHeaders(
				new MetadataRequestHeaders(new RegexMetadataFilter().withIncludePatterns("X-HTTP.*").withExcludePatterns("X-NotHttp.*")),
				new ConfiguredRequestHeaders().withHeaders(new KeyValuePair("SOAPAction", "urn:hello")));
		producer.setRequestHeaderProvider(headers);

		producer.setResponseHeaderHandler(new CompositeResponseHeaders(new MetadataResponseHeaders("resp_hdr_")));

		//producer.setAuthenticator(getAuthenticator("username", "password"));

		return new StandaloneProducer(producer);
	}
}
