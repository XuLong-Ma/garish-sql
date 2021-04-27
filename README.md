## garish-sql

### 底层数据源操作封装了SpringJdbc

| 注解 | 描述 |
| --- | --- |
| AutoRowMapper | 标识为需要被编译时织入代码(列映射注解和Insert方法的植入) `value`属性值为true时 未忽略和未映射的字段会默认按照驼峰转下划线做映射 |
| EnableGarishSQL | SpringBoot注入默认对象 |
| Entity | 映射表字段名称 |
| Cell | 映射表字段名称 |
| Ignore | 标识在类上时需要指明属性名称字符串标识忽略不做映射，标识在属性字段上时不需要填写内容，字段默认标识为忽略 |
| Id | 标识为主键 和 `GeneratedValue` 一起使用时才有意义 |
| GeneratedValue | 自增列 此属性不会算入 `$insertSQL` 和 `$objects` 方法中|

<hr>

### 数据映射自定义类 `org.dregs.garish.sql.mapper.OrdainRowMapper`

<hr>

### 默认注入Bean 在 `org.dregs.garish.sql.config.InjectBeans`

<hr>

```java

import org.dregs.garish.sql.action.Delete;
import org.dregs.garish.sql.action.Query;
import org.dregs.garish.sql.action.Update;
import org.dregs.garish.sql.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.dregs.garish.sql.dao.trait.IndexDao;
import java.util.Date;

@Service
public class IndexServiceImpl implements IndexService{
    
    @Autowired
    private IndexDao indexDao;
    
    public boolean insert(){
        Data data = new Data();
        return 1 == indexDao.insert(data);
    }
    public boolean update(Long id,String newName){
        return Update
            .createUpdate(Data.class)
            .set(Data::getName,newName)
            .set(Data::getUpdateAt,new Date())
            .eq(Data::getId,id)
            .affectResult(indexDao);
    }
    public boolean delete(Long id){
        Delete delete = Delete
                .createDelete(Data.class)
                .eqIndex(Data::getId,id);
        return 1 == indexDao.delete(delete);
    }
    public List<Data> query(Date st,Date et){
        return Query
            .createQuery(Data.class)
            .ge(Data::getCreateAt,st)
            .le(Data::getCreateAt,et)
            .findList(indexDao);
    }
    
    @AutoRowMapper(true)
    @Entity(name = "data")
    @Ignore({"createTime"})
    public static class Data{
        @Id
        @GeneratedValue
        private Long id;
        private String name;
        private Date createAt;
        @Cell(name = "update_at")
        private Date updateAt;
        
        private Long createTime;
        @Ignore
        private Long updateTime;
        
        
        public Long getId(){
            return this.id;
        }
        public String getName(){
            return this.name;
        }
        public Date getCreateAt(){
            return this.createAt;
        }
        public Date getUpdateAt(){
            return this.updateAt;
        }
        public Long getCreateTime(){
            return this.createTime;
        }
        public Long getUpdateTime(){
            return this.updateTime;
        }
        public void setId(Long id){
            return this.id = id;
        }
        public void setName(String name){
            return this.name = name;
        }
        public void setCreateAt(Date createAt){
            return this.createAt = createAt;
        }
        public void setUpdateAt(Date updateAt){
            return this.updateAt = updateAt;
        }
        public void setCreateTime(Long createTime){
            return this.createTime = createTime;
        }
        public void setUpdateTime(Long updateTime){
            return this.updateTime = updateTime;
        }
    }
}
 
```

<hr>

