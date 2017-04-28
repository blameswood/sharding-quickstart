package mybatis.sharding.quickstart.route;

import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.insert.Insert;

public class InsertObjectRoute extends AbstractTableRouter {

    @Override
    protected Statement processConvert(Statement statement,String tableName, Object param, String function_expression) {
        Insert insert = (Insert) statement;
        String index = this.calcFunctionExpressionResult(function_expression, param);
        String suffix = getSuffixedIndex(index);
        insert.getTable().setName(super.concatTableName(tableName, suffix));
        return insert;
    }

}
