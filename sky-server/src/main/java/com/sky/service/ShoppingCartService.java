package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

/**
 * 购物车业务接口。
 */
public interface ShoppingCartService {

    /**
     * 添加商品到购物车。
     */
    void addShoppingCart(ShoppingCartDTO shoppingCartDTO);

    /**
     * 减少购物车中商品的数量。
     */
    void subShoppingCart(ShoppingCartDTO shoppingCartDTO);

    /**
     * 查看当前用户的购物车。
     */
    List<ShoppingCart> showShoppingCart();

    /**
     * 清空当前用户的购物车。
     */
    void cleanShoppingCart();
}
