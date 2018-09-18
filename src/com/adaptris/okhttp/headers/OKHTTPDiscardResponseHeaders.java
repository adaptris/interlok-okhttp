package com.adaptris.okhttp.headers;

import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.http.client.ResponseHeaderHandler;
import com.thoughtworks.xstream.annotations.XStreamAlias;

import okhttp3.Response;

/**
 * {@link ResponseHeaderHandler} implementation that discards the headers from the HTTP response.
 */
@XStreamAlias("okhttp-discard-response-headers")
public class OKHTTPDiscardResponseHeaders implements ResponseHeaderHandler<Response>
{
	@Override
	public AdaptrisMessage handle(Response response, AdaptrisMessage msg)
	{
		return msg;
	}
}
