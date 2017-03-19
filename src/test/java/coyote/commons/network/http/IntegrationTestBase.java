package coyote.commons.network.http;

import java.io.IOException;

import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.After;
import org.junit.Before;


public abstract class IntegrationTestBase<T extends HTTPD> {

  protected DefaultHttpClient httpclient;

  protected T testServer;




  public abstract T createTestServer();




  @Before
  public void setUp() {
    this.testServer = createTestServer();
    this.httpclient = new DefaultHttpClient();
    try {
      this.testServer.start();
    } catch ( final IOException e ) {
      e.printStackTrace();
    }
  }




  @After
  public void tearDown() {
    this.httpclient.getConnectionManager().shutdown();
    this.testServer.stop();
  }
}
