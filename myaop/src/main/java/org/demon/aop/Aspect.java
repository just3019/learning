package org.demon.aop;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author demon
 * @version 1.0.0
 */
@Data
@AllArgsConstructor
public class Aspect {
    /**
     * 增强逻辑
     */
    private Advice advice;
    /**
     * 切入点
     */
    private PointCut pointCut;
}
