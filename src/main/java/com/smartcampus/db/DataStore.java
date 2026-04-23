/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.db;

import com.smartcampus.model.*;
import java.util.concurrent.ConcurrentHashMap;

public class DataStore {
    public static ConcurrentHashMap<String, Room> rooms = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, Sensor> sensors = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, SensorReading> readings = new ConcurrentHashMap<>();
}
