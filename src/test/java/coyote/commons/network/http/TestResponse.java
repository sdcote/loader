package coyote.commons.network.http;

public class TestResponse {
  String location = null;
  int status = 0;
  String data = null;




  public TestResponse( final String url ) {
    location = url;
  }




  public String getData() {
    return data;
  }




  public String getLocation() {
    return location;
  }




  public int getStatus() {
    return status;
  }




  public void setData( final String data ) {
    this.data = data;
  }




  public void setLocation( final String url ) {
    location = url;
  }




  public void setStatus( final int code ) {
    status = code;
  }

}
