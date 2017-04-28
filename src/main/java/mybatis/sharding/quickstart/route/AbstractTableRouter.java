package mybatis.sharding.quickstart.route;

import org.apache.commons.lang3.StringUtils;
import org.mvel2.MVEL;

import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.util.deparser.StatementDeParser;

public abstract class AbstractTableRouter implements TableRouter{

    public static final String TABLE_NAME_SLIPTOR = "_";
    
    protected String revert(Statement statement) {
        StatementDeParser deParser = new StatementDeParser(new StringBuilder());
        statement.accept(deParser);
        return deParser.getBuffer().toString();
    }
    
    protected String concatTableName(String orgin,String suffix){
        return orgin + TABLE_NAME_SLIPTOR + suffix;
    }
    
    @Override
    public String convert(Statement statement, String tableName,Object param, String functionExpression) {
        return revert(processConvert(statement,tableName,param,functionExpression));
    }
    
    protected String calcFunctionExpressionResult(String expression,Object param){
        return String.valueOf(MVEL.eval(expression, param));
    }
    
    protected String getSuffixedIndex(String index){
        return StringUtils.leftPad(index, 4, "0");
    }
    
    protected abstract Statement processConvert(Statement statement, String tableName,Object param, String functionExpression);
    
}