package mybatis.sharding.quickstart.tx;

import javax.sql.DataSource;

/**
 * @author SongJian email:1738042258@QQ.COM
 * 设置当前线程执行点的数据库
 */
public class DoTransactionDataSourceContext {
    
    private static final ThreadLocal<DataSource> curDataSource = new ThreadLocal<DataSource>();

    public static DataSource getCurTransactionDataSource() {
        return curDataSource.get();
    }

    public static void setCurDataSource(DataSource dataSource) {
        curDataSource.set(dataSource);
    }

    public static void clear() {
        curDataSource.remove();
    }

}
