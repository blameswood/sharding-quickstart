package mybatis.sharding.quickstart.wrapper;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import mybatis.sharding.quickstart.context.ReadWriteSplittingDataSourceHolder;

/**
 * @author SongJian email:1738042258@QQ.COM
 * 在这里拦截service层,设置切换读写库
 */
@Aspect
public class SqlSessionTemplateAdvice {

    private final String writeExpression = "execution(* mybatis.sharding.quickstart.service.HelloService.add*(..)))";
    private final String readExpression = "execution(* mybatis.sharding.quickstart.service.HelloService.get*(..)))";
  
    @Pointcut(writeExpression)
    private void writeDao() {}; 
    
    @Pointcut(readExpression) 
    private void readDao() {}; 
    
    /**
     * selectOne
     * selectMap
     * selectList
     * ...
     * @return
     * @throws Throwable 
     */
    @Around("writeDao()") 
    public Object aroundTemplateWrite(ProceedingJoinPoint proceedingJoinPoint) throws Throwable{
        ReadWriteSplittingDataSourceHolder.setWriteDataSource();
        Object processResult = null;
        //需要先定位是哪个DataSourceGroupId的
        //设置为读状态
        processResult = proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs());
        return processResult;
    }
    
    @Around("readDao()") 
    public Object aroundTemplateRead(ProceedingJoinPoint proceedingJoinPoint) throws Throwable{
        ReadWriteSplittingDataSourceHolder.setReadDataSource();
        Object processResult = null;
        //需要先定位是哪个DataSourceGroupId的
        //设置为读状态
        processResult = proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs());
        return processResult;
    }
    
}