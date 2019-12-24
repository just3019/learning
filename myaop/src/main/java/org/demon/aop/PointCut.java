package org.demon.aop;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author demon
 * @version 1.0.0
 */
@Data
@AllArgsConstructor
public class PointCut {
    //切入点的颗粒度需要到某个类下面的某个方法
    /**
     * 类名
     */
    private String className;
    /**
     * 方法名
     */
    private String methorName;

}
