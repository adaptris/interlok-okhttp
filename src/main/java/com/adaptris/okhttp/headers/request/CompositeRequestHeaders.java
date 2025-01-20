package com.adaptris.okhttp.headers.request;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jakarta.validation.constraints.NotNull;

import com.adaptris.annotation.AutoPopulated;
import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.http.client.RequestHeaderProvider;
import com.adaptris.core.util.Args;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import okhttp3.Request;

/**
 * Implementation of {@link RequestHeaderProvider} that uses nested providers to add headers.
 *
 * This implementation is primarily so that you can mix and match both static and metadata driven headers; the order in which you configure
 * them determines what is actually present as headers.
 *
 * @config okhttp-composite-request-headers
 */
@XStreamAlias("okhttp-composite-request-headers")
public class CompositeRequestHeaders implements RequestHeaderProvider<Request.Builder> {
  @XStreamImplicit
  @NotNull
  @AutoPopulated
  private List<RequestHeaderProvider<Request.Builder>> providers;

  public CompositeRequestHeaders() {
    providers = new ArrayList<>();
  }

  public CompositeRequestHeaders(@SuppressWarnings("unchecked") final RequestHeaderProvider<Request.Builder>... headers) {
    this();
    setProviders(new ArrayList<>(Arrays.asList(headers)));
  }

  @Override
  public Request.Builder addHeaders(final AdaptrisMessage msg, final Request.Builder target) {
    for (final RequestHeaderProvider<Request.Builder> h : getProviders()) {
      h.addHeaders(msg, target);
    }
    return target;
  }

  public List<RequestHeaderProvider<Request.Builder>> getProviders() {
    return providers;
  }

  public void setProviders(final List<RequestHeaderProvider<Request.Builder>> handlers) {
    providers = Args.notNull(handlers, "Request Header Providers");
  }

  public void addProvider(final RequestHeaderProvider<Request.Builder> handler) {
    getProviders().add(Args.notNull(handler, "Request Header Provider"));
  }

}
