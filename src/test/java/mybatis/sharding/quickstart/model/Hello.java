package mybatis.sharding.quickstart.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class Hello implements Serializable {


  private static final long serialVersionUID = -5020922952830369407L;

  private Long id;
  private String name;
  private int age;



}
