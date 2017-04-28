package mybatis.sharding.quickstart.route;

import net.sf.jsqlparser.statement.Statement;

public interface TableRouter {
    
    String convert(Statement statement,String tableName,Object param,String function_expression);
    
}
