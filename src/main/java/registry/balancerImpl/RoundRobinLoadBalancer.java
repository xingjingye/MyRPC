package registry.balancerImpl;

import com.alibaba.nacos.api.naming.pojo.Instance;
import registry.LoadBalancer;

import java.util.List;

/**
 * @program: MyDubbo
 * @description:
 * @author: XingJingYe
 * @create: 2022-05-27 12:51
 **/
public class RoundRobinLoadBalancer implements LoadBalancer {

    private int index = 0;

    @Override
    public Instance select(List<Instance> instances) {
        if (index >= instances.size()) {
            index = index % instances.size();
        }
        return instances.get(index++);
    }

    @Override
    public int getCode() {
        return 1;
    }
}
