package mybatis.sharding.quickstart.tx;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;

/**
 * @author SongJian email:1738042258@QQ.COM
 * 分库后,当前库的读写控制中介
 */
@SuppressWarnings("serial")
public class ShardingDataSourceTransactionManager extends DataSourceTransactionManager{

    private String txManagerName;
    
    public ShardingDataSourceTransactionManager() {
    }

    public ShardingDataSourceTransactionManager(DataSource dataSource) {
        super(dataSource);
    }

    public String getTxManagerName() {
        return txManagerName;
    }

    public void setTxManagerName(String txManagerName) {
        this.txManagerName = txManagerName;
    }
    
    @Override
    protected void doBegin(Object transaction, TransactionDefinition definition) {
        DoTransactionDataSourceContext.setCurDataSource(getDataSource());
        super.doBegin(transaction, definition);
    }
 
    @Override
    protected void doCleanupAfterCompletion(Object transaction) {
        super.doCleanupAfterCompletion(transaction);
        DoTransactionDataSourceContext.clear();
    }
    
}
