package mybatis.sharding.quickstart;

import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import mybatis.sharding.quickstart.model.Hello;
import mybatis.sharding.quickstart.service.HelloService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
  "classpath:applicationContext-simple.xml"
})
public class TestClient {

    @Resource(name="helloService")
    private HelloService helloService;
    
    private int count = 10;
    
    @Test
    public void testSave() {
        AtomicLong id = new AtomicLong(1);
        for(int i=0;i<count;i++){
            Hello hello = new Hello();
            hello.setId(id.incrementAndGet());
            hello.setName("name"+i);
            hello.setAge(12);
            helloService.addHello(hello);
        }
    }
    
    @Test
    public void testSelect(){
      AtomicLong id = new AtomicLong(1);
      for(int i=0;i<count;i++){
          Hello hello1=  helloService.getHelloByHelloId(id.incrementAndGet());
          System.out.println(hello1);
      }
    }
    
}