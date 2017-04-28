package mybatis.sharding.quickstart.plugin;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.binding.MapperMethod.ParamMap;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlDeleteStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlUpdateStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.google.common.collect.Maps;

import lombok.extern.slf4j.Slf4j;
import mybatis.sharding.quickstart.annotation.ShardMethod;
import mybatis.sharding.quickstart.context.ShardMethodFactory;
import mybatis.sharding.quickstart.route.SingleTableRouterFactory;
import mybatis.sharding.quickstart.util.Reflections;


@Slf4j
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class,Integer.class})})
public class StatementHandlerPrepareInterceptor implements Interceptor{

    private static final ObjectFactory DEFAULT_OBJECT_FACTORY = new DefaultObjectFactory();
    private static final ObjectWrapperFactory DEFAULT_OBJECT_WRAPPER_FACTORY = new DefaultObjectWrapperFactory();
    private static final ReflectorFactory REFLECTOR_FACTORY = new DefaultReflectorFactory();
    
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        
        MetaObject metaStatementHandler = MetaObject.forObject(statementHandler, DEFAULT_OBJECT_FACTORY,DEFAULT_OBJECT_WRAPPER_FACTORY,REFLECTOR_FACTORY);
        // 分离代理对象链(由于目标类可能被多个拦截器拦截，从而形成多次代理，通过下面的两次循环可以分离出最原始的的目标类)
        while (metaStatementHandler.hasGetter("h")) {
            Object object = metaStatementHandler.getValue("h");
            metaStatementHandler = MetaObject.forObject(object, DEFAULT_OBJECT_FACTORY, DEFAULT_OBJECT_WRAPPER_FACTORY,REFLECTOR_FACTORY);
        }
        // 分离最后一个代理对象的目标类
        while (metaStatementHandler.hasGetter("target")) {
            Object object = metaStatementHandler.getValue("target");
            metaStatementHandler = MetaObject.forObject(object, DEFAULT_OBJECT_FACTORY, DEFAULT_OBJECT_WRAPPER_FACTORY,REFLECTOR_FACTORY);
        }
        MappedStatement mappedStatement = (MappedStatement) metaStatementHandler.getValue("delegate.mappedStatement");
        
        String mappedStatementId = mappedStatement.getId();
        
        Method statementTargetMethod = ShardMethodFactory.getStatementMethodById(mappedStatementId);
        
        if (statementTargetMethod==null) {
            return invocation.proceed();
        }
        
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
        
        String sqlCommandTypeName = sqlCommandType.name();
        
        BoundSql boundSql = (BoundSql) metaStatementHandler.getValue("delegate.boundSql");
        
        DefaultParameterHandler parameterHandler = (DefaultParameterHandler) metaStatementHandler.getValue("delegate.parameterHandler");
       
        //statement原始SQL
        String sql = boundSql.getSql();
        
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement statement = parser.parseStatement();
        
        String tableName = "";
        
        
        
        if(sqlCommandType.equals(SqlCommandType.INSERT)){
            MySqlInsertStatement mysqlInsertStatement = (MySqlInsertStatement) statement;
            tableName = mysqlInsertStatement.getTableName().getSimpleName();
        }else if(sqlCommandType.equals(SqlCommandType.DELETE)){
            MySqlDeleteStatement mysqlDeleteStatement = (MySqlDeleteStatement) statement;
            tableName = mysqlDeleteStatement.getTableName().getSimpleName();
        }else if(sqlCommandType.equals(SqlCommandType.UPDATE)){
            MySqlUpdateStatement mysqlUpdateStatement = (MySqlUpdateStatement) statement;
            tableName =mysqlUpdateStatement.getTableName().getSimpleName();
        }else if(sqlCommandType.equals(SqlCommandType.SELECT)){
//            SQLSelectStatement sqlSelectStatement = (SQLSelectStatement) statement;
            MySqlSchemaStatVisitor sqlParserVisitor = new MySqlSchemaStatVisitor();
            statement.accept(sqlParserVisitor);
            
            tableName = sqlParserVisitor.getCurrentTable();
        }

        ShardMethod shardMethodMark = statementTargetMethod.getAnnotation(ShardMethod.class);
        
        if(shardMethodMark!=null){
            
            Object parameterObject = parameterHandler.getParameterObject();
            
            boolean isSimpleShardTable = shardMethodMark.simple();
            
            String expression = shardMethodMark.expression();
            
            String convertAfterSQL = sql;
            
            Map<String,Object> expressionData = Maps.newHashMap();
            
            if(parameterObject!=null){
                if (isSimpleShardTable) {
                    if(parameterObject instanceof MapperMethod.ParamMap){
                        @SuppressWarnings("unchecked")
                        MapperMethod.ParamMap<Object> paramMaps = (ParamMap<Object>) parameterObject;
                        
                        Object propertyObject = paramMaps.get(shardMethodMark.shardProperty());
                        
                        
                        if(instanceofSingleValueType(propertyObject)){
                            expressionData.put(shardMethodMark.shardProperty(), paramMaps.get(shardMethodMark.shardProperty()));    
                        }else if(instanceofCollectionValueType(propertyObject)){
                            Object[] vv = (Object[]) paramMaps.get(shardMethodMark.shardProperty());
                            expressionData.put(shardMethodMark.shardProperty(), vv[0]);
                        }else if(instanceofArrayValueType(propertyObject)){
                            Object[] vv = (Object[]) paramMaps.get(shardMethodMark.shardProperty());
                            expressionData.put(shardMethodMark.shardProperty(), vv[0]);
                        }else if(propertyObject.getClass().getName().equals("java.util.Arrays$ArrayList")){
                            throw new IllegalArgumentException("发生异常statementId:"+mappedStatementId + "不支持这种 @Paran(\"id\")List id 的写法");
                        }
                    }else if(instanceofSingleValueType(parameterObject)){
                        expressionData.put(shardMethodMark.shardProperty(), expressionData);
                        
                    }else if(instanceofCollectionValueType(parameterObject)){
                        @SuppressWarnings("unchecked")
                        Iterable<Object> it = ((Iterable<Object>) parameterObject);
                        if(it.iterator().hasNext()){
                            Object item = it.iterator().next();
                            expressionData.put(shardMethodMark.shardProperty(),Reflections.getFieldValue(item, shardMethodMark.shardProperty()));
                        }
                    }else{
                        throw new IllegalArgumentException("发生异常statementId:"+mappedStatementId + "参数异常");
                    }
                    convertAfterSQL = SingleTableRouterFactory.convert(sqlCommandTypeName, sql, tableName ,expressionData, expression);                
                }else{
                    expressionData.put(shardMethodMark.shardProperty(), Reflections.getFieldValue(parameterObject, shardMethodMark.shardProperty()));
                    convertAfterSQL = SingleTableRouterFactory.convert(sqlCommandTypeName, sql, tableName,expressionData, expression);
                }
                Reflections.setFieldValue(boundSql, "sql", convertAfterSQL);
                log.info("sql:"+convertAfterSQL);
            }
            
        }
        return invocation.proceed();

    }
    @Override
    public Object plugin(Object target) {
        // 当目标类是StatementHandler类型时，才包装目标类，否者直接返回目标本身,减少目标被代理的次数
        if (target instanceof StatementHandler) {
            return Plugin.wrap(target, this);
        } else {
            return target;
        }
    }
    
    @Override
    public void setProperties(Properties properties) {
    } 
    
    private boolean instanceofSingleValueType(Object o){
        Set<Class<?>> s = Types.SINGLE_VALUE_TYPE;
        return s.contains(o.getClass());
    }
    
    private boolean instanceofCollectionValueType(Object o){
        Set<Class<?>> s = Types.COLLECTION_VALUE_TYPE;
        return s.contains(o.getClass());
    }
    
    private boolean instanceofArrayValueType(Object o){
        Set<Class<?>> s = Types.ARRAY_VALUE_TYPE;
        return s.contains(o.getClass());
    }
    
}