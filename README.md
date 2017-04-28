# sharding-quickstart
快速使Mybatis支持分库表读写分离

之前学习过t-Sharding 感觉有的地方封装的太繁琐了,
也学习过一个JD的大神分库表的封装 看似NB哄哄,,其实 通过spring配置本身就可以把自己的ORM层支持的很灵活了
所以写了这个东西

QQ群 616109824
有问题可以在这边问 或者 issue留言

测试入口
mybatis.sharding.quickstart.TestClient

如果你不需要

```xml
<dependency>
	<groupId>org.springframework.data</groupId>
	<artifactId>spring-data-commons</artifactId>
	<version>2.0.0.M1</version>
</dependency>
```

可以把JDK降低到1.7