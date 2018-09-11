package com.xiong.dao;

import com.xiong.entity.Seckill;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface SeckillDao {

    /**
     * 减库存
     * @param seckillId 秒杀商品Id
     * @param killTime 秒杀时间
     * @return 如果影响行数>1,表示更新库存的记录行数
     */
    int reduceNumber(@Param("seckillId") long seckillId, @Param("killTime") Date killTime);

    /**
     * 根据id查询秒杀的商品信息
     * @param seckillId 商品id
     * @return 根据id返回查询到的商品信息
     */
    Seckill queryById(long seckillId);

    /**
     * 根据偏移量查询秒杀商品列表
     * @param offset 开始
     * @param limit 结束
     * @return 返回商品列表
     */
    List<Seckill> queryAll(@Param("offset") int offset, @Param("limit") int limit);
}
