package com.web.distributed.cache.tk_cache.redis;

import com.web.tk.common.tk_common.exception.CommonException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;
import redis.clients.jedis.commands.JedisCommands;
import redis.clients.jedis.params.GeoRadiusParam;
import redis.clients.jedis.params.SetParams;
import redis.clients.jedis.params.ZAddParams;
import redis.clients.jedis.params.ZIncrByParams;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 封装JedisCommands指令类
 * <br/>=================================
 * <br/>公司：xxx公司
 * <br/>开发：lizhenghg<xxxx@lizhenghg.com>
 * <br/>版本：1.0
 * <br/>创建时间：2019-4-20
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class RedisCommands implements JedisCommands {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisCommands.class);

    private IRedisPool redisPool;

    private RedisCommands() {
    }

    public RedisCommands(IRedisPool redisPool) {
        if (redisPool == null) {
            throw new NullPointerException("RedisCommands, pool is null");
        }
        this.redisPool = redisPool;
    }

    //这里写分布式锁

    @Override
    public String set(String key, String value) {
        Jedis jedis = null;
        String result = null;
        try {
            jedis = redisPool.getResource();
            result = jedis.set(key, value);
        } catch (Exception ex) {
            LOGGER.error("RedisCommands, fail to set, key: {}, value: {}", key, value, ex);
            throw new CommonException(String.format("RedisCommands, fail to set, key: {}, value: {}", key, value), ex);
        } finally {
            jedis.close();
        }
        return result;
    }

    @Override
    public Long hset(String key, String field, String value) {
        Jedis jedis = null;
        Long lng = 0L;
        try {
            jedis = redisPool.getResource();
            lng = jedis.hset(key, field, value);
        } catch (Exception e) {
            LOGGER.error("RedisCommands, fail to hset, key: {}, field：{}, value: {}", key, field, value, e);
            throw new CommonException(String.format("RedisCommands, fail to hset, key: {}, field：{}, value: {}", key, field, value), e);
        } finally {
            jedis.close();
        }
        return lng;
    }

    @Override
    public String get(String key) {
        Jedis jedis = null;
        String value = null;
        try {
            jedis = redisPool.getResource();
            value = jedis.get(key);
        } catch (Exception e) {
            LOGGER.error(String.format("RedisCommands, fail to get, key：%s", key), e);
            throw new CommonException(String.format("RedisCommands, fail to get, key：%s", key), e);
        } finally {
            jedis.close();
        }
        return value;
    }

    @Override
    public String hget(String key, String field) {
        Jedis jedis = null;
        String value = null;
        try {
            jedis = redisPool.getResource();
            value = jedis.hget(key, field);
        } catch (Exception e) {
            LOGGER.error(String.format("RedisCommands, fail to hget, key：%s, field：%s", key, field), e);
            throw new CommonException(String.format("RedisCommands, fail to hget, key：%s, field：%s", key, field), e);
        } finally {
            jedis.close();
        }
        return value;
    }

    @Override
    public Boolean exists(String key) {
        Jedis jedis = null;
        Boolean value = false;
        try {
            jedis = redisPool.getResource();
            value = jedis.exists(key);
        } catch (Exception e) {
            LOGGER.error(String.format("RedisCommands, fail to exists, key：%s", key), e);
            throw new CommonException(String.format("RedisCommands, fail to exists, key：%s", key), e);
        } finally {
            jedis.close();
        }
        return value;
    }

    @Override
    public Boolean hexists(String key, String field) {
        Jedis jedis = null;
        Boolean value = false;
        try {
            jedis = redisPool.getResource();
            value = jedis.hexists(key, field);
        } catch (Exception e) {
            LOGGER.error(String.format("RedisCommands, fail to hexists, key：%s，field：%s", key), e);
            throw new CommonException(String.format("RedisCommands, fail to hexists, key：%s，field：%s", key), e);
        } finally {
            jedis.close();
        }
        return value;
    }

    @Override
    public Long setnx(String key, String value) {
        Jedis jedis = null;
        Long lng = 0L;
        try {
            jedis = redisPool.getResource();
            lng = jedis.setnx(key, value);
        } catch (Exception e) {
            LOGGER.error(String.format("RedisCommands, fail to setnx, key：%s, value：%s", key, value), e);
            throw new CommonException(String.format("RedisCommands, fail to setnx, key：%s, value：%s", key, value), e);
        } finally {
            jedis.close();
        }
        return lng;
    }

    @Override
    public Long hset(String key, Map<String, String> hash) {
        Jedis jedis = null;
        Long ret = 0L;
        try {
            jedis = redisPool.getResource();
            ret = jedis.hset(key, hash);
        } catch (Exception e) {
            LOGGER.error(String.format("RedisCommands, fail to hset, key：%s, hash：%s", key, hash.toString()), e);
            throw new CommonException(String.format("RedisCommands, fail to hset, key：%s, hash：%s", key, hash.toString()), e);
        } finally {
            jedis.close();
        }
        return ret;
    }

    @Override
    public Long hsetnx(String key, String field, String value) {
        Jedis jedis = null;
        Long ret = 0L;
        try {
            jedis = redisPool.getResource();
            ret = jedis.hsetnx(key, field, value);
        } catch (Exception e) {
            LOGGER.error(String.format("RedisCommands, fail to hsetnx, key：%s, field：%s，value：%s", key, field, value), e);
            throw new CommonException(String.format("RedisCommands, fail to hsetnx, key：%s, field：%s，value：%s", key, field, value), e);
        } finally {
            jedis.close();
        }
        return ret;
    }

    @Override
    public String hmset(String key, Map<String, String> hash) {
        Jedis jedis = null;
        String value = null;
        try {
            jedis = redisPool.getResource();
            value = jedis.hmset(key, hash);
        } catch (Exception e) {
            LOGGER.error(String.format("RedisCommands, fail to hmset, key：%s, hash：%s", key, hash.toString()), e);
            throw new CommonException(String.format("RedisCommands, fail to hmset, key：%s, hash：%s", key, hash.toString()), e);
        } finally {
            jedis.close();
        }
        return value;
    }


    @Override
    public Long expire(String key, int seconds) {
        Jedis jedis = redisPool.getResource();
        Long lng = null;
        try {
            lng = jedis.expire(key, seconds);
        } catch (Exception e) {
            LOGGER.error(String.format("RedisCommands, fail to expire，key：%s，seconds：%s", key, seconds), e);
            throw new CommonException(String.format("RedisCommands, fail to expire，key：%s，seconds：%s", key, seconds), e);
        }
        return lng;
    }


    @Override
    public Long hlen(String key) {
        Jedis jedis = redisPool.getResource();
        Long lng = null;
        try {
            lng = jedis.hlen(key);
        } catch (Exception e) {
            LOGGER.error(String.format("RedisCommands, fail to hlen，key：%s", key), e);
            throw new CommonException(String.format("RedisCommands, fail to hlen，key：%s", key), e);
        }
        return lng;
    }


    @Override
    public Long hdel(String key, String... field) {
        Jedis jedis = redisPool.getResource();
        Long lng = null;
        try {
            lng = jedis.hdel(key, field);
        } catch (Exception e) {
            LOGGER.error(String.format("RedisCommands, fail to hdel，key：%s，value：%s", key, field), e);
            throw new CommonException(String.format("RedisCommands, fail to hdel，key：%s，value：%s", key, field), e);
        }
        return lng;
    }


    @Override
    public Long del(String key) {
        Jedis jedis = redisPool.getResource();
        Long lng = null;
        try {
            lng = jedis.del(key);
        } catch (Exception e) {
            LOGGER.error(String.format("RedisCommands, fail to del，key：%s", key), e);
            throw new CommonException(String.format("RedisCommands, fail to del，key：%s", key), e);
        }
        return lng;
    }


    @Override
    public Long lpush(String key, String... string) {
        Jedis jedis = redisPool.getResource();
        Long lng = null;
        try {
            lng = jedis.lpush(key, string);
        } catch (Exception e) {
            LOGGER.error(String.format("RedisCommands, fail to lpush，key：%s，string：%s", key, string), e);
            throw new CommonException(String.format("RedisCommands, fail to lpush，key：%s，string：%s", key, string), e);
        }
        return lng;
    }

    @Override
    public Long llen(String key) {
        Jedis jedis = redisPool.getResource();
        Long lng = null;
        try {
            lng = jedis.llen(key);
        } catch (Exception e) {
            LOGGER.error(String.format("RedisCommands, fail to llen，key：%s", key), e);
            throw new CommonException(String.format("RedisCommands, fail to llen，key：%s", key), e);
        }
        return lng;
    }

    @Override
    public String lset(String key, long index, String value) {
        Jedis jedis = null;
        String ret = null;
        try {
            jedis = redisPool.getResource();
            ret = jedis.lset(key, index, value);
        } catch (Exception e) {
            LOGGER.error(String.format("RedisCommands, fail to lset, key：%s, index：%s，value：%s", key, index, value), e);
            throw new CommonException(String.format("RedisCommands, fail to lset, key：%s, index：%s，value：%s", key, index, value), e);
        } finally {
            jedis.close();
        }
        return ret;
    }

    @Override
    public Long linsert(String key, ListPosition where, String pivot, String value) {
        Jedis jedis = redisPool.getResource();
        Long lng = null;
        try {
            lng = jedis.linsert(key, where, pivot, value);
        } catch (Exception e) {
            LOGGER.error(String.format("RedisCommands, fail to linsert，key：%s，pivot：%s，value：%s", key, pivot, value), e);
            throw new CommonException(String.format("RedisCommands, fail to linsert，key：%s，pivot：%s，value：%s", key, pivot, value), e);
        }
        return lng;
    }

    @Override
    public String lindex(String key, long index) {
        Jedis jedis = null;
        String ret = null;
        try {
            jedis = redisPool.getResource();
            ret = jedis.lindex(key, index);
        } catch (Exception e) {
            LOGGER.error(String.format("RedisCommands, fail to lindex, key：%s, index：%s", key, index), e);
            throw new CommonException(String.format("RedisCommands, fail to lindex, key：%s, index：%s", key, index), e);
        } finally {
            jedis.close();
        }
        return ret;
    }



    /*
     * 如下方法暂时不实现
     */

    @Override
    public List<String> hmget(String key, String... fields) {
        return null;
    }


    @Override
    public Long pexpire(String key, long milliseconds) {
        return null;
    }


    @Override
    public String set(String key, String value, SetParams params) {
        return null;
    }

    @Override
    public Long persist(String key) {
        return null;
    }

    @Override
    public String type(String key) {
        return null;
    }

    @Override
    public byte[] dump(String key) {
        return new byte[0];
    }

    @Override
    public String restore(String key, int ttl, byte[] serializedValue) {
        return null;
    }

    @Override
    public String restoreReplace(String key, int ttl, byte[] serializedValue) {
        return null;
    }

    @Override
    public Long expireAt(String key, long unixTime) {
        return null;
    }

    @Override
    public Long pexpireAt(String key, long millisecondsTimestamp) {
        return null;
    }

    @Override
    public Long ttl(String key) {
        return null;
    }

    @Override
    public Long pttl(String key) {
        return null;
    }

    @Override
    public Long touch(String key) {
        return null;
    }

    @Override
    public Boolean setbit(String key, long offset, boolean value) {
        return null;
    }

    @Override
    public Boolean setbit(String key, long offset, String value) {
        return null;
    }

    @Override
    public Boolean getbit(String key, long offset) {
        return null;
    }

    @Override
    public Long setrange(String key, long offset, String value) {
        return null;
    }

    @Override
    public String getrange(String key, long startOffset, long endOffset) {
        return null;
    }

    @Override
    public String getSet(String key, String value) {
        return null;
    }

    @Override
    public String setex(String key, int seconds, String value) {
        return null;
    }

    @Override
    public String psetex(String key, long milliseconds, String value) {
        return null;
    }

    @Override
    public Long decrBy(String key, long decrement) {
        return null;
    }

    @Override
    public Long decr(String key) {
        return null;
    }

    @Override
    public Long incrBy(String key, long increment) {
        return null;
    }

    @Override
    public Double incrByFloat(String key, double increment) {
        return null;
    }

    @Override
    public Long incr(String key) {
        return null;
    }

    @Override
    public Long append(String key, String value) {
        return null;
    }

    @Override
    public String substr(String key, int start, int end) {
        return null;
    }


    @Override
    public Long hincrBy(String key, String field, long value) {
        return null;
    }

    @Override
    public Double hincrByFloat(String key, String field, double value) {
        return null;
    }

    @Override
    public Set<String> hkeys(String key) {
        return null;
    }

    @Override
    public List<String> hvals(String key) {
        return null;
    }

    @Override
    public Map<String, String> hgetAll(String key) {
        return null;
    }

    @Override
    public Long rpush(String key, String... string) {
        return null;
    }

    @Override
    public List<String> lrange(String key, long start, long stop) {
        return null;
    }

    @Override
    public String ltrim(String key, long start, long stop) {
        return null;
    }

    @Override
    public Long lrem(String key, long count, String value) {
        return null;
    }

    @Override
    public String lpop(String key) {
        return null;
    }

    @Override
    public String rpop(String key) {
        return null;
    }

    @Override
    public Long sadd(String key, String... member) {
        return null;
    }

    @Override
    public Set<String> smembers(String key) {
        return null;
    }

    @Override
    public Long srem(String key, String... member) {
        return null;
    }

    @Override
    public String spop(String key) {
        return null;
    }

    @Override
    public Set<String> spop(String key, long count) {
        return null;
    }

    @Override
    public Long scard(String key) {
        return null;
    }

    @Override
    public Boolean sismember(String key, String member) {
        return null;
    }

    @Override
    public String srandmember(String key) {
        return null;
    }

    @Override
    public List<String> srandmember(String key, int count) {
        return null;
    }

    @Override
    public Long strlen(String key) {
        return null;
    }

    @Override
    public Long zadd(String key, double score, String member) {
        return null;
    }

    @Override
    public Long zadd(String key, double score, String member, ZAddParams params) {
        return null;
    }

    @Override
    public Long zadd(String key, Map<String, Double> scoreMembers) {
        return null;
    }

    @Override
    public Long zadd(String key, Map<String, Double> scoreMembers, ZAddParams params) {
        return null;
    }

    @Override
    public Set<String> zrange(String key, long start, long stop) {
        return null;
    }

    @Override
    public Long zrem(String key, String... members) {
        return null;
    }

    @Override
    public Double zincrby(String key, double increment, String member) {
        return null;
    }

    @Override
    public Double zincrby(String key, double increment, String member, ZIncrByParams params) {
        return null;
    }

    @Override
    public Long zrank(String key, String member) {
        return null;
    }

    @Override
    public Long zrevrank(String key, String member) {
        return null;
    }

    @Override
    public Set<String> zrevrange(String key, long start, long stop) {
        return null;
    }

    @Override
    public Set<Tuple> zrangeWithScores(String key, long start, long stop) {
        return null;
    }

    @Override
    public Set<Tuple> zrevrangeWithScores(String key, long start, long stop) {
        return null;
    }

    @Override
    public Long zcard(String key) {
        return null;
    }

    @Override
    public Double zscore(String key, String member) {
        return null;
    }

    @Override
    public List<String> sort(String key) {
        return null;
    }

    @Override
    public List<String> sort(String key, SortingParams sortingParameters) {
        return null;
    }

    @Override
    public Long zcount(String key, double min, double max) {
        return null;
    }

    @Override
    public Long zcount(String key, String min, String max) {
        return null;
    }

    @Override
    public Set<String> zrangeByScore(String key, double min, double max) {
        return null;
    }

    @Override
    public Set<String> zrangeByScore(String key, String min, String max) {
        return null;
    }

    @Override
    public Set<String> zrevrangeByScore(String key, double max, double min) {
        return null;
    }

    @Override
    public Set<String> zrangeByScore(String key, double min, double max, int offset, int count) {
        return null;
    }

    @Override
    public Set<String> zrevrangeByScore(String key, String max, String min) {
        return null;
    }

    @Override
    public Set<String> zrangeByScore(String key, String min, String max, int offset, int count) {
        return null;
    }

    @Override
    public Set<String> zrevrangeByScore(String key, double max, double min, int offset, int count) {
        return null;
    }

    @Override
    public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max) {
        return null;
    }

    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min) {
        return null;
    }

    @Override
    public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max, int offset, int count) {
        return null;
    }

    @Override
    public Set<String> zrevrangeByScore(String key, String max, String min, int offset, int count) {
        return null;
    }

    @Override
    public Set<Tuple> zrangeByScoreWithScores(String key, String min, String max) {
        return null;
    }

    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(String key, String max, String min) {
        return null;
    }

    @Override
    public Set<Tuple> zrangeByScoreWithScores(String key, String min, String max, int offset, int count) {
        return null;
    }

    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min, int offset, int count) {
        return null;
    }

    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(String key, String max, String min, int offset, int count) {
        return null;
    }

    @Override
    public Long zremrangeByRank(String key, long start, long stop) {
        return null;
    }

    @Override
    public Long zremrangeByScore(String key, double min, double max) {
        return null;
    }

    @Override
    public Long zremrangeByScore(String key, String min, String max) {
        return null;
    }

    @Override
    public Long zlexcount(String key, String min, String max) {
        return null;
    }

    @Override
    public Set<String> zrangeByLex(String key, String min, String max) {
        return null;
    }

    @Override
    public Set<String> zrangeByLex(String key, String min, String max, int offset, int count) {
        return null;
    }

    @Override
    public Set<String> zrevrangeByLex(String key, String max, String min) {
        return null;
    }

    @Override
    public Set<String> zrevrangeByLex(String key, String max, String min, int offset, int count) {
        return null;
    }

    @Override
    public Long zremrangeByLex(String key, String min, String max) {
        return null;
    }


    @Override
    public Long lpushx(String key, String... string) {
        return null;
    }

    @Override
    public Long rpushx(String key, String... string) {
        return null;
    }

    @Override
    public List<String> blpop(int timeout, String key) {
        return null;
    }

    @Override
    public List<String> brpop(int timeout, String key) {
        return null;
    }

    @Override
    public Long unlink(String key) {
        return null;
    }

    @Override
    public String echo(String string) {
        return null;
    }

    @Override
    public Long move(String key, int dbIndex) {
        return null;
    }

    @Override
    public Long bitcount(String key) {
        return null;
    }

    @Override
    public Long bitcount(String key, long start, long end) {
        return null;
    }

    @Override
    public Long bitpos(String key, boolean value) {
        return null;
    }

    @Override
    public Long bitpos(String key, boolean value, BitPosParams params) {
        return null;
    }

    @Override
    public ScanResult<Map.Entry<String, String>> hscan(String key, String cursor) {
        return null;
    }

    @Override
    public ScanResult<Map.Entry<String, String>> hscan(String key, String cursor, ScanParams params) {
        return null;
    }

    @Override
    public ScanResult<String> sscan(String key, String cursor) {
        return null;
    }

    @Override
    public ScanResult<Tuple> zscan(String key, String cursor) {
        return null;
    }

    @Override
    public ScanResult<Tuple> zscan(String key, String cursor, ScanParams params) {
        return null;
    }

    @Override
    public ScanResult<String> sscan(String key, String cursor, ScanParams params) {
        return null;
    }

    @Override
    public Long pfadd(String key, String... elements) {
        return null;
    }

    @Override
    public long pfcount(String key) {
        return 0;
    }

    @Override
    public Long geoadd(String key, double longitude, double latitude, String member) {
        return null;
    }

    @Override
    public Long geoadd(String key, Map<String, GeoCoordinate> memberCoordinateMap) {
        return null;
    }

    @Override
    public Double geodist(String key, String member1, String member2) {
        return null;
    }

    @Override
    public Double geodist(String key, String member1, String member2, GeoUnit unit) {
        return null;
    }

    @Override
    public List<String> geohash(String key, String... members) {
        return null;
    }

    @Override
    public List<GeoCoordinate> geopos(String key, String... members) {
        return null;
    }

    @Override
    public List<GeoRadiusResponse> georadius(String key, double longitude, double latitude, double radius, GeoUnit unit) {
        return null;
    }

    @Override
    public List<GeoRadiusResponse> georadiusReadonly(String key, double longitude, double latitude, double radius, GeoUnit unit) {
        return null;
    }

    @Override
    public List<GeoRadiusResponse> georadius(String key, double longitude, double latitude, double radius, GeoUnit unit, GeoRadiusParam param) {
        return null;
    }

    @Override
    public List<GeoRadiusResponse> georadiusReadonly(String key, double longitude, double latitude, double radius, GeoUnit unit, GeoRadiusParam param) {
        return null;
    }

    @Override
    public List<GeoRadiusResponse> georadiusByMember(String key, String member, double radius, GeoUnit unit) {
        return null;
    }

    @Override
    public List<GeoRadiusResponse> georadiusByMemberReadonly(String key, String member, double radius, GeoUnit unit) {
        return null;
    }

    @Override
    public List<GeoRadiusResponse> georadiusByMember(String key, String member, double radius, GeoUnit unit, GeoRadiusParam param) {
        return null;
    }

    @Override
    public List<GeoRadiusResponse> georadiusByMemberReadonly(String key, String member, double radius, GeoUnit unit, GeoRadiusParam param) {
        return null;
    }

    @Override
    public List<Long> bitfield(String key, String... arguments) {
        return null;
    }

    @Override
    public Long hstrlen(String key, String field) {
        return null;
    }

}
