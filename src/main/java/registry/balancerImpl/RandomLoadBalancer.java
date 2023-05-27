package registry.balancerImpl;

import com.alibaba.nacos.api.naming.pojo.Instance;
import registry.LoadBalancer;

import java.util.List;
import java.util.Random;

/**
 * @program: MyDubbo
 * @description:
 * @author: XingJingYe
 * @create: 2022-05-27 12:46
 **/
public class RandomLoadBalancer implements LoadBalancer {
    @Override
    public Instance select(List<Instance> instances) {
        return instances.get(new Random().nextInt(instances.size()));
    }

    @Override
    public int getCode() {
        return 0;
    }
}
