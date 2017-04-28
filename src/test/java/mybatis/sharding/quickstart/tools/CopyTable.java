package mybatis.sharding.quickstart.tools;

import org.apache.commons.lang3.StringUtils;

public class CopyTable {
    
    public static void main(String[] args) {
        
        for(int i=0;i<=32;i++){
            System.out.println("create table hello_"+StringUtils.leftPad(i+"", 4, "0")+" like hello;");
        }
        
    }

}
