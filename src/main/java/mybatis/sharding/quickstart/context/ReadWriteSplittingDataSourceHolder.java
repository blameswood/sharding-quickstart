package mybatis.sharding.quickstart.context;

/**
 * @author SongJian email:1738042258@QQ.COM
 * 当前线程执行点的读写分离设置 
 */
public class ReadWriteSplittingDataSourceHolder {
    
    private static final ThreadLocal<Boolean> readWriteThreadControl = new ThreadLocal<Boolean>(){
        @Override
        protected Boolean initialValue() {
            return true;
        }
    };
    
    public static Boolean selectWriteOrReadResult(){
        return readWriteThreadControl.get();
    }

    
    public static void setReadDataSource(){
        readWriteThreadControl.set(false);
    }
    
    public static void setWriteDataSource(){
        readWriteThreadControl.set(true);
    }
    
}
