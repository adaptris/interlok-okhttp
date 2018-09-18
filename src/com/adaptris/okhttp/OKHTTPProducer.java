package com.adaptris.okhttp;

import java.net.URL;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adaptris.annotation.AdapterComponent;
import com.adaptris.annotation.AdvancedConfig;
import com.adaptris.annotation.ComponentProfile;
import com.adaptris.annotation.DisplayOrder;
import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.NullConnection;
import com.adaptris.core.ProduceDestination;
import com.adaptris.core.ProduceException;
import com.adaptris.core.common.PayloadStreamInputParameter;
import com.adaptris.core.common.PayloadStreamOutputParameter;
import com.adaptris.core.common.StringPayloadDataOutputParameter;
import com.adaptris.core.http.client.RequestMethodProvider.RequestMethod;
import com.adaptris.core.http.client.net.HttpProducer;
import com.adaptris.core.util.Args;
import com.adaptris.interlok.config.DataInputParameter;
import com.adaptris.interlok.config.DataOutputParameter;
import com.adaptris.okhttp.headers.OKHTTPDiscardResponseHeaders;
import com.adaptris.okhttp.headers.OKHTTPNoRequestHeaders;
import com.thoughtworks.xstream.annotations.XStreamAlias;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

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
public class OKHTTPProducer extends HttpProducer<Request.Builder, Response>
{
	private static final transient Logger logger = LoggerFactory.getLogger(OKHTTPProducer.class);

	private final OkHttpClient client = new OkHttpClient();

	@Valid
	@AdvancedConfig
	private DataInputParameter<String> requestBody;

	@Valid
	@AdvancedConfig
	private DataOutputParameter<String> responseBody;

//	@Valid
//	@AdvancedConfig
//	@NotNull
//	@AutoPopulated
//	private HttpAuthenticator authenticator = new NoAuthentication();

	public OKHTTPProducer()
	{
		super();
		setRequestHeaderProvider(new OKHTTPNoRequestHeaders());
		setResponseHeaderHandler(new OKHTTPDiscardResponseHeaders());
	}

	public OKHTTPProducer(final ProduceDestination destination)
	{
		this();
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
	@SuppressWarnings("hiding") 
	protected AdaptrisMessage doRequest(final AdaptrisMessage msg, final ProduceDestination dest, final long timeout) throws ProduceException
	{
		logger.info("OKHTTP producer request");
		try
		{
			final URL url = new URL(dest.getDestination(msg));

			logger.debug("URL = " + url);
			final RequestBody requestBody = RequestBody.create(MediaType.parse(getContentTypeProvider().getContentType(msg)), this.requestBody.extract(msg));

			final Request.Builder rb = new Request.Builder().url(url);

			getRequestHeaderProvider().addHeaders(msg, rb);

			final RequestMethod method = getMethod(msg);
			switch (method)
			{
				case DELETE:
					logger.trace("HTTP DELETE");
					rb.delete(requestBody);
					break;
				case GET:
					logger.trace("HTTP GET");
					rb.get();
					break;
				case HEAD:
					logger.trace("HTTP HEAD");
					rb.head();
					break;
				case PATCH:
					logger.trace("HTTP PATCH");
					rb.patch(requestBody);
					break;
				case PUT:
					logger.trace("HTTP PUT");
					rb.put(requestBody);
					break;
				case POST:
					logger.trace("HTTP POST");
					rb.post(requestBody);
					break;
				default: /* CONNECT, OPTIONS, TRACE */
					logger.warn("HTTP " + method + " (unsupported)");
					throw new UnsupportedOperationException("HTTP request method " + method + " is not yet supported!");
			}

			final Request request = rb.build();
			try (final Response response = client.newCall(request).execute())
			{
				try (final ResponseBody responseBody = response.body())
				{
					logger.debug("Received response of length " + responseBody.contentLength());

					this.responseBody = new StringPayloadDataOutputParameter();
					this.responseBody.insert(responseBody.string(), msg);
				}
				getResponseHeaderHandler().handle(response, msg);
			}
		}
		catch (final Exception e)
		{
			logger.error("Exception occurred during OK HTTP request!", e);
			throw new ProduceException(e);
		}
		return msg;
	}

	/**
	 * Get the request body.
	 * 
	 * @return The request body.
	 */
	public DataInputParameter<String> getRequestBody()
	{
		return requestBody;
	}

	/**
	 * Set where the HTTP Request body is going to come from.
	 * 
	 * @param input
	 *            The input; default is {@link PayloadStreamInputParameter} which is the only implementation currently.
	 */
	public void setRequestBody(final DataInputParameter<String> input)
	{
		requestBody = Args.notNull(input, "data input");
	}

	/**
	 * Get the response body.
	 * 
	 * @return The response body,
	 */
	public DataOutputParameter<String> getResponseBody()
	{
		return responseBody;
	}

	/**
	 * Set where the HTTP Response Body will be written to.
	 * 
	 * @param output
	 *            The output; default is {@link PayloadStreamOutputParameter}.
	 */
	public void setResponseBody(final DataOutputParameter<String> output)
	{
		responseBody = Args.notNull(output, "data output");
	}

//	/**
//	 * Get the HTTP authentication method.
//	 * 
//	 * @return The HTTP authentication method.
//	 */
//	public HttpAuthenticator getAuthenticator()
//	{
//		return authenticator;
//	}
//
//	/**
//	 * Set the authentication method to use for the HTTP request
//	 * 
//	 * @param auth
//	 *            The method of HTTP authentication to use.
//	 */
//	public void setAuthenticator(final HttpAuthenticator auth)
//	{
//		authenticator = auth;
//	}

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public void prepare()
	{
		// empty method
	}
}
