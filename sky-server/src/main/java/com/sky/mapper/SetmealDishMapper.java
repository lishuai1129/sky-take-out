package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    /**
     * 批量保存套餐和菜品的关联关系。
     *
     * @param setmealDishes 套餐包含的菜品列表
     */
    void insertBatch(@Param("setmealDishes") List<SetmealDish> setmealDishes);

    /**
     * 根据套餐id删除套餐和菜品的关联关系。
     *
     * @param setmealId 套餐id
     */
    @Delete("delete from setmeal_dish where setmeal_id = #{setmealId}")
    void deleteBySetmealId(Long setmealId);

    /**
     * 根据套餐id查询包含的菜品列表。
     *
     * @param setmealId 套餐id
     * @return 套餐包含的菜品列表
     */
    @Select("select * from setmeal_dish where setmeal_id = #{setmealId}")
    List<SetmealDish> getBySetmealId(Long setmealId);

    /**
     * 根据菜品id查询对应的套餐id
     * @param dishIds 菜品id集合
     * @return 返回结果
     */
    //select setmeal_id from setmeal_dish where dish_id in (1,2,3)
    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);

    /**
     * 根据菜品id产出与之关联的口味
     * @param dishId 菜品id
     */
    @Delete("delete from setmeal_dish where dish_id = #{dishId}")
    void deleleByDishId(Long dishId);

    /**
     * 优化批量
     * @param dishIds 菜品id集合
     */
    void deleleByDishIds(List<Long> dishIds);
}
