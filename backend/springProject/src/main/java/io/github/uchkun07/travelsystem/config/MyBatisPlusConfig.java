package io.github.uchkun07.travelsystem.config;

import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.annotation.DbType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class MyBatisPlusConfig {

    /**
     * 新的分页插件,一缓和二缓遵循mybatis的规则
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
        paginationInnerInterceptor.setDbType(DbType.MYSQL);
        paginationInnerInterceptor.setOverflow(false); // 改为false,超过最大页不返回数据
        paginationInnerInterceptor.setMaxLimit(65L); // 设置最大单页限制
        interceptor.addInnerInterceptor(paginationInnerInterceptor);
        log.info("MyBatis-Plus分页插件配置完成");
        return interceptor;
    }
}
