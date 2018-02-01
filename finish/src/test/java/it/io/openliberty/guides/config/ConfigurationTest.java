// tag::comment[]
/*******************************************************************************
 * Copyright (c) 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial implementation
 *******************************************************************************/
// end::comment[]
// tag::test[]
package it.io.openliberty.guides.config;

import static org.junit.Assert.*;

import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.apache.cxf.jaxrs.provider.jsrjsonp.JsrJsonpProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ConfigurationTest {

  private String port;
  private String baseUrl;
  private Client client;

  private final String INVENTORY_HOSTS = "inventory/hosts";
  private final String CONFIG_MANAGER = "config";
  private final String USER_DIR = System.getProperty("user.dir");
  private final String DEFAULT_CONFIG_FILE = USER_DIR
      + "/src/main/resources/META-INF/microprofile-config.properties";
  private final String CUSTOM_CONFIG_FILE = USER_DIR.split("src")[0]
      + "/resource/CustomConfigSource.json";
  private final String INV_MAINTENANCE_PROP = "io_openliberty_guides_inventory_inMaintenance";
  private final String TEST_OVERWRITE_PROP = "io_openliberty_guides_testConfigOverwrite";

  @Before
  public void setup() {
    port = System.getProperty("liberty.test.port");
    baseUrl = "http://localhost:" + port + "/";
    ConfigTestUtil.setDefaultJsonFile(CUSTOM_CONFIG_FILE);

    client = ClientBuilder.newClient();
    client.register(JsrJsonpProvider.class);
  }

  @After
  public void teardown() {
    ConfigTestUtil.setDefaultJsonFile(CUSTOM_CONFIG_FILE);
    client.close();
  }

  // tag::testSuite[]
  @Test
  public void testSuite() {
    this.testInitialServiceStatus();
    this.testOverrideConfigProperty();
    this.testPutServiceInMaintenance();
  }
  // end::testSuite[]

  // tag::testInitialServiceStatus()[]
  public void testInitialServiceStatus() {
    JsonObject obj = ConfigTestUtil.getJsonObjectFromURL(
                                       client,baseUrl + INVENTORY_HOSTS, 1, null);
    boolean status = Boolean.valueOf(ConfigTestUtil.readPropertyValueInFile(
                                       INV_MAINTENANCE_PROP, DEFAULT_CONFIG_FILE));
    if (!status) {
      assertEquals("The Inventory Service should be available", 0, obj.getInt("total"));
    } else {
      assertEquals("The Inventory Service should be in maintenance",
                   "Service is temporarily down for maintenance",
                   obj.getString("InventoryResource"));
    }
  }
  // end::testInitialServiceStatus()[]

  // tag::testOverrideConfigProperty()[]
  public void testOverrideConfigProperty() {
    JsonObject properties = ConfigTestUtil.getJsonObjectFromURL(client, baseUrl
        + CONFIG_MANAGER, 2, "ConfigProperties");
    assertEquals(TEST_OVERWRITE_PROP + " should be DefaultSource in the beginning",
                 "DefaultSource", properties.getString(TEST_OVERWRITE_PROP));
    ConfigTestUtil.changeConfigSourcePriority(CUSTOM_CONFIG_FILE, 700);

    JsonObject newProperties = ConfigTestUtil.getJsonObjectFromURL(client, baseUrl
        + CONFIG_MANAGER, 2, "ConfigProperties");
    assertEquals(TEST_OVERWRITE_PROP + " should be CustomSource in the end",
                 "CustomSource", newProperties.getString(TEST_OVERWRITE_PROP));
  }
  // end::testOverrideConfigProperty()[]

  // tag::testPutServiceInMaintenance()[]
  public void testPutServiceInMaintenance() {
    JsonObject obj = ConfigTestUtil.getJsonObjectFromURL(
                                           client,baseUrl + INVENTORY_HOSTS, 1, null);
    assertEquals("The inventory service should be up in the beginning", 0,
                 obj.getInt("total"));

    ConfigTestUtil.changeConfigSourcePriority(CUSTOM_CONFIG_FILE, 700);
    ConfigTestUtil.switchInventoryMaintenance(CUSTOM_CONFIG_FILE, true);

    JsonObject newObj = ConfigTestUtil.getJsonObjectFromURL(
                                         client, baseUrl + INVENTORY_HOSTS, 2, "Status");
    assertEquals("The inventory service should be down in the end",
                 "Service is temporarily down for maintenance",
                 newObj.getString("InventoryResource"));
  }
  // end::testPutServiceInMaintenance()[]

}
// end::test[]