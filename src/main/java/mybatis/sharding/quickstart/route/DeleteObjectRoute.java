package mybatis.sharding.quickstart.route;

import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;

public class DeleteObjectRoute extends AbstractTableRouter {

    @Override
    protected Statement processConvert(Statement statement,String tableName, Object param, String function_expression) {
        Delete delete = (Delete) statement;
        String index = calcFunctionExpressionResult(function_expression, param);
        String suffix = getSuffixedIndex(index);
        delete.getTable().setName(super.concatTableName(tableName, suffix));
        return delete;
    }

}
