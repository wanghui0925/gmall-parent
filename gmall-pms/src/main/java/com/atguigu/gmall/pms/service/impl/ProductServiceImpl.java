package com.atguigu.gmall.pms.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.pms.entity.Product;
import com.atguigu.gmall.pms.mapper.ProductMapper;
import com.atguigu.gmall.pms.service.ProductService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 商品信息 服务实现类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-19
 */
@Component
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Override
    public Map<String, Object> pageProduct(Integer pageSize, Integer pageNum) {

        ProductMapper baseMapper = getBaseMapper();
        Page<Product> page = new Page<>(pageNum,pageSize);

        //去数据库分页查
        IPage<Product> selectPage = baseMapper.selectPage(page,null);

        //封装数据
        Map<String, Object> map = new HashMap<>();
        map.put("pageSize",pageSize);//当前页条数
        map.put("totalPage",selectPage.getPages());//当前分页总页数
        map.put("total",selectPage.getTotal());//总条数
        map.put("pageNum",selectPage.getCurrent());//当前页
        map.put("list",selectPage.getRecords());//分页记录列表

        return map;
    }
}
