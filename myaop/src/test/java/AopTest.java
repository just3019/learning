import org.demon.aop.Advice;
import org.demon.aop.Aspect;
import org.demon.aop.IocContainer;
import org.demon.aop.PointCut;
import org.demon.aop.TimeCsAdvice;
import org.demon.entity.User;
import org.demon.service.UserService;
import org.demon.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;

/**
 * @author demon
 * @version 1.0.0
 */
public class AopTest {

    @Test
    public void test() throws Exception {

        // 定义aop的各个节点。
        Advice advice = new TimeCsAdvice();
        PointCut pointCut = new PointCut("org\\.demon\\.service\\.impl..*", ".*");
        Aspect aspect = new Aspect(advice, pointCut);
        //需要将业务代码织入增强逻辑。
        IocContainer ioc = new IocContainer();
        ioc.setAspect(aspect);
        ioc.addBeanDefinition("userService", UserServiceImpl.class);
        UserService userService = (UserService) ioc.getBean("userService");
        System.out.println(userService.getUser1());
        System.out.println(userService.getUser2());
    }
}
