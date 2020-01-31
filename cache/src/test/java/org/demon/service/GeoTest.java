package org.demon.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author demon
 * @version 1.0.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class GeoTest {

    @Autowired
    private GeoService geoService;

    @Test
    public void test() {
//        geoService.add(new Point(120.6545164800, 27.6733984100), "song4");
//        geoService.add(new Point(120.4545964800, 27.6733974100), "song5");
//        geoService.add(new Point(120.3545864800, 27.6733994100), "song6");

        GeoResults<RedisGeoCommands.GeoLocation> list = geoService.near(new Point(120.3545864800,
                27.6733994100), 100000);
        for (GeoResult<RedisGeoCommands.GeoLocation> result : list) {
            RedisGeoCommands.GeoLocation content = result.getContent();
            log.info(String.valueOf(result.getDistance()));
        }


    }


}
