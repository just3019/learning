package org.demon.ioc;

import lombok.Data;
import org.springframework.util.StringUtils;

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

    public void SetScope(String scope) {
        if (!StringUtils.isEmpty(scope)) {
            this.scope = scope;
        }
    }

    @Override
    public String getScope() {
        return this.scope;
    }

    @Override
    public Class<?> getBeanClass() {
        return this.beanClass;
    }

    @Override
    public String getFactoryBeanName() {
        return this.factoryBeanName;
    }

    @Override
    public String getFactoryMethodName() {
        return this.factoryMethodName;
    }
}
