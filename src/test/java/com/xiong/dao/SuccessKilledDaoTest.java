package com.xiong.dao;

import com.xiong.entity.SuccessKilled;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import javax.annotation.Resource;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccessKilledDaoTest {

    @Resource
    private SuccessKilledDao successKilledDao;

    @Test
    public void insertSuccessKilled() throws Exception {
        long seckillId = 1000;
        long userPhone = 13429876543L;
        int successKilled = successKilledDao.insertSuccessKilled(seckillId, userPhone);
        System.out.println(successKilled);
    }

    @Test
    public void queryByIdWithSeckill() throws Exception {
        long seckillId = 1000;
        long userPhone = 13429876543L;
        SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
        System.out.println("secKill:" + successKilled.getSeckill());
        System.out.println(successKilled);
    }
}
