package com.xiong.service.impl;

import com.xiong.dao.SeckillDao;
import com.xiong.dao.SuccessKilledDao;
import com.xiong.dto.Exposer;
import com.xiong.dto.SeckillExecution;
import com.xiong.entity.Seckill;
import com.xiong.entity.SuccessKilled;
import com.xiong.enums.SeckillStatEnum;
import com.xiong.exception.RepeatKillException;
import com.xiong.exception.SeckillCloseException;
import com.xiong.exception.SeckillException;
import com.xiong.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.List;

public class SeckillServiceImpl implements SeckillService {

    //日志对象
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    //加入一个混淆字符串(秒杀接口)的salt，为了我避免用户猜出我们的md5值，值任意给，越复杂越好
    private final String salt="shsdssljdd'l.";

    //注入Service依赖
    @Autowired
    private SeckillDao seckillDao;

    @Autowired
    private SuccessKilledDao successKilledDao;

    @Override
    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0, 4);
    }

    @Override
    public Seckill getById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    @Override
    public Exposer exportSeckillUrl(long seckillId) {
        Seckill seckill = seckillDao.queryById(seckillId);
        if (seckill == null) {
            return new Exposer(false, seckillId);
        }

        //若是秒杀未开启
        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        //系统当前时间
        Date nowTime = new Date();

        if (startTime.getTime() > nowTime.getTime() || endTime.getTime() < nowTime.getTime()) {
            return new Exposer(false, seckillId, nowTime.getTime(), startTime.getTime(), endTime.getTime());
        }

        //秒杀开启,返回秒杀商品的id，用给接口加密的md5
        String md5 = getMd5(seckillId);
        return new Exposer(true, md5, seckillId);
    }

    private String getMd5(long seckillId) {
        String base = seckillId + "/" + salt;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }

    //秒杀是否成功， 成功：减库存，增加明细；失败：抛出异常，事务回滚
    @Override
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws SeckillException, RepeatKillException, SeckillCloseException {
        if (md5==null || !md5.equals(getMd5(seckillId))) {
            throw new SeckillException("seckill data rewrite");
        }

        //执行秒杀逻辑，减库存+增加购买明细
        Date nowTime = new Date();

        try {
            int reduceNumber = seckillDao.reduceNumber(seckillId, nowTime);
            if (reduceNumber <= 0) {
                //没有更新库存记录，说明秒杀结束
                throw new SeckillCloseException("seckill is closed");
            } else {
                //否则更新库存，秒杀成功，增加明细
                int insetrCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
                //看是否该明细被重复插入，即用户重复秒杀
                if (insetrCount <= 0) {
                    throw new RepeatKillException("seckill repeated");
                } else {
                    //秒杀成功，得到成功插入的明细记录，并返回成功秒杀的信息
                    SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
                    return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS, successKilled);
                }
            }

        } catch (SeckillCloseException closeException) {
            throw closeException;
        } catch (RepeatKillException repeatException) {
            throw repeatException;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            //所以编译期异常转化为运行期异常
            throw new SeckillException("seckill inner error:" + e.getMessage());
        }
    }
}
