package com.hamifs.mapper;

import com.hamifs.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {

    /** 根据用户名查询（含密码哈希，用于登录校验） */
    User selectByUsername(@Param("username") String username);

    /** 根据 id 查询（不含密码场景用） */
    User selectById(@Param("id") Long id);

    /** 启动时初始化 admin：若 username 不存在则插入，返回受影响行数（0=已存在） */
    int insertIfAbsent(User user);

    /**
     * 分页查询用户列表（不含密码字段）。
     * @param username 模糊匹配，可空
     * @param offset   偏移
     * @param size     每页条数
     */
    List<User> list(@Param("username") String username,
                    @Param("offset") int offset,
                    @Param("size") int size);

    /** 满足过滤条件的总数 */
    long count(@Param("username") String username);
}