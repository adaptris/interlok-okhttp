package com.adaptris.okhttp.headers.request;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adaptris.annotation.AdvancedConfig;
import com.adaptris.annotation.InputFieldDefault;
import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.MetadataCollection;
import com.adaptris.core.MetadataElement;
import com.adaptris.core.http.client.RequestHeaderProvider;
import com.adaptris.core.metadata.MetadataFilter;
import com.adaptris.core.util.Args;
import com.thoughtworks.xstream.annotations.XStreamAlias;

import okhttp3.Request;

/**
 * Implementation of {@link RequestHeaderProvider} that applies {@link com.adaptris.core.AdaptrisMessage} metadata as
 * headers to a OK HTTP request.
 * 
 * @config okhttp-metadata-request-headers
 */
@XStreamAlias("okhttp-metadata-request-headers")
public class Metadata implements RequestHeaderProvider<Request.Builder>
{
	private static final transient Logger log = LoggerFactory.getLogger(Metadata.class);

	@NotNull
	@Valid
	private MetadataFilter filter;

	@AdvancedConfig
	@InputFieldDefault(value = "false")
	private Boolean unfold;

	public Metadata()
	{
		super();
	}

	public Metadata(final MetadataFilter mf)
	{
		this();
		setFilter(mf);
	}

	@Override
	public Request.Builder addHeaders(final AdaptrisMessage msg, final Request.Builder target)
	{
		final MetadataCollection metadataSubset = getFilter().filter(msg);
		for (final MetadataElement me : metadataSubset)
		{
			final String value = unfold(me.getValue());
			log.trace("Adding Request Property [{}: {}]", me.getKey(), value);
			target.addHeader(me.getKey(), value);
		}
		return target;
	}

	public MetadataFilter getFilter()
	{
		return filter;
	}

	/**
	 * Set the filter to be applied to metadata before adding as request properties.
	 * 
	 * @param mf
	 *            the filter.
	 */
	public void setFilter(final MetadataFilter mf)
	{
		this.filter = Args.notNull(mf, "metadata filter");
	}

	public Boolean getUnfold()
	{
		return unfold;
	}

	/**
	 * Unfold headers onto a single line.
	 * <p>
	 * RFC7230 deprecates the folding of headers onto multiple lines; so HTTP headers are expected to be single line. This param
	 * allows you to enforce that unfolding metadata values happens before writing them as request properties.
	 * </p>
	 * 
	 * @param b
	 *            true to unfold values (default is false to preserve legacy behaviour).
	 */
	public void setUnfold(final Boolean b)
	{
		this.unfold = b == null ? false : b;
	}

	private String unfold(final String s)
	{
		if (unfold)
		{
			return s.replaceAll("\\s\\r\\n\\s+", " ").replaceAll("\\r\\n\\s+", " ");
		}
		return s;
	}
}
