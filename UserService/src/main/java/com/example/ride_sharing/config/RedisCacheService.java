package com.example.ride_sharing.config;
import com.example.ride_sharing.model.RideRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisCacheService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // Cache a ride in Redis
    public void cacheRide(String rideId, RideRequest ride) {
        redisTemplate.opsForValue().set(rideId, ride);
    }

    // Retrieve a cached ride from Redis
    public RideRequest getCachedRide(String rideId) {
        return (RideRequest) redisTemplate.opsForValue().get(rideId);
    }
}

