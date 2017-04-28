package mybatis.sharding.quickstart.util;

import java.util.List;

import org.springframework.beans.factory.InitializingBean;

import com.google.common.base.Splitter;

import mybatis.sharding.quickstart.annotation.ShardMethod;


public class ShardMethodFinder implements InitializingBean {

    private String packages;
    
    @Override
    public void afterPropertiesSet() throws Exception {
        List<String> packageCollection = Splitter.onPattern("[,;]").splitToList(getPackages());
        for(String packageName : packageCollection){
            PackageUtil.findPackageInnerShardMethodsAndRegist(packageName,ShardMethod.class);
        }
    }

    public String getPackages() {
        return packages;
    }

    public void setPackages(String packages) {
        this.packages = packages;
    }
    
}
