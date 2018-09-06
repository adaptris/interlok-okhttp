package com.adaptris.okhttp;

import java.io.IOException;
import java.net.URL;

import com.adaptris.annotation.AdapterComponent;
import com.adaptris.annotation.ComponentProfile;
import com.adaptris.annotation.DisplayOrder;
import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.NullConnection;
import com.adaptris.core.ProduceDestination;
import com.adaptris.core.ProduceException;
import com.adaptris.core.http.client.net.HttpProducer;
import com.adaptris.core.http.client.RequestMethodProvider.RequestMethod;
import com.thoughtworks.xstream.annotations.XStreamAlias;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A HTTP producer that uses the OKHTTP; in time it will (hopefully) make use
 * of some of the more advanced features of OKHTTP.
 */
@XStreamAlias("okhttp-http-producer")
@AdapterComponent
@ComponentProfile(summary = "Make a HTTP request to a remote server using the OKHTTP library", tag = "producer,http,https", metadata =
{
	"adphttpresponse"

}, recommended =
{
	NullConnection.class
})
@DisplayOrder(order =
{
	"authenticator", "allowRedirect", "ignoreServerResponseCode", "alwaysSendPayload", "methodProvider", "contentTypeProvider", "requestHeaderProvider",
	"requestBody", "responseHeaderHandler", "responseBody"
})
public class OKHTTPProducer extends HttpProducer
{
	private final OkHttpClient client = new OkHttpClient();

	public OKHTTPProducer()
	{
		super();
	}

	public OKHTTPProducer(final ProduceDestination destination)
	{
		setDestination(destination);
	}

	@Override
	public void produce(final AdaptrisMessage msg, final ProduceDestination destination) throws ProduceException
	{
		doRequest(msg, destination, defaultTimeout());
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	protected AdaptrisMessage doRequest(final AdaptrisMessage msg, final ProduceDestination dest, final long timeout) throws ProduceException
	{
		try
		{
			URL url = new URL(dest.getDestination(msg));
			RequestMethod method = getMethod(msg);
			MediaType type = MediaType.parse(getContentTypeProvider().getContentType(msg));

			post(url, type, null);
		}
		catch (final Exception e)
		{
			throw new ProduceException(e);
		}
		return msg;
	}

	private String post(final URL url, final MediaType type, final String data) throws IOException
	{
		RequestBody body = RequestBody.create(type, data);
		Request request = new Request.Builder().url(url).post(body).build();
		try (Response response = client.newCall(request).execute())
		{
			return response.body().string();
		}
	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public void prepare()
	{
		// empty method
	}
}
