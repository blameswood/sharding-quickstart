package mybatis.sharding.quickstart.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import mybatis.sharding.quickstart.mapper.HelloMapper;
import mybatis.sharding.quickstart.model.Hello;

@Service(value="helloService")
public class HelloService {
    
    @Resource
    private HelloMapper helloMapper;
    
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int addHello(Hello hello){
        /**
         * ackManager.注册一个start-tx-id
         * 
         */
        
        return helloMapper.addHello(hello);
    }
    
    
    public Hello getHelloByHelloId(Long id){
        return helloMapper.getHelloByHelloId(id);
    }
    
}
