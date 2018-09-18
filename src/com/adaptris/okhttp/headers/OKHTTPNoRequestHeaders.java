package com.adaptris.okhttp.headers;

import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.http.client.RequestHeaderProvider;
import com.thoughtworks.xstream.annotations.XStreamAlias;

import okhttp3.Request;

/**
 * Implementation of {@link RequestHeaderProvider} that adds no additional headers
 */
@XStreamAlias("okhttp-no-request-headers")
public class OKHTTPNoRequestHeaders implements RequestHeaderProvider<Request.Builder>
{
	@Override
	public Request.Builder addHeaders(AdaptrisMessage msg, Request.Builder target)
	{
		return target;
	}
}
