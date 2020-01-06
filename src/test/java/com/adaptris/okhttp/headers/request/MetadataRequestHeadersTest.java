package com.adaptris.okhttp.headers.request;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import org.junit.Test;
import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.AdaptrisMessageFactory;
import com.adaptris.core.MetadataElement;
import com.adaptris.core.metadata.NoOpMetadataFilter;
import com.adaptris.core.metadata.RemoveAllMetadataFilter;
import okhttp3.Request;

public class MetadataRequestHeadersTest {


  @Test
  public void testSetHeaders() throws Exception {
    MetadataRequestHeaders header = new MetadataRequestHeaders(new RemoveAllMetadataFilter());
    assertEquals(RemoveAllMetadataFilter.class, header.getFilter().getClass());
    header.setFilter(new NoOpMetadataFilter());
    assertEquals(NoOpMetadataFilter.class, header.getFilter().getClass());
  }

  @Test
  public void testAddHeaders() throws Exception {
    MetadataRequestHeaders header = new MetadataRequestHeaders(new NoOpMetadataFilter());
    Request.Builder builder = new Request.Builder();
    AdaptrisMessage msg = AdaptrisMessageFactory.getDefaultInstance().newMessage();
    msg.addMetadata(new MetadataElement("a", "b"));
    assertSame(builder, header.addHeaders(msg, builder));
  }

  @Test
  public void testAddHeaders_Unfold() throws Exception {
    MetadataRequestHeaders header = new MetadataRequestHeaders(new NoOpMetadataFilter());
    header.setUnfold(true);
    Request.Builder builder = new Request.Builder();
    AdaptrisMessage msg = AdaptrisMessageFactory.getDefaultInstance().newMessage();
    msg.addMetadata(new MetadataElement("a", "b"));
    assertSame(builder, header.addHeaders(msg, builder));
  }

}
