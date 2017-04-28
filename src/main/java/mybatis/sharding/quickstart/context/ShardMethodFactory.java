package mybatis.sharding.quickstart.context;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ShardMethodFactory {
    //key:statement id
    //target method
    private static Map<String, Method> STATEMENTID_MAPPING_METHOD = new HashMap<String, Method>();
    
    public static Method getStatementMethodById(String id){
        return STATEMENTID_MAPPING_METHOD.get(id);
    }
    
    public static Method regMethod(String id,Method method) {
        STATEMENTID_MAPPING_METHOD.put(id, method);
        return method;
    }
    
    public static Map<String, Method> getShardMethods(){
        return Collections.unmodifiableMap(STATEMENTID_MAPPING_METHOD);
    }
    
}   
