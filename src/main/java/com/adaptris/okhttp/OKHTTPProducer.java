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
import com.adaptris.core.ProduceException;
import com.adaptris.core.common.PayloadStreamInputParameter;
import com.adaptris.core.common.PayloadStreamOutputParameter;
import com.adaptris.core.common.StringPayloadDataInputParameter;
import com.adaptris.core.common.StringPayloadDataOutputParameter;
import com.adaptris.core.http.client.RequestMethodProvider.RequestMethod;
import com.adaptris.core.http.client.net.HttpProducer;
import com.adaptris.core.util.Args;
import com.adaptris.interlok.config.DataInputParameter;
import com.adaptris.interlok.config.DataOutputParameter;
import com.adaptris.okhttp.headers.request.NoRequestHeaders;
import com.adaptris.okhttp.headers.response.DiscardResponseHeaders;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * A HTTP producer that uses the OKHTTP; in time it will (hopefully) make use of some of the more advanced features of OKHTTP.
 */
@XStreamAlias("okhttp-http-producer")
@AdapterComponent
@ComponentProfile(summary = "Make a HTTP request to a remote server using the OKHTTP library", tag = "producer,http,https",
    metadata = {"adphttpresponse"

    }, recommended = {NullConnection.class})
@DisplayOrder(order = {"url", "allowRedirect", "ignoreServerResponseCode", "alwaysSendPayload",
    "methodProvider", "contentTypeProvider", "requestHeaderProvider", "requestBody",
    "responseHeaderHandler", "responseBody"})
public class OKHTTPProducer extends HttpProducer<Request.Builder, Response> {
  private static final transient Logger logger = LoggerFactory.getLogger(OKHTTPProducer.class);

  private transient OkHttpClient client = new OkHttpClient();

  @Valid
  @AdvancedConfig
  private DataInputParameter<String> requestBody;

  @Valid
  @AdvancedConfig
  private DataOutputParameter<String> responseBody;

  public OKHTTPProducer() {
    super();
    setRequestHeaderProvider(new NoRequestHeaders()); // no additional request headers
    setResponseHeaderHandler(new DiscardResponseHeaders()); // discard response headers
    setRequestBody(new StringPayloadDataInputParameter());
    setResponseBody(new StringPayloadDataOutputParameter());
  }

  @Override
  protected void doProduce(AdaptrisMessage msg, String endpointUrl) throws ProduceException {
    doRequest(msg, endpointUrl, defaultTimeout());
  }

  @Override
  @SuppressWarnings("hiding")
  protected AdaptrisMessage doRequest(final AdaptrisMessage msg, final String endpointUrl,
      final long timeout)
      throws ProduceException {
    logger.info("OKHTTP producer request");
    try {
      final URL url = new URL(endpointUrl);

      final Request.Builder rb = new Request.Builder().url(url);
      getRequestHeaderProvider().addHeaders(msg, rb);

      final MediaType mediaType = MediaType.parse(getContentTypeProvider().getContentType(msg));
      final RequestBody requestBody = RequestBody.create(mediaType, getRequestBody().extract(msg));

      final RequestMethod method = getMethod(msg);
      logger.info("HTTP " + method.name() + " " + url);
      if (method == RequestMethod.GET) {
        rb.get();
      } else {
        rb.method(method.name(), requestBody);
      }

      final Request request = rb.build();
      try (final Response response = client.newCall(request).execute()) {
        try (final ResponseBody responseBody = response.body()) {
          logger.debug("Received response of length " + responseBody.contentLength());
          getResponseBody().insert(responseBody.string(), msg);
        }
        getResponseHeaderHandler().handle(response, msg);
      }
    } catch (final Exception e) {
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
  public DataInputParameter<String> getRequestBody() {
    return requestBody;
  }

  /**
   * Set where the HTTP Request body is going to come from.
   *
   * @param input The input; default is {@link PayloadStreamInputParameter} which is the only implementation currently.
   */
  public void setRequestBody(final DataInputParameter<String> input) {
    requestBody = Args.notNull(input, "data input");
  }

  /**
   * Get the response body.
   *
   * @return The response body,
   */
  public DataOutputParameter<String> getResponseBody() {
    return responseBody;
  }

  /**
   * Set where the HTTP Response Body will be written to.
   *
   * @param output The output; default is {@link PayloadStreamOutputParameter}.
   */
  public void setResponseBody(final DataOutputParameter<String> output) {
    responseBody = Args.notNull(output, "data output");
  }
}
