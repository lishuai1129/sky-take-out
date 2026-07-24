package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 购物车数据访问层。
 */
@Mapper
public interface ShoppingCartMapper {

    /**
     * 查询当前用户购物车中的指定商品。
     * 菜品使用菜品 id 和口味定位，套餐使用套餐 id 定位。
     */
    ShoppingCart getByUserIdAndGoods(ShoppingCart shoppingCart);

    /**
     * 新增购物车商品。
     */
    void insert(ShoppingCart shoppingCart);

    /**
     * 根据主键更新商品数量。
     */
    @Update("update shopping_cart set number = #{number} where id = #{id}")
    void updateNumberById(ShoppingCart shoppingCart);

    /**
     * 查询指定用户的购物车。
     */
    @Select("select * from shopping_cart where user_id = #{userId} order by create_time desc, id desc")
    List<ShoppingCart> listByUserId(Long userId);

    /**
     * 根据主键删除一条购物车记录。
     */
    @Delete("delete from shopping_cart where id = #{id}")
    void deleteById(Long id);

    /**
     * 清空指定用户的购物车。
     */
    @Delete("delete from shopping_cart where user_id = #{userId}")
    void deleteByUserId(Long userId);
}
