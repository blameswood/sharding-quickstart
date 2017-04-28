package mybatis.sharding.quickstart.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * @author SongJian email:1738042258@QQ.COM
 * 在mapper接口中的方法标注了这个注解 就表示这是个shard方法,框架会自动处理
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ShardMethod {
    
    boolean simple() default true;
    
    String expression();
    
    String shardProperty() default "";
    
}
