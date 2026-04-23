/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.resource;

import com.smartcampus.db.DataStore;
import com.smartcampus.model.Sensor;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    @GET
    public List<Sensor> getSensors(@QueryParam("type") String type) {
        List<Sensor> result = new ArrayList<>(DataStore.sensors.values());
        if (type != null && !type.trim().isEmpty()) {
            result.removeIf(s -> !s.getType().equalsIgnoreCase(type));
        }
        return result;
    }

    @POST
    public Response createSensor(Sensor sensor) {
        if (!DataStore.rooms.containsKey(sensor.getRoomId())) {
             return Response.status(422)
                     .entity("{\"error\":\"Linked Room does not exist.\"}")
                     .build();
        }
        DataStore.sensors.put(sensor.getId(), sensor);
        DataStore.rooms.get(sensor.getRoomId()).getSensorIds().add(sensor.getId());
        return Response.status(Response.Status.CREATED).entity(sensor).build();
    }

    @Path("/{sensorId}/readings")
    public SensorReadingResource getReadingsResource(@PathParam("sensorId") String sensorId) {
        return new SensorReadingResource(sensorId);
    }
}
