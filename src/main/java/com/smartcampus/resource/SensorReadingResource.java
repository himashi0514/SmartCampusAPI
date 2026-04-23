/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.resource;

import com.smartcampus.db.DataStore;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    private String sensorId;

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    @GET
    public List<SensorReading> getReadings() {
        return new ArrayList<>(DataStore.readings.values()); 
    }

    @POST
    public Response addReading(SensorReading reading) {
        Sensor sensor = DataStore.sensors.get(sensorId);
        if (sensor == null) return Response.status(Response.Status.NOT_FOUND).build();
        
        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("{\"error\":\"Sensor is in maintenance mode.\"}")
                    .build();
        }

        reading.setId(UUID.randomUUID().toString());
        reading.setTimestamp(System.currentTimeMillis());
        DataStore.readings.put(reading.getId(), reading);
        
        sensor.setCurrentValue(reading.getValue()); 
        
        return Response.status(Response.Status.CREATED).entity(reading).build();
    }
}
