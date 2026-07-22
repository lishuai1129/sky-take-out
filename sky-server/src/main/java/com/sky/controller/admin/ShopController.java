package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("adminShopControlller")
@RequestMapping("/admin/shop")
@Api(tags = "店铺管理")
@Slf4j
public class ShopController {

    public static final String KEY = "SHOP_STATUS";

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 设置店铺营业状态
     * @param status 状态
     * @return 返回结果
     */
    @PutMapping("/{status}")
    @ApiOperation("设置店铺营业状态")
    public Result setStatus(@PathVariable Integer status) {
        log.info("修改店铺状态: {}", status==1 ? "营业" : "打洋");
        redisTemplate.opsForValue().set(KEY, status.toString());
        return Result.success();
    }

    /**
     * 获取店铺营业状态
     * @return 返回结果
     */
    @ApiOperation("获取店铺营业状态")
    @GetMapping("/status")
    public Result<Integer> getStatus() {
        log.info("获取店铺状态");
        String value = (String) redisTemplate
                .opsForValue()
                .get(KEY);
        Integer status = Integer.valueOf(value);
        log.info("查询店铺状态：{}", status == 1 ? "营业" : "打烊");
        return Result.success(status);
    }
}
