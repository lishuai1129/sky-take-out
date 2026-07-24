package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 购物车业务实现。
 */
@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 添加购物车商品。相同菜品和口味、或者相同套餐会合并数量。
     */
    @Override
    @Transactional
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        //只能传入一个，传两个就会抛异常
        validateGoods(shoppingCartDTO);

        ShoppingCart condition = buildCondition(shoppingCartDTO);
        ShoppingCart existing = shoppingCartMapper.getByUserIdAndGoods(condition);
        if (existing != null) {
            existing.setNumber(existing.getNumber() + 1);
            shoppingCartMapper.updateNumberById(existing);
            return;
        }

        /**
         * 不存在插入一条购物车商品
         */
        ShoppingCart shoppingCart = buildNewShoppingCart(shoppingCartDTO);
        shoppingCartMapper.insert(shoppingCart);
    }

    /**
     * 商品数量大于 1 时减一，等于 1 时删除该购物车记录。
     */
    @Override
    @Transactional
    public void subShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        validateGoods(shoppingCartDTO);

        ShoppingCart condition = buildCondition(shoppingCartDTO);
        ShoppingCart existing = shoppingCartMapper.getByUserIdAndGoods(condition);
        if (existing == null) {
            throw new ShoppingCartBusinessException("购物车中不存在该商品");
        }

        if (existing.getNumber() <= 1) {
            shoppingCartMapper.deleteById(existing.getId());
        } else {
            existing.setNumber(existing.getNumber() - 1);
            shoppingCartMapper.updateNumberById(existing);
        }
    }

    @Override
    public List<ShoppingCart> showShoppingCart() {
        return shoppingCartMapper.listByUserId(getCurrentUserId());
    }

    @Override
    public void cleanShoppingCart() {
        shoppingCartMapper.deleteByUserId(getCurrentUserId());
    }

    /**
     * 构造定位购物车商品的查询条件。
     */
    private ShoppingCart buildCondition(ShoppingCartDTO shoppingCartDTO) {
        return ShoppingCart.builder()
                .userId(getCurrentUserId())
                .dishId(shoppingCartDTO.getDishId())
                .setmealId(shoppingCartDTO.getSetmealId())
                .dishFlavor(shoppingCartDTO.getDishFlavor())
                .build();
    }

    /**
     * 从菜品或套餐中补齐购物车展示信息。
     */
    private ShoppingCart buildNewShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart.ShoppingCartBuilder builder = ShoppingCart.builder()
                .userId(getCurrentUserId())
                .dishId(shoppingCartDTO.getDishId())
                .setmealId(shoppingCartDTO.getSetmealId())
                .dishFlavor(shoppingCartDTO.getDishFlavor())
                .number(1)
                .createTime(LocalDateTime.now());

        if (shoppingCartDTO.getDishId() != null) {
            Dish dish = dishMapper.getById(shoppingCartDTO.getDishId());
            if (dish == null) {
                throw new ShoppingCartBusinessException("菜品不存在");
            }
            builder.name(dish.getName())
                    .amount(dish.getPrice())
                    .image(dish.getImage());
        } else {
            Setmeal setmeal = setmealMapper.getById(shoppingCartDTO.getSetmealId());
            if (setmeal == null) {
                throw new ShoppingCartBusinessException("套餐不存在");
            }
            builder.name(setmeal.getName())
                    .amount(setmeal.getPrice())
                    .image(setmeal.getImage());
        }

        return builder.build();
    }

    /**
     * 每次请求必须且只能传入菜品 id 或套餐 id 中的一个。校验前端传入的购物车商品参数是否合法，防止后面的sql和业务逻辑出错
     */
    private void validateGoods(ShoppingCartDTO shoppingCartDTO) {
        if (shoppingCartDTO == null) {
            throw new ShoppingCartBusinessException("购物车商品参数不能为空");
        }

        boolean hasDish = shoppingCartDTO.getDishId() != null;
        boolean hasSetmeal = shoppingCartDTO.getSetmealId() != null;
        if (hasDish == hasSetmeal) {
            throw new ShoppingCartBusinessException("菜品id和套餐id必须且只能传入一个");
        }
    }

    private Long getCurrentUserId() {
        Long userId = BaseContext.getCurrentId();
        if (userId == null) {
            throw new ShoppingCartBusinessException("用户未登录");
        }
        return userId;
    }
}
