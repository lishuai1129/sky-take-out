package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 套餐业务实现。
 */
@Service
@Slf4j
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Autowired
    private DishMapper dishMapper;

    /**
     * 新增套餐，同时保存套餐和菜品的关联关系。
     *
     * @param setmealDTO 套餐信息及套餐包含的菜品
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        if (setmeal.getStatus() == null) {
            setmeal.setStatus(StatusConstant.DISABLE);
        }

        setmealMapper.insert(setmeal);
        saveSetmealDishes(setmeal.getId(), setmealDTO.getSetmealDishes());
    }

    /**
     * 套餐分页查询。
     *
     * @param setmealPageQueryDTO 分页及查询条件
     * @return 套餐分页数据
     */
    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(
                setmealPageQueryDTO.getPage(),
                setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> page = setmealMapper.pageQuery(setmealPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 批量删除套餐及其菜品关联关系。
     *
     * @param ids 套餐id集合
     */
    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        for (Long id : ids) {
            Setmeal setmeal = setmealMapper.getById(id);
            if (setmeal != null && setmeal.getStatus() == StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        }

        for (Long id : ids) {
            setmealMapper.deleteById(id);
            setmealDishMapper.deleteBySetmealId(id);
        }
    }

    /**
     * 根据套餐id查询套餐及其包含的菜品。
     *
     * @param id 套餐id
     * @return 套餐详情
     */
    @Override
    public SetmealVO getByIdWithDish(Long id) {
        Setmeal setmeal = setmealMapper.getById(id);
        if (setmeal == null) {
            return null;
        }

        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal, setmealVO);
        setmealVO.setSetmealDishes(setmealDishMapper.getBySetmealId(id));
        return setmealVO;
    }

    /**
     * 修改套餐及其菜品关联关系。
     *
     * @param setmealDTO 套餐信息及套餐包含的菜品
     */
    @Override
    @Transactional
    public void update(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.update(setmeal);

        Long setmealId = setmealDTO.getId();
        setmealDishMapper.deleteBySetmealId(setmealId);
        saveSetmealDishes(setmealId, setmealDTO.getSetmealDishes());
    }

    /**
     * 设置套餐的起售或停售状态。
     *
     * @param status 套餐状态，1为起售，0为停售
     * @param id 套餐id
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        if (status != null && status == StatusConstant.ENABLE) {
            List<Dish> dishes = dishMapper.getBySetmealId(id);
            if (dishes != null) {
                for (Dish dish : dishes) {
                    if (dish.getStatus() == StatusConstant.DISABLE) {
                        throw new SetmealEnableFailedException(
                                MessageConstant.SETMEAL_ENABLE_FAILED);
                    }
                }
            }
        }

        Setmeal setmeal = Setmeal.builder()
                .id(id)
                .status(status)
                .build();
        setmealMapper.update(setmeal);
    }

    @Override
    public List<Setmeal> list(Setmeal setmeal) {
        return setmealMapper.list(setmeal);
    }

    @Override
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishItemBySetmealId(id);
    }

    /**
     * 批量保存套餐和菜品的关联关系。
     *
     * @param setmealId 套餐id
     * @param setmealDishes 套餐包含的菜品列表
     */
    private void saveSetmealDishes(Long setmealId, List<SetmealDish> setmealDishes) {
        if (setmealDishes == null || setmealDishes.isEmpty()) {
            return;
        }

        setmealDishes.forEach(setmealDish -> setmealDish.setSetmealId(setmealId));
        setmealDishMapper.insertBatch(setmealDishes);
    }
}
