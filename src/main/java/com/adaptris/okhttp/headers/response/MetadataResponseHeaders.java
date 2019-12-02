package com.adaptris.okhttp.headers.response;

import static org.apache.commons.lang.StringUtils.defaultIfEmpty;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.adaptris.annotation.AdvancedConfig;
import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.http.client.ResponseHeaderHandler;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import okhttp3.Response;

/**
 * @config okhttp-response-headers-as-metadata
 */
@XStreamAlias("okhttp-response-headers-as-metadata")
public class MetadataResponseHeaders implements ResponseHeaderHandler<Response> {

  private static final transient Logger log = LoggerFactory.getLogger(MetadataResponseHeaders.class);

  private static final String DEFAULT_SEPARATOR_CHAR = "\t";

  private String metadataPrefix;

  @AdvancedConfig
  private String metadataSeparator = DEFAULT_SEPARATOR_CHAR;

  public MetadataResponseHeaders() {}

  public MetadataResponseHeaders(final String prefix, final String separator) {
    this();
    setMetadataPrefix(prefix);
    setMetadataSeparator(separator);
  }

  public MetadataResponseHeaders(final String prefix) {
    this(prefix, DEFAULT_SEPARATOR_CHAR);
  }

  @Override
  public AdaptrisMessage handle(final Response response, final AdaptrisMessage msg) {
    addMetadata(response.headers().toMultimap(), msg);
    return msg;
  }

  private void addMetadata(final Map<String, List<String>> headers, final AdaptrisMessage msg) {
    final String separator = metadataSeparator();
    for (final String key : headers.keySet()) {
      final String value = StringUtils.join(headers.get(key), separator);
      logAdd(msg, defaultIfEmpty(getMetadataPrefix(), "") + key, value);
    }
  }

  private static void logAdd(final AdaptrisMessage msg, final String key, final String value) {
    log.trace("Adding Metadata [{}:{}]", key, value);
    msg.addMetadata(key, value);
  }

  /**
   * Get the metadata prefix.
   * 
   * @return The metadata prefix.
   */
  public String getMetadataPrefix() {
    return metadataPrefix;
  }

  /**
   * Set the metadata prefix.
   * 
   * @param metadataPrefix The metadata prefix.
   */
  public void setMetadataPrefix(final String metadataPrefix) {
    this.metadataPrefix = metadataPrefix;
  }

  /**
   * Get the metadata separator.
   * 
   * @return The metadata separator.
   */
  public String getMetadataSeparator() {
    return metadataSeparator;
  }

  /**
   * Set the separator to be used when multiple headers should be associated with the same key.
   * 
   * @param s the separator (default if not specified is "\t");
   */
  public void setMetadataSeparator(final String s) {
    this.metadataSeparator = s;
  }

  private String metadataSeparator() {
    return ObjectUtils.defaultIfNull(getMetadataSeparator(), DEFAULT_SEPARATOR_CHAR);
  }
}
