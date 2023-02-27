package com.prgms.devcourse.configures;

import net.sf.log4jdbc.Log4jdbcProxyDataSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * BeanPostProcessor: Bean이 초기화 되기 전 & 후 Bean을 직접 조작할 수 있도록 한다.
 */
@Component
public class DataSourcePostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        //매개변수로 받은 bean이 Datasource인지 확인   , 즉, 순수 Data라면
        if (bean instanceof DataSource && !(bean instanceof Log4jdbcProxyDataSource)) {
            return new Log4jdbcProxyDataSource((DataSource) bean);
        } else {
            return bean;
        }

    }
}
