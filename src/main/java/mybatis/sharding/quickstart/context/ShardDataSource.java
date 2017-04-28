package mybatis.sharding.quickstart.context;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class ShardDataSource extends AbstractRoutingDataSource {

    /**
     * 返回定位得到的数据库组的索引
     */
    @Override
    protected Object determineCurrentLookupKey() {
        return "group1";
    }

}
