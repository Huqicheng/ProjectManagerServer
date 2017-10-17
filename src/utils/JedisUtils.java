package utils;

import java.util.List;
import java.util.Map;


import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.exceptions.JedisException;

public abstract class JedisUtils {
    public abstract int getDBIndex();
//    /**
//     * 默认日志打印logger_default
//     */
//    public static Logger logger_default = Logger.getLogger("logger_jCache_default");
//    /**
//     * 失败日志logger，用于定期del指定的key
//     */
//    public static Logger logger_failure = Logger.getLogger("logger_jCache_failure");

    
    protected JedisPool jedisPool;

    protected Jedis getJedis() throws JedisException {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.auth("redis");
        } catch (JedisException e) {
            //LogContext.instance().warn(logger_failure, "failed:jedisPool getResource.", e);
            if(jedis!=null){
                jedisPool.returnBrokenResource(jedis);
            }
            throw e;
        }
        return jedis;
    }

    protected String auth(String pwd){
    	Jedis jedis = null;
        boolean isBroken = false;
        String lastVal = null;
        try {
            jedis = this.getJedis();
            jedis.select(getDBIndex());
            lastVal = jedis.auth(pwd);
            //LogContext.instance().debug(logger_default, "succeed:" + methodName, key, value);
        } catch (Exception e) {
            isBroken = true;
            
            //LogContext.instance().warn(logger_failure, "failed:" + methodName, key, value, e);
        } finally {
            release(jedis, isBroken);
        }
        return lastVal; 
    }
    
    public void lpushOneString(String key,String str){
    	Jedis jedis = null;
        boolean isBroken = false;
        try {
            jedis = this.getJedis();
            jedis.select(getDBIndex());
            jedis.lpush(key, str);
            
            //LogContext.instance().debug(logger_default, "succeed:" + methodName, key, value);
        } catch (Exception e) {
            isBroken = true;
            System.out.println(e.getMessage());
            //LogContext.instance().warn(logger_failure, "failed:" + methodName, key, value, e);
        } finally {
            release(jedis, isBroken);
        }
        
    }
    
    public List<String> getStrings(String key,long end){
    	Jedis jedis = null;
        boolean isBroken = false;
        try {
            jedis = this.getJedis();
            jedis.select(getDBIndex());
            end = Math.max(jedis.llen(key), end);
            List<String> messageList = jedis.lrange(key, 0, (int) end);
//            for (int i = 0; i < messageList.size(); ++i)
//            {
//                System.out.println(messageList.get(i));
//            }
            return messageList;
            //LogContext.instance().debug(logger_default, "succeed:" + methodName, key, value);
        } catch (Exception e) {
            isBroken = true;
            //LogContext.instance().warn(logger_failure, "failed:" + methodName, key, value, e);
        } finally {
            release(jedis, isBroken);
        }
		return null;
        
    }
    
    public void ltrim(String key, int start, int end){
    	Jedis jedis = null;
        boolean isBroken = false;
        try {
            jedis = this.getJedis();
            jedis.select(getDBIndex());
            jedis.ltrim(key, start, end);
            
            //LogContext.instance().debug(logger_default, "succeed:" + methodName, key, value);
        } catch (Exception e) {
            isBroken = true;
            //LogContext.instance().warn(logger_failure, "failed:" + methodName, key, value, e);
        } finally {
            release(jedis, isBroken);
        }
        
    }
    protected void release(Jedis jedis, boolean isBroken) {
        if (jedis != null) {
            if (isBroken) {
                jedisPool.returnBrokenResource(jedis);
            } else {
                jedisPool.returnResource(jedis);
            }
        }
    }

    
    
    protected String addStringToJedis(String key, String value, int cacheSeconds, String methodName) {
        Jedis jedis = null;
        boolean isBroken = false;
        String lastVal = null;
        try {
            jedis = this.getJedis();
            jedis.select(getDBIndex());
            lastVal = jedis.getSet(key, value);
            if(cacheSeconds!=0){
                jedis.expire(key,cacheSeconds);
            }
            //LogContext.instance().debug(logger_default, "succeed:" + methodName, key, value);
        } catch (Exception e) {
            isBroken = true;
            //LogContext.instance().warn(logger_failure, "failed:" + methodName, key, value, e);
        } finally {
            release(jedis, isBroken);
        }
        return lastVal;
    }

    protected void addStringToJedis(Map<String,String> batchData, int cacheSeconds, String methodName) {
        Jedis jedis = null;
        boolean isBroken = false;
        try {
            jedis = this.getJedis();
            jedis.select(getDBIndex());
            Pipeline pipeline = jedis.pipelined();
            for(Map.Entry<String,String> element:batchData.entrySet()){
                if(cacheSeconds!=0){
                    pipeline.setex(element.getKey(),cacheSeconds,element.getValue());
                }else{
                    pipeline.set(element.getKey(),element.getValue());
                }
            }
            pipeline.sync();
            //LogContext.instance().debug(logger_default, "succeed:" + methodName,batchData.size());
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            release(jedis, isBroken);
        }
    }

    protected void addListToJedis(String key, List<String> list, int cacheSeconds, String methodName) {
        if (list != null && list.size() > 0) {
            Jedis jedis = null;
            boolean isBroken = false;
            try {
                jedis = this.getJedis();
                jedis.select(getDBIndex());
                if (jedis.exists(key)) {
                    jedis.del(key);
                }
                for (String aList : list) {
                    jedis.rpush(key, aList);
                }
                if(cacheSeconds!=0){
                    jedis.expire(key, cacheSeconds);
                }
                //LogContext.instance().debug(logger_default, "succeed:" + methodName, key, list.size());
            } catch (JedisException e) {
                isBroken = true;
                //LogContext.instance().warn(logger_failure, "failed:" + methodName, key, list.size(), e);
            } catch (Exception e) {
                isBroken = true;
                //LogContext.instance().warn(logger_failure, "failed:" + methodName, key, list.size(), e);
            } finally {
                release(jedis, isBroken);
            }
        }
    }

//    protected void addToSetJedis(String key, String[] value, String methodName) {
//        Jedis jedis = null;
//        boolean isBroken = false;
//        try {
//            jedis = this.getJedis();
//            jedis.select(getDBIndex());
//            jedis.sadd(key,value);
//            //LogContext.instance().debug(logger_default, "succeed:" + methodName, key, value);
//        } catch (Exception e) {
//            isBroken = true;
//            //LogContext.instance().warn(logger_failure, "failed:" + methodName, key, value, e);
//        } finally {
//            release(jedis, isBroken);
//        }
//    }

    protected void removeSetJedis(String key,String value, String methodName) {
        Jedis jedis = null;
        boolean isBroken = false;
        try {
            jedis = this.getJedis();
            jedis.select(getDBIndex());
            jedis.srem(key,value);
            //LogContext.instance().debug(logger_default, "succeed:" + methodName, key, value);
        } catch (Exception e) {
            isBroken = true;
            //LogContext.instance().warn(logger_failure, "failed:" + methodName, key, value, e);
        } finally {
            release(jedis, isBroken);
        }
    }

    protected void pushDataToListJedis(String key, String data, int cacheSeconds, String methodName) {
        Jedis jedis = null;
        boolean isBroken = false;
        try {
            jedis = this.getJedis();
            jedis.select(getDBIndex());
            jedis.rpush(key, data);
            if(cacheSeconds!=0){
                jedis.expire(key,cacheSeconds);
            }
            //LogContext.instance().debug(logger_default, "succeed:" + methodName, key, data);
        } catch (Exception e) {
            isBroken = true;
            //LogContext.instance().warn(logger_failure, "failed:" + methodName, key, data, e);
        } finally {
            release(jedis, isBroken);
        }
    }
//    protected void pushDataToListJedis(String key,List<String> batchData, int cacheSeconds, String methodName) {
//        Jedis jedis = null;
//        boolean isBroken = false;
//        try {
//            jedis = this.getJedis();
//            jedis.select(getDBIndex());
//            jedis.del(key);
//            jedis.lpush(key,batchData.toArray(new String[batchData.size()]));
//            if(cacheSeconds!=0)
//                jedis.expire(key,cacheSeconds);
//            //LogContext.instance().debug(logger_default, "succeed:" + methodName,batchData!=null?batchData.size():0);
//        } catch (Exception e) {
//            isBroken = true;
//            //LogContext.instance().warn(logger_failure, "failed:" + methodName, batchData!=null?batchData.size():0, e);
//        } finally {
//            release(jedis, isBroken);
//        }
//    }

    /**
     * 删除list中的元素
     * @param key
     * @param values
     * @param methodName
     */
    protected void deleteDataFromListJedis(String key,List<String> values, String methodName) {
        Jedis jedis = null;
        boolean isBroken = false;
        try {
            jedis = this.getJedis();
            jedis.select(getDBIndex());
            Pipeline pipeline = jedis.pipelined();
            if(values!=null && !values.isEmpty()){
                for (String val:values){
                    pipeline.lrem(key,0,val);
                }
            }
            pipeline.sync();
            //LogContext.instance().debug(logger_default, "succeed:" + methodName,values!=null?values.size():0);
        } catch (Exception e) {
            isBroken = true;
            //LogContext.instance().warn(logger_failure, "failed:" + methodName, values!=null?values.size():0, e);
        } finally {
            release(jedis, isBroken);
        }
    }

    protected void addHashMapToJedis(String key, Map<String, String> map, int cacheSeconds, boolean isModified, String methodName) {
        boolean isBroken = false;
        Jedis jedis = null;
        if (map != null && map.size() > 0) {
            try {
                jedis = this.getJedis();
                jedis.select(getDBIndex());
                jedis.hmset(key, map);
                if (cacheSeconds >= 0)
                    jedis.expire(key, cacheSeconds);
                //LogContext.instance().debug(logger_default, "succeed:" + methodName, key, map.size());
            } catch (Exception e) {
                isBroken = true;
                //LogContext.instance().warn(logger_failure, "failed:" + methodName, key, map.size(), e);
            } finally {
                release(jedis, isBroken);
            }
        }
    }

    protected void addHashMapToJedis(String key, String field, String value, int cacheSeconds, String methodName) {
        boolean isBroken = false;
        Jedis jedis = null;
        try {
            jedis = this.getJedis();
            if (jedis != null) {
                jedis.select(getDBIndex());
                jedis.hset(key, field, value);
                jedis.expire(key, cacheSeconds);
                //LogContext.instance().debug(logger_default, "succeed:" + methodName, key, field, value);
            }
        } catch (Exception e) {
            isBroken = true;
            //LogContext.instance().warn(logger_failure, "failed:" + methodName, key, field, value, e);
        }finally {
            release(jedis, isBroken);
        }
    }

    protected void updateHashMapToJedis(String key, String incrementField, long incrementValue, String dateField, String dateValue, String methodName) {
        boolean isBroken = false;
        Jedis jedis = null;
        try {
            jedis = this.getJedis();
            jedis.select(getDBIndex());
            jedis.hincrBy(key, incrementField, incrementValue);
            jedis.hset(key, dateField, dateValue);
            //LogContext.instance().debug(logger_default, "succeed:" + methodName, key, incrementField, incrementValue);
        } catch (Exception e) {
            isBroken = true;
            //LogContext.instance().warn(logger_failure, "failed:" + methodName, key, incrementField, incrementValue, e);
        }finally {
            release(jedis, isBroken);
        }
    }


    

}
