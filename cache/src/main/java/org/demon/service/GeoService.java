package org.demon.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @author demon
 * @version 1.0.0
 */
@Service
public class GeoService {

    private static final String GEO_KEY = "user_geo";

    @Autowired
    private RedisTemplate redisTemplate;


    public void add(Point point, String username) {
        redisTemplate.opsForGeo().add(GEO_KEY, point, username);
    }

    public GeoResults<RedisGeoCommands.GeoLocation> near(Point point, double distance) {
        Distance distance1 = new Distance(distance, RedisGeoCommands.DistanceUnit.METERS);
        Circle circle = new Circle(point, distance1);
        RedisGeoCommands.GeoRadiusCommandArgs geoRadiusCommandArgs =
                RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs().includeDistance().includeCoordinates();
        return redisTemplate.opsForGeo().radius(GEO_KEY, circle, geoRadiusCommandArgs);
    }
}
