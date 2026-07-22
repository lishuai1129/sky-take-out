package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId 分类id
     * @return 返回结果
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    /**
     * 插入一条菜品
     */
    @AutoFill(OperationType.INSERT)
    void insert(Dish dish);

    /**
     * 分页查询
     * @param dishPageQueryDTO 菜品分页查询条件
     * @return 返回结果
     */
    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 根据id查询菜品
     * @param id 主键id
     * @return 返回结果
     */
    @Select("select * from dish where id = #{id}")
    Dish getById(Long id);

    /**
     * 根据id删除菜品
     * @param id 单个
     */
    @Delete("delete from dish where id in (#{id})")
    void deleteById(Long id);

    /**
     * 根据ids批量删除
     * @param ids 优化批量
     */
    void deleteByIds(List<Long> ids);

    @AutoFill(OperationType.UPDATE)
    void update(Dish dish);

    /**
     * 动态条件查询菜品。
     *
     * @param dish 菜品查询条件
     * @return 菜品列表
     */
    List<Dish> list(Dish dish);

    /**
     * 根据套餐id查询套餐包含的菜品。
     *
     * @param setmealId 套餐id
     * @return 套餐包含的菜品列表
     */
    @Select("select d.* from dish d left join setmeal_dish sd on d.id = sd.dish_id " +
            "where sd.setmeal_id = #{setmealId}")
    List<Dish> getBySetmealId(Long setmealId);
}
