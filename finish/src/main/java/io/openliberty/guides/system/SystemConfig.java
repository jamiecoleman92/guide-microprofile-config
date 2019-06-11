/*******************************************************************************
 * Copyright (c) 2017, 2018, 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial implementation
 *******************************************************************************/
package io.openliberty.guides.system;

import java.net.URL;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import javax.inject.Provider;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import io.openliberty.guides.config.Email;

@RequestScoped
public class SystemConfig {

  @Inject
  @ConfigProperty(name = "io_openliberty_guides_system_inMaintenance")
  Provider<Boolean> inMaintenance;

  @Inject
  @ConfigProperty(name = "io_openliberty_guides_email")
  private Provider<Email> email;
  
  @Inject
  @ConfigProperty(name = "io_openliberty_guides_system_dbUrl")
  Provider<URL> url;
  
  @Inject
  @ConfigProperty(name = "io_openliberty_guides_system_apiKey")
  Provider<String> apiKey;
  
  @Inject
  @ConfigProperty(name = "io_openliberty_guides_system_dbName")
  Provider<String> dbName;

  public boolean isInMaintenance() {
    return inMaintenance.get();
  }

  public Email getEmail() {
    return email.get();
  }

}
