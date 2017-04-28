package mybatis.sharding.quickstart.wrapper;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.util.StopWatch;

@Aspect
public class PerformaceMonitor {
    
    @Pointcut("execution(* mybatis.sharding.client..**.*(..))")
    public void performance(){}

    
    @Around("performance()")
    public Object watchPerformance(ProceedingJoinPoint point){
        System.out.println("The service start:");
        StopWatch stopWatch = new StopWatch("performance");
        stopWatch.start(point.getSignature().toString());
        try {
            return point.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }finally {
            stopWatch.stop();
            StopWatch.TaskInfo[] taskInfo = stopWatch.getTaskInfo();
            for (StopWatch.TaskInfo info : taskInfo) {
                System.out.println(info.getTaskName());
                System.out.println(info.getTimeMillis());
            }
            System.out.println("The "+point.getSignature().toString()+" run time:"+stopWatch.prettyPrint());
        }

        return null;
    }

}
