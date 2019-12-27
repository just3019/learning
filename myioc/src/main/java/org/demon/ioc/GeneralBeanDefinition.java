package org.demon.ioc;

import lombok.Data;
import org.springframework.util.StringUtils;

import java.lang.reflect.Constructor;
import java.util.List;

/**
 * @author demon
 * @version 1.0.0
 */
@Data
public class GeneralBeanDefinition implements BeanDefinition {

    private Class<?> beanClass;
    private String scope = SINGLETON;
    private String factoryBeanName;
    private String factoryMethodName;
    private List<?> constructorArgumentValues;
    private Constructor<?> constructor;

    public void SetScope(String scope) {
        if (!StringUtils.isEmpty(scope)) {
            this.scope = scope;
        }
    }

}
