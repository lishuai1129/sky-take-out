package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/admin/dish")
@Api( tags = "菜品管理接口")
@Slf4j
public class DishController {

    private static final String DISH_CACHE_PREFIX = "dish_";

    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping
    @ApiOperation("新增菜品")
    public Result save(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品：{}", dishDTO);
        dishService.saveWithFlavor(dishDTO);
        cleanCache(DISH_CACHE_PREFIX + dishDTO.getCategoryId());
        return Result.success();

    }

    /**
     * 菜品管理分页查询
     * @param dishPageQueryDTO 菜品分页查询条件
     * @return 返回结果
     */
    @GetMapping("page")
    @ApiOperation("分页查询菜品")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        log.info("菜品管理分页查询：{}", dishPageQueryDTO);
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    @DeleteMapping
    @ApiOperation("删除菜品")
    public Result delete(@RequestParam List<Long> ids){
        log.info("删除菜品：{}", ids);
        dishService.deleteBatch(ids);
        //有可能影响多个key，直接全部清楚缓存
        cleanCache(DISH_CACHE_PREFIX + "*");
        return Result.success();
    }

    /**
     * 根据id查询菜品
     * @param id 主键id
     * @return 返回结果
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品")
    public Result<DishVO> getById(@PathVariable long id){
        DishVO dishVO = dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }

    /**
     * 修改菜品
     */
    @PutMapping
    @ApiOperation("修改菜品")
    public Result updata(@RequestBody DishDTO dishDTO){
        dishService.updatWithFlavor(dishDTO);
        // 菜品修改时可能更换分类，因此清理全部菜品分类缓存。
        cleanCache(DISH_CACHE_PREFIX + "*");
        return Result.success();
    }

    /**
     * 设置菜品的起售或停售状态。
     *
     * @param status 菜品状态，1为起售，0为停售
     * @param id 菜品id
     * @return 操作结果
     */
    @PostMapping("/status/{status}")
    @ApiOperation("菜品起售停售")
    public Result<String> startOrStop(@PathVariable Integer status,
                                      @RequestParam Long id) {
        log.info("修改菜品状态：status={}，id={}", status, id);
        dishService.startOrStop(status, id);

        // 菜品状态发生变化后，清理所有菜品分类缓存。
        cleanCache(DISH_CACHE_PREFIX + "*");
        return Result.success();
    }

    /**
     * 根据分类id查询起售中的菜品，供新增或修改套餐时选择。
     *
     * @param categoryId 分类id
     * @return 起售中的菜品列表
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<Dish>> list(Long categoryId) {
        List<Dish> list = dishService.list(categoryId);
        return Result.success(list);
    }

    /**
     * 根据匹配模式清理菜品缓存。
     *
     * @param pattern Redis key匹配模式
     */
    private void cleanCache(String pattern) {
        Set keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}
