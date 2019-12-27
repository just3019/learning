package org.demon.ioc;

/**
 * @author demon
 * @version 1.0.0
 */
public interface BeanFactory {
    /**
     * 因为是Bean工厂，需要对外提供获取Bean方法。
     * 因为Bean是类型不定，所以使用Object作为返回值类型
     */
    Object getBean(String name) throws Exception;
}
