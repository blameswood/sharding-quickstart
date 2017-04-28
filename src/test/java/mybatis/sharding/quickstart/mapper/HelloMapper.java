package mybatis.sharding.quickstart.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import mybatis.sharding.quickstart.annotation.ShardMethod;
import mybatis.sharding.quickstart.model.Hello;

public interface HelloMapper {
    
    @ShardMethod(expression="id % 32",shardProperty="id")
    public Hello getHelloByHelloId(@Param("id")Long id);
    
    @ShardMethod(expression="id % 32",shardProperty="id")
    public Hello getHelloByHelloId2(@Param("id")Long id,String name,List<Long> ids);

    public List<Hello> getHelloByHelloIds(List<Long> helloIds);
    
    @ShardMethod(expression="id % 32",simple=false,shardProperty="id")
    public int addHello(Hello hello);
    
}
