package com.adaptris.okhttp.headers.response;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotNull;

import com.adaptris.annotation.AutoPopulated;
import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.http.client.ResponseHeaderHandler;
import com.adaptris.core.http.client.net.ResponseHeadersAsMetadata;
import com.adaptris.core.http.client.net.ResponseHeadersAsObjectMetadata;
import com.adaptris.core.util.Args;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import okhttp3.Response;

/**
 * Implementation of {@link ResponseHeaderHandler} that uses nested handlers to extract headers.
 * 
 * This implementation is primarily so that you can mix and matchhow you capture response headers; If you wanted to use both
 * {@link ResponseHeadersAsMetadata} and {@link ResponseHeadersAsObjectMetadata} then you can.
 *
 * @config http-composite-request-headers
 */
@XStreamAlias("okhttp-composite-response-header-handler")
public class CompositeResponseHeaders implements ResponseHeaderHandler<Response>
{
	@XStreamImplicit
	@NotNull
	@AutoPopulated
	private List<ResponseHeaderHandler<Response>> handlers;

	public CompositeResponseHeaders()
	{
		setHandlers(new ArrayList<>());
	}

	public CompositeResponseHeaders(@SuppressWarnings("unchecked") ResponseHeaderHandler<Response>... handlers)
	{
		this();
		for (final ResponseHeaderHandler<Response> h : handlers)
		{
			addHandler(h);
		}
	}

	public List<ResponseHeaderHandler<Response>> getHandlers()
	{
		return handlers;
	}

	public void setHandlers(final List<ResponseHeaderHandler<Response>> handlers)
	{
		this.handlers = Args.notNull(handlers, "Response Header Handlers");
	}

	public void addHandler(ResponseHeaderHandler<Response> handler)
	{
		getHandlers().add(Args.notNull(handler, "Response Handler"));
	}

	@Override
	public AdaptrisMessage handle(final Response repsonse, final AdaptrisMessage msg)
	{
		for (final ResponseHeaderHandler<Response> h : getHandlers())
		{
			h.handle(repsonse, msg);
		}
		return msg;
	}
}
