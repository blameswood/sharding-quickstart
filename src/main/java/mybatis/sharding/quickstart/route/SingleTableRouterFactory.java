package mybatis.sharding.quickstart.route;

import java.io.StringReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.ibatis.mapping.SqlCommandType;

import com.google.common.base.Throwables;

import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.Statement;

@Slf4j
public class SingleTableRouterFactory {
    
    private static CCJSqlParserManager pm = new CCJSqlParserManager();
    
    private static Map<String, TableRouter> SQLCOMMANDTYPE_MAPPING_SQLCONVERT = new ConcurrentHashMap<String,TableRouter>();
    
    static {
        SQLCOMMANDTYPE_MAPPING_SQLCONVERT.put(SqlCommandType.INSERT.name(),new InsertObjectRoute());
        SQLCOMMANDTYPE_MAPPING_SQLCONVERT.put(SqlCommandType.DELETE.name(),new DeleteObjectRoute());
        SQLCOMMANDTYPE_MAPPING_SQLCONVERT.put(SqlCommandType.UPDATE.name(),new UpdateSingleTableRoute());
        SQLCOMMANDTYPE_MAPPING_SQLCONVERT.put(SqlCommandType.SELECT.name(),new SelectSingleTableRouter());
    }
    
    public static String convert(String sqlCommandType,String orginSql,String tableName,Object args,String function_expression){
        Statement statement = null;
        try {
            statement = pm.parse(new StringReader(orginSql));
        } catch (JSQLParserException e) {
            log.error(e.getMessage(), e);
            Throwables.propagate(e);
        }
        TableRouter sqlConverter = SQLCOMMANDTYPE_MAPPING_SQLCONVERT.get(sqlCommandType);
        if(sqlConverter != null){
            return sqlConverter.convert(statement, tableName ,args, function_expression);
        }
        return orginSql;
    }
 
}
