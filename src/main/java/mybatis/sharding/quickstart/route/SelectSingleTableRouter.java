package mybatis.sharding.quickstart.route;


import mybatis.sharding.quickstart.plugin.support.TablesNamesFinder;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Select;

public class SelectSingleTableRouter extends AbstractTableRouter{

    @Override
    protected Statement processConvert(Statement statement, String tableName,Object param, String functionExpression) {
        Select selectStatement = (Select) statement;
        TablesNamesFinder tablesNamesFinder = new TablesNamesFinder(functionExpression,param);
        tablesNamesFinder.getTableList(selectStatement);
        return statement;
    }

}
