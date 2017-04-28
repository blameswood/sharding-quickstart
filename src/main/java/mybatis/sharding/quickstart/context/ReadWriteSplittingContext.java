//package mybatis.sharding.quickstart.context;
//
//import mybatis.sharding.quickstart.datasource.DataSourceMasterSlaveType;
//
///**
// * @author SongJian email:1738042258@QQ.COM
// * 
// */
//public class ReadWriteSplittingContext {
//    
//    public static ThreadLocal<DataSourceMasterSlaveType> currentDataSourceMasterSlaveType = new ThreadLocal<DataSourceMasterSlaveType>();
//    
//    public static void setMaster() {
//        currentDataSourceMasterSlaveType.set(DataSourceMasterSlaveType.master);
//    }
//    
//    public static void setSlave() {
//        currentDataSourceMasterSlaveType.set(DataSourceMasterSlaveType.slave);
//    }
//
//    public static void clear() {
//        currentDataSourceMasterSlaveType.remove();
//    }
//
//    public static boolean isMaster() {
//        return DataSourceMasterSlaveType.master == currentDataSourceMasterSlaveType.get();
//    }
//    
//}
