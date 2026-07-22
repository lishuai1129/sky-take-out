package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;


public interface DishService {

    /**
     * 新增菜品
     */
    void saveWithFlavor(DishDTO dishDTO);

    /**
     * 菜品管理分页查询
     * @param dishPageQueryDTO 菜品分页查询条件
     * @return 返回结果
     */
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     *
     * @param ids id集合
     */
    void deleteBatch(List<Long> ids);

    /**
     * 根据id查询菜品信息和口味信息
     * @param id 主键id
     * @return 返回结果
     */
    DishVO getByIdWithFlavor(long id);

    /**
     * 修改菜品
     * @param dishDTO 菜品信息
     */
    void updatWithFlavor(DishDTO dishDTO);

    /**
     * 条件查询菜品和口味
     * @param dish 菜品查询条件
     * @return 返回结果
     */
    List<DishVO> listWithFlavor(Dish dish);

    /**
     * 根据分类id查询起售中的菜品。
     *
     * @param categoryId 分类id
     * @return 起售中的菜品列表
     */
    List<Dish> list(Long categoryId);
}
