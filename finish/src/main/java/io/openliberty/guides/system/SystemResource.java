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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

// CDI
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

// JAX-RS
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

// Cloudant
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;

@RequestScoped
@Path("properties")
public class SystemResource {

	 @Inject
  SystemConfig systemConfig;

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response getProperties() {
    if (!systemConfig.isInMaintenance()) {
    	
   	 	URL test = null;
		try {
			test = new URL("https://e1cf9f82-898c-401e-8a66-e1d2cbdf0529-bluemix.cloudantnosqldb.appdomain.cloud");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Connect to your Cloudant account
    	CloudantClient clientDB = com.cloudant.client.api.ClientBuilder.url(test)
             .iamApiKey("kD9wH-tLh-EDVjTtOjVniHom7QU5pdd-HvhWZpmXzlPJ")
             .build();
    	
    	// List all the Databases
    	List<String> databases = clientDB.getAllDbs();
    	System.out.println("All my databases : ");
    	for ( String db : databases ) {
    		System.out.println(db);
    	}
    	
    	// Connect to database
    	Database db = clientDB.database("sheffieldtest", false);
    	
    	// Save new document to database
    	db.save(System.getProperties());
    	
    	System.out.println("Data has been added to the DB");
    	
      return Response.ok(System.getProperties()).build();
    } else {
      return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                     .entity("ERROR: Service is currently in maintenance. Contact: "
                         + systemConfig.getEmail().toString())
                     .build();
    }
  }
}
