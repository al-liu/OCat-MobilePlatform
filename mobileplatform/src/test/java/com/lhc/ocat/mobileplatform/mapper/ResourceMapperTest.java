package com.lhc.ocat.mobileplatform.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lhc.ocat.mobileplatform.domain.dos.ResourceDO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author lhc
 * @date 2019-10-29 16:50
 */

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest
public class ResourceMapperTest {

    private static Logger logger = LoggerFactory.getLogger(ResourceMapperTest.class);

    @Autowired
    private ResourceMapper resourceMapper;

    @Test
    public void testRequestQuery() {
        List<ResourceDO> list = resourceMapper.selectList(new LambdaQueryWrapper<ResourceDO>()
                .orderByDesc(ResourceDO::getId).last("limit 1"));
        Assert.assertEquals(list.size(), 1);
    }

    @Test
    public void testQuery() {
        // 3,4
        List<ResourceDO> list =
                resourceMapper.selectList(new LambdaQueryWrapper<ResourceDO>()
                        .ge(ResourceDO::getVersionCode, 10)
                        .orderByDesc(ResourceDO::getId)
                        .last("limit 2"));
        logger.info("list:"+list);
    }

}
