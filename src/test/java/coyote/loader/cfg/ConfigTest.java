package coyote.loader.cfg;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

//import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Test;

import coyote.commons.GUID;
import coyote.commons.StringUtil;
import coyote.commons.UriUtil;
import coyote.dataframe.DataFrame;
import coyote.dataframe.marshal.JSONMarshaler;


public class ConfigTest {

  @Test
  public void test() {
    Config config = new Config();
    config.setName("Loader");
    config.setClassName(coyote.loader.DefaultLoader.class.getName());
    config.setId(GUID.randomGUID().toString());
    config.setName("Bob");

    // The configuration section of all the components this loader is to load
    Config componentCfg = new Config();

    // Create a configuration for component 1
    Config cfg = new Config();
    cfg.setName("Component1");
    cfg.setClassName(coyote.loader.SimpleComponent.class.getName());
    cfg.setId("F56A");
    componentCfg.add(cfg);

    cfg = new Config();
    cfg.setName("Component2");
    cfg.setClassName(coyote.loader.SimpleComponent.class.getName());
    cfg.setId("DB46");
    componentCfg.add(cfg);

    cfg = new Config();
    cfg.setName("Component3");
    cfg.setClassName(coyote.loader.SimpleComponent.class.getName());
    cfg.setId("8A0A");
    componentCfg.add(cfg);

    // Add the component configuration to the main Loader config
    config.add("Component", componentCfg);

    // This is a standard configuration for a loader
    //System.out.println(JSONMarshaler.toFormattedString(config));

  }




  @Test
  public void testGetInt() {

    Config cfg = new Config();
    cfg.set("port", "123");

    try {
      int value = cfg.getInt("PORT");
      assertEquals(123, value);
    } catch (NumberFormatException e) {
      fail("Could not retrieve as an integer");
    }

    cfg.set("fail", null);
    try {
      int value = cfg.getInt("FAIL");
      fail("Should have thrown an exception");

      cfg.getInt("NotThere");
      fail("Should have thrown an exception - null value");
    } catch (NumberFormatException ignore) {
      // ignore the exception
    }

    try {
      int value = cfg.getInt("NotThere");
      fail("Should have thrown an exception - not found");
    } catch (NumberFormatException ignore) {
      // ignore the exception
    }

  }




  @Test
  public void readFromNetwork() throws URISyntaxException, IOException, ConfigurationException {
    URI cfgUri = new URI("http://github.com/sdcote/loader/blob/master/src/test/resources/ou812");
    Config config = Config.read(cfgUri);
    assertNotNull(config);
    if (StringUtil.isBlank(config.getName())) {
      String basename = UriUtil.getBase(cfgUri);
      assertNotNull(basename);
      config.setName(basename);
      assertNotNull(config.getName());
      assertEquals(basename, config.getName());
    }
    //System.out.println( JSONMarshaler.toFormattedString( config ) );
  }




  @Test
  public void testGetString() {

    Config cfg = new Config();
    cfg.set("port", "123");

    // case insensitive by default
    assertNotNull(cfg.getString("Port"));

    // case sensitive (ignoreCase=false)
    assertNull(cfg.getString("Port", false));

    // case insensitive (ignoreCase=true)
    assertNotNull(cfg.getString("Port", true));
  }




  @Test
  public void copyTest() {
    Config cfg = new Config();
    cfg.set("port", "123");
    assertNotNull(cfg.getString("port"));
    assertEquals(cfg.getString("port"), "123");

    Config copy = cfg.copy();
    assertNotNull(copy.getString("port"));
    assertEquals("123", copy.getString("port"));
    copy.put("port", "456");

    assertEquals("456", copy.getString("port"));
    assertEquals("123", cfg.getString("port"));

  }




  @Test
  public void readArrayBug() {
    String[] models = {"PT3500", "PT4000", "PT4500"};
    DataFrame config = new DataFrame().set("array", models);
    String cfgData = config.toString();
    //System.out.println(cfgData);
    List<DataFrame> cfglist = JSONMarshaler.marshal(cfgData);
    //DataFrame frame = cfglist.get(0);
    Config configuration = new Config(cfglist.get(0));
    Object obj = configuration.getObject("array");
    assertNotNull(obj);
    assertTrue(obj instanceof String[]); // Not DataFrame
  }



  @Test
  public void sanitize() throws URISyntaxException, IOException, ConfigurationException {
    File file = new File("src\\test\\resources\\webauth.json");
    System.out.println(file.getAbsolutePath());
    Config config = Config.read(file);
    assertNotNull(config);
    ConfigSanitizer.addProtectedFieldName("default");
    Config cleanConfig = ConfigSanitizer.sanitize(config);
    assertNotNull(cleanConfig);

    // Test the set protected field name
    Config aclSection = cleanConfig.getSection("IPACL");
    assertNotNull(aclSection);
    String value = aclSection.getString("default");
    assertEquals(ConfigSanitizer.PROTECTED,value);

    // Test the password in an embedded section
    Config authSection = cleanConfig.getSection("Auth");
    Config usersSection = authSection.getSection("Users");
    for( Config section: usersSection.getSections()){
      value = section.getString("ENC:Password");
      assertEquals(ConfigSanitizer.PROTECTED,value);
    }
    //System.out.println( JSONMarshaler.toFormattedString( cleanConfig ) );
  }

}
