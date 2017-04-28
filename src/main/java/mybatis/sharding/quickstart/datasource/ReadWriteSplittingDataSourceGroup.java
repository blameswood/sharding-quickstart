package mybatis.sharding.quickstart.datasource;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.datasource.AbstractDataSource;

import lombok.extern.slf4j.Slf4j;
import mybatis.sharding.quickstart.context.ReadWriteSplittingDataSourceHolder;
import mybatis.sharding.quickstart.tx.DoTransactionDataSourceContext;


/**
 * @author SongJian email:1738042258@QQ.COM
 * 设置读写分离组,理论上你可以根据AbstractDataSource无线扩展
 */
@Slf4j
public class ReadWriteSplittingDataSourceGroup extends AbstractDataSource implements DataSource,InitializingBean{
    
    protected String dataSourceGroupId;//1个逻辑数据源1个
    protected DataSource defaultDataSource;
    protected DataSource writeDataSource;
    protected DataSource readDataSource;
    
    @Override
    public Connection getConnection() throws SQLException {
        return this.determineTargetDataSource().getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return this.determineTargetDataSource().getConnection(username, password);
    }
    
    private DataSource determineTargetDataSource(){
        if (this.isInTransaction()) {
            ReadWriteSplittingDataSourceHolder.setWriteDataSource();
            return writeDataSource;
        }
        Boolean selectWriteDataSource = ReadWriteSplittingDataSourceHolder.selectWriteOrReadResult();
        if (selectWriteDataSource) {
            return getWriteDataSource();
        }else if (readDataSource!=null) {
            return getReadDataSource();
        }else{
            log.warn("{} on {} is none !,usr defaultTargetDataSource","slave",getDataSourceGroupId());
        }
        return getDefaultDataSource();
    }
    

    private boolean isInTransaction() {
        return DoTransactionDataSourceContext.getCurTransactionDataSource() != null;
    }

    public DataSource getWriteDataSource() {
        return writeDataSource;
    }

    public void setWriteDataSource(DataSource writeDataSource) {
        this.writeDataSource = writeDataSource;
    }

    public DataSource getReadDataSource() {
        return readDataSource;
    }

    public void setReadDataSource(DataSource readDataSource) {
        this.readDataSource = readDataSource;
    }

    public DataSource getDefaultDataSource() {
        return defaultDataSource;
    }

    public void setDefaultDataSource(DataSource defaultDataSource) {
        this.defaultDataSource = defaultDataSource;
    }

    public String getDataSourceGroupId() {
        return dataSourceGroupId;
    }

    public void setDataSourceGroupId(String dataSourceGroupId) {
        this.dataSourceGroupId = dataSourceGroupId;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (getDefaultDataSource()==null && getWriteDataSource()!=null) {
            setDefaultDataSource(getWriteDataSource());
        }
    }
}
