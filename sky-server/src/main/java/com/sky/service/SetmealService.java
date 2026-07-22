package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import java.util.List;

public interface SetmealService {

    /**
     * 新增套餐，同时保存套餐和菜品的关联关系。
     *
     * @param setmealDTO 套餐信息及套餐包含的菜品
     */
    void saveWithDish(SetmealDTO setmealDTO);

    /**
     * 套餐分页查询。
     *
     * @param setmealPageQueryDTO 分页及查询条件
     * @return 套餐分页数据
     */
    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 批量删除套餐及其菜品关联关系。
     *
     * @param ids 套餐id集合
     */
    void deleteBatch(List<Long> ids);

    /**
     * 根据id查询套餐及其菜品关联关系。
     *
     * @param id 套餐id
     * @return 套餐详情
     */
    SetmealVO getByIdWithDish(Long id);

    /**
     * 修改套餐及其菜品关联关系。
     *
     * @param setmealDTO 套餐信息及套餐包含的菜品
     */
    void update(SetmealDTO setmealDTO);

    /**
     * 套餐起售、停售。
     *
     * @param status 套餐状态，1为起售，0为停售
     * @param id 套餐id
     */
    void startOrStop(Integer status, Long id);

    /**
     * 条件查询
     * @param setmeal 套餐查询条件
     * @return 返回结果
     */
    List<Setmeal> list(Setmeal setmeal);

    /**
     * 根据id查询菜品选项
     * @param id 主键id
     * @return 返回结果
     */
    List<DishItemVO> getDishItemById(Long id);

}
