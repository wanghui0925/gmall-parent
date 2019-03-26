package com.atguigu.gmall.pms.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.constant.RedisCacheConstant;
import com.atguigu.gmall.pms.entity.Product;
import com.atguigu.gmall.pms.entity.ProductAttribute;
import com.atguigu.gmall.pms.entity.ProductCategory;
import com.atguigu.gmall.pms.mapper.ProductCategoryMapper;
import com.atguigu.gmall.pms.service.ProductCategoryService;
import com.atguigu.gmall.to.PmsProductCategoryWithChildrenItem;
import com.atguigu.gmall.utils.PageUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 产品分类 服务实现类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
@Slf4j
@Component
@Service
public class ProductCategoryServiceImpl extends ServiceImpl<ProductCategoryMapper, ProductCategory> implements ProductCategoryService {

    //注入redis
    @Autowired
    StringRedisTemplate redisTemplate;
    @Override
    public List<PmsProductCategoryWithChildrenItem> listWithChildren() {
        //注入redis
        ValueOperations<String, String> ops = redisTemplate.opsForValue();

        //从缓存中拿出值
        String cache = ops.get(RedisCacheConstant.PRODUCT_CATEGORY_CACHE_KEY);
        //判断是否为空
        if(!StringUtils.isEmpty(cache)){
            log.debug("PRODUCT_CATEGORY_CACHE 缓存命中....");
            //转化过来返回出去  对json数据进行解析
            List<PmsProductCategoryWithChildrenItem> items = JSON.parseArray(cache, PmsProductCategoryWithChildrenItem.class);
            return items;
        }

        log.debug("PRODUCT_CATEGORY_CACHE 缓存未命中，去查询数据库");

        ProductCategoryMapper baseMapper = getBaseMapper();
        List<PmsProductCategoryWithChildrenItem> items = baseMapper.listWithChildren(0);

        //将查到的数据储存到redis缓存中
        //存数据都给一个过期时间比较好； 转换成json字符串
        String jsonString = JSON.toJSONString(items);
        //设置redis过期时间   TimeUnit ： 时间单位统计
        ops.set(RedisCacheConstant.PRODUCT_CATEGORY_CACHE_KEY,jsonString,3, TimeUnit.DAYS);
        //查某个菜单的所有子菜单
        //TODO 这个数据加缓存，
        return items;
    }

    @Override
    public Map<String, Object> PageProductCategoryById(Long parentId, Integer pageSize, Integer pageNum) {
        ProductCategoryMapper baseMapper = getBaseMapper();

        IPage<ProductCategory> page = baseMapper.selectPage(new Page<ProductCategory>(pageNum, pageSize)
                ,new QueryWrapper<ProductCategory>().eq("parent_id", parentId));


        return PageUtils.getPageMap(page);
    }


}
