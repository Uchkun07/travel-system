package io.github.uchkun07.travelsystem.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "registerTime", Date.class, new Date());
        this.strictInsertFill(metaObject, "lastLoginTime", Date.class, new Date());
        this.strictInsertFill(metaObject, "createTime", Date.class, new Date());
        this.strictInsertFill(metaObject, "updateTime", Date.class, new Date()); 
        this.strictInsertFill(metaObject, "registerTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "lastLoginTime",LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now()); 
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "lastLoginTime", Date.class, new Date());
        this.strictUpdateFill(metaObject, "updateTime", Date.class, new Date());
    }
}