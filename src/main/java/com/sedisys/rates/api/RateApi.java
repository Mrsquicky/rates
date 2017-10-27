package com.sedisys.rates.api;

import io.swagger.annotations.*;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Api("Rates")
@Path("/rate")
@Consumes({"application/json", "application/xml"})
@Produces({"application/json", "application/xml"})
public interface RateApi {

	@GET
	@Consumes({"application/json", "application/xml"})
	@Produces({"application/json", "application/xml"})
	@ApiOperation(value = "Get the rate for a given datetime range", notes = "", nickname = "getRate", response = String.class, tags = {})
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Status 200", response = String.class),
			@ApiResponse(code = 400, message = "Request datetimes were malformed", response = String.class)})
	Response getRate(
			@ApiParam(value = "The ISO formatted start of the date time range", required = true, type = "string", format = "date-time")
			@QueryParam("start") String start,
			@ApiParam(value = "The ISO formatted end of the date time range", required = true, type = "string", format = "date-time")
			@QueryParam("end") String end);
}
