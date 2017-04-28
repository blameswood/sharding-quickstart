package mybatis.sharding.quickstart.route;

import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.update.Update;

public class UpdateSingleTableRoute extends AbstractTableRouter {

    @Override
    protected Statement processConvert(Statement statement,String tableName, Object param, String expression) {
        Update update = (Update) statement;
        
        Table table = update.getTables().get(0);
        
        String tableOrginName = table.getName();
        
        String index = calcFunctionExpressionResult(expression, param);
        
        String suffix = getSuffixedIndex(index);
        table.setName(super.concatTableName(tableOrginName, suffix));
        return update;
    }

}
