package com.adaptris.okhttp.headers.request;

import jakarta.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adaptris.annotation.AutoPopulated;
import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.http.client.RequestHeaderProvider;
import com.adaptris.core.util.Args;
import com.adaptris.util.KeyValuePair;
import com.adaptris.util.KeyValuePairSet;
import com.thoughtworks.xstream.annotations.XStreamAlias;

import okhttp3.Request;

/**
 * Implementation of {@link RequestHeaderProvider} that applies configured values as headers.
 */
@XStreamAlias("okhttp-configured-request-headers")
public class ConfiguredRequestHeaders implements RequestHeaderProvider<Request.Builder> {
  private static final transient Logger log = LoggerFactory.getLogger(ConfiguredRequestHeaders.class);

  @NotNull
  @AutoPopulated
  private KeyValuePairSet headers;

  public ConfiguredRequestHeaders() {
    headers = new KeyValuePairSet();
  }

  @Override
  public Request.Builder addHeaders(final AdaptrisMessage msg, final Request.Builder target) {
    for (final KeyValuePair k : getHeaders()) {
      log.trace("Adding Request Property [{}: {}]", k.getKey(), k.getValue());
      target.addHeader(k.getKey(), k.getValue());
    }
    return target;
  }

  public KeyValuePairSet getHeaders() {
    return headers;
  }

  public void setHeaders(final KeyValuePairSet headers) {
    this.headers = Args.notNull(headers, "headers");
  }

  public ConfiguredRequestHeaders withHeaders(final KeyValuePair... keyValuePairs) {
    for (final KeyValuePair pair : keyValuePairs) {
      headers.add(pair);
    }
    return this;
  }

}
