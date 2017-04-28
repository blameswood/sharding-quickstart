package mybatis.sharding.quickstart.plugin;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.google.common.collect.Sets;

/**
 * @author SongJian email:1738042258@QQ.COM
 * 必有用处
 */
public class Types {
    
    public static final Set<Class<?>> SINGLE_VALUE_TYPE = Sets.newHashSet();
    
    public static final Set<Class<?>> ARRAY_VALUE_TYPE = Sets.newHashSet();
    
    public static final Set<Class<?>> COLLECTION_VALUE_TYPE = Sets.newHashSet();
    
    public static final Set<Class<?>> COLLECTION_KVALUE_TYPE = Sets.newHashSet();
    
    static{
        SINGLE_VALUE_TYPE.add(String.class);
        SINGLE_VALUE_TYPE.add(Byte.class);
        SINGLE_VALUE_TYPE.add(Long.class);
        SINGLE_VALUE_TYPE.add(Short.class);
        SINGLE_VALUE_TYPE.add(Integer.class);
        SINGLE_VALUE_TYPE.add(Double.class);
        SINGLE_VALUE_TYPE.add(Float.class);
        SINGLE_VALUE_TYPE.add(Boolean.class);
        SINGLE_VALUE_TYPE.add(Date.class);
        SINGLE_VALUE_TYPE.add(BigDecimal.class);
        SINGLE_VALUE_TYPE.add(BigInteger.class);
        SINGLE_VALUE_TYPE.add(byte.class);
        SINGLE_VALUE_TYPE.add(long.class);
        SINGLE_VALUE_TYPE.add(short.class);
        SINGLE_VALUE_TYPE.add(int.class);
        SINGLE_VALUE_TYPE.add(double.class);
        SINGLE_VALUE_TYPE.add(float.class);
        SINGLE_VALUE_TYPE.add(boolean.class);
        
        
        ARRAY_VALUE_TYPE.add(String[].class);
        ARRAY_VALUE_TYPE.add(Byte[].class);
        ARRAY_VALUE_TYPE.add(Long[].class);
        ARRAY_VALUE_TYPE.add(Short[].class);
        ARRAY_VALUE_TYPE.add(Integer[].class);
        ARRAY_VALUE_TYPE.add(Double[].class);
        ARRAY_VALUE_TYPE.add(Float[].class);
        ARRAY_VALUE_TYPE.add(Boolean[].class);
        ARRAY_VALUE_TYPE.add(byte[].class);
        ARRAY_VALUE_TYPE.add(long[].class);
        ARRAY_VALUE_TYPE.add(short[].class);
        ARRAY_VALUE_TYPE.add(int[].class);
        ARRAY_VALUE_TYPE.add(double[].class);
        ARRAY_VALUE_TYPE.add(float[].class);
        ARRAY_VALUE_TYPE.add(boolean[].class);
        
        COLLECTION_VALUE_TYPE.add(List.class);
        COLLECTION_VALUE_TYPE.add(Iterable.class);
        COLLECTION_VALUE_TYPE.add(Set.class);
        COLLECTION_VALUE_TYPE.add(ArrayList.class);
        COLLECTION_VALUE_TYPE.add(Array.class);
        
        COLLECTION_KVALUE_TYPE.add(Map.class);
        COLLECTION_KVALUE_TYPE.add(HashMap.class);
        COLLECTION_KVALUE_TYPE.add(TreeMap.class);
        
    }
}
