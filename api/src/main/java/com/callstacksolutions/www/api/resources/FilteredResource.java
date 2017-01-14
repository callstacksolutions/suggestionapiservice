package com.callstacksolutions.www.api.resources;


import com.callstacksolutions.www.api.filters.DateRequired;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/filtered")
public class FilteredResource {

    @GET
    @DateRequired
    @Path("filtered")
    public String sayHello() {
        return "hello";
    }
}
