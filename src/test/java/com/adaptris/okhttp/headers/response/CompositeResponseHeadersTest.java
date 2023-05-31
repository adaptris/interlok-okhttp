package com.adaptris.okhttp.headers.response;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

public class CompositeResponseHeadersTest {

  @Test
  public void testConstructor() {
    CompositeResponseHeaders response = new CompositeResponseHeaders(new DiscardResponseHeaders());
    assertEquals(1, response.getHandlers().size());
    assertEquals(DiscardResponseHeaders.class, response.getHandlers().get(0).getClass());
  }

  @Test
  public void testAddHandler() throws Exception {
    CompositeResponseHeaders response = new CompositeResponseHeaders();
    response.setHandlers(new ArrayList<>(Arrays.asList(new DiscardResponseHeaders())));
    assertEquals(1, response.getHandlers().size());
    assertEquals(DiscardResponseHeaders.class, response.getHandlers().get(0).getClass());
  }

}
