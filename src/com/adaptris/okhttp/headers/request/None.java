package com.adaptris.okhttp.headers.request;

import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.http.client.RequestHeaderProvider;
import com.thoughtworks.xstream.annotations.XStreamAlias;

import okhttp3.Request;

/**
 * Implementation of {@link RequestHeaderProvider} that adds no additional headers
 */
@XStreamAlias("okhttp-no-request-headers")
public class None implements RequestHeaderProvider<Request.Builder>
{
	@Override
	public Request.Builder addHeaders(final AdaptrisMessage msg, final Request.Builder target)
	{
		return target;
	}
}
