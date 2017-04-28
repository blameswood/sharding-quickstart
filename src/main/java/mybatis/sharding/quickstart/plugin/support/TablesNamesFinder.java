package mybatis.sharding.quickstart.plugin.support;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.mvel2.MVEL;

import mybatis.sharding.quickstart.route.AbstractTableRouter;
import net.sf.jsqlparser.expression.AllComparisonExpression;
import net.sf.jsqlparser.expression.AnalyticExpression;
import net.sf.jsqlparser.expression.AnyComparisonExpression;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.CaseExpression;
import net.sf.jsqlparser.expression.CastExpression;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression;
import net.sf.jsqlparser.expression.DateValue;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.expression.ExtractExpression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.HexValue;
import net.sf.jsqlparser.expression.IntervalExpression;
import net.sf.jsqlparser.expression.JdbcNamedParameter;
import net.sf.jsqlparser.expression.JdbcParameter;
import net.sf.jsqlparser.expression.JsonExpression;
import net.sf.jsqlparser.expression.KeepExpression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.MySQLGroupConcat;
import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.NumericBind;
import net.sf.jsqlparser.expression.OracleHierarchicalExpression;
import net.sf.jsqlparser.expression.OracleHint;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.RowConstructor;
import net.sf.jsqlparser.expression.SignedExpression;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.TimeKeyExpression;
import net.sf.jsqlparser.expression.TimeValue;
import net.sf.jsqlparser.expression.TimestampValue;
import net.sf.jsqlparser.expression.UserVariable;
import net.sf.jsqlparser.expression.WhenClause;
import net.sf.jsqlparser.expression.WithinGroupExpression;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseAnd;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseOr;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseXor;
import net.sf.jsqlparser.expression.operators.arithmetic.Concat;
import net.sf.jsqlparser.expression.operators.arithmetic.Division;
import net.sf.jsqlparser.expression.operators.arithmetic.Modulo;
import net.sf.jsqlparser.expression.operators.arithmetic.Multiplication;
import net.sf.jsqlparser.expression.operators.arithmetic.Subtraction;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.Between;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExistsExpression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.IsNullExpression;
import net.sf.jsqlparser.expression.operators.relational.ItemsListVisitor;
import net.sf.jsqlparser.expression.operators.relational.LikeExpression;
import net.sf.jsqlparser.expression.operators.relational.Matches;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;
import net.sf.jsqlparser.expression.operators.relational.MultiExpressionList;
import net.sf.jsqlparser.expression.operators.relational.NotEqualsTo;
import net.sf.jsqlparser.expression.operators.relational.RegExpMatchOperator;
import net.sf.jsqlparser.expression.operators.relational.RegExpMySQLOperator;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.FromItemVisitor;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.LateralSubSelect;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectVisitor;
import net.sf.jsqlparser.statement.select.SetOperationList;
import net.sf.jsqlparser.statement.select.SubJoin;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.statement.select.TableFunction;
import net.sf.jsqlparser.statement.select.ValuesList;
import net.sf.jsqlparser.statement.select.WithItem;


public class TablesNamesFinder implements SelectVisitor, FromItemVisitor,ItemsListVisitor,ExpressionVisitor{

    private List<String> tables;
    
    private String expression;
    private Object param;
    
    public TablesNamesFinder(String expression,Object param){
        this.expression = expression;
        this.param = param;
    }
    
    public List<String> getTableList(Select select) {
        tables = new ArrayList<String>();
        select.getSelectBody().accept(this);
        return tables;
     }
    
    @Override
    public void visit(Table table) {
        String tableWholeName = table.getName();
        
        String tableOrginName = table.getName();
        String index = String.valueOf(MVEL.eval(this.expression, this.param));
        String suffix = StringUtils.leftPad(index, 4, "0");
        table.setName(tableOrginName + AbstractTableRouter.TABLE_NAME_SLIPTOR + suffix);
        
        tables.add(tableWholeName);
    }

    @Override
    public void visit(SubSelect subSelect) {
        subSelect.getSelectBody().accept(this);
    }

    @Override
    public void visit(SubJoin subjoin) {
        subjoin.getLeft().accept(this);
        subjoin.getJoin().getRightItem().accept(this);
    }

    @Override
    public void visit(LateralSubSelect lateralSubSelect) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(ValuesList valuesList) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(TableFunction tableFunction) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(PlainSelect plainSelect) {
        plainSelect.getFromItem().accept(this);
        if (plainSelect.getJoins()!=null) {
            for(Iterator<?> joinsIt = plainSelect.getJoins().iterator();joinsIt.hasNext();){
                Join join = (Join) joinsIt.next();
                join.getRightItem().accept(this);
            }
        }
        
        if (plainSelect.getWhere() != null) {
            plainSelect.getWhere().accept(this);
        }
    }
    
    public void visitBinaryExpression(BinaryExpression binaryExpression) {
        binaryExpression.getLeftExpression().accept(this);
        binaryExpression.getRightExpression().accept(this);
    }

    @Override
    public void visit(SetOperationList setOpList) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(WithItem withItem) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(NullValue nullValue) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(Function function) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(SignedExpression signedExpression) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(JdbcParameter jdbcParameter) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(JdbcNamedParameter jdbcNamedParameter) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(DoubleValue doubleValue) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(LongValue longValue) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(HexValue hexValue) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(DateValue dateValue) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(TimeValue timeValue) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(TimestampValue timestampValue) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(Parenthesis parenthesis) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(StringValue stringValue) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(Addition addition) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(Division division) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(Multiplication multiplication) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(Subtraction subtraction) {
        visitBinaryExpression(subtraction);
    }

    @Override
    public void visit(AndExpression andExpression) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(OrExpression orExpression) {
        visitBinaryExpression(orExpression);
    }

    @Override
    public void visit(Between between) {
        
    }

    @Override
    public void visit(EqualsTo equalsTo) {
        visitBinaryExpression(equalsTo);
    }

    @Override
    public void visit(GreaterThan greaterThan) {
        visitBinaryExpression(greaterThan);
    }

    @Override
    public void visit(GreaterThanEquals greaterThanEquals) {
        visitBinaryExpression(greaterThanEquals);
    }

    @Override
    public void visit(InExpression inExpression) {
        inExpression.getLeftExpression().accept(this);
        inExpression.getLeftItemsList().accept(this);
    }

    @Override
    public void visit(IsNullExpression isNullExpression) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(LikeExpression likeExpression) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(MinorThan minorThan) {
        visitBinaryExpression(minorThan);
    }

    @Override
    public void visit(MinorThanEquals minorThanEquals) {
        visitBinaryExpression(minorThanEquals);
    }

    @Override
    public void visit(NotEqualsTo notEqualsTo) {
        visitBinaryExpression(notEqualsTo);
    }

    @Override
    public void visit(Column tableColumn) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(CaseExpression caseExpression) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(WhenClause whenClause) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(ExistsExpression existsExpression) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(AllComparisonExpression allComparisonExpression) {
        allComparisonExpression.getSubSelect().getSelectBody().accept(this);
    }

    @Override
    public void visit(AnyComparisonExpression anyComparisonExpression) {
        anyComparisonExpression.getSubSelect().getSelectBody().accept(this);
    }

    @Override
    public void visit(Concat concat) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(Matches matches) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(BitwiseAnd bitwiseAnd) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(BitwiseOr bitwiseOr) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(BitwiseXor bitwiseXor) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(CastExpression cast) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(Modulo modulo) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(AnalyticExpression aexpr) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(WithinGroupExpression wgexpr) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(ExtractExpression eexpr) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(IntervalExpression iexpr) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(OracleHierarchicalExpression oexpr) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(RegExpMatchOperator rexpr) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(JsonExpression jsonExpr) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(RegExpMySQLOperator regExpMySQLOperator) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(UserVariable var) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(NumericBind bind) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(KeepExpression aexpr) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(MySQLGroupConcat groupConcat) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(RowConstructor rowConstructor) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(OracleHint hint) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(TimeKeyExpression timeKeyExpression) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(DateTimeLiteralExpression literal) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visit(ExpressionList expressionList) {
        for (Iterator<?> iter = expressionList.getExpressions().iterator();iter.hasNext();) {
            Expression expression = (Expression) iter.next();
            expression.accept(this);
        }
    }

    @Override
    public void visit(MultiExpressionList multiExprList) {
        // TODO Auto-generated method stub
        
    }
    
}