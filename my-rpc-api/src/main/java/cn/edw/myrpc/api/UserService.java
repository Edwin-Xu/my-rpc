package cn.edw.myrpc.api;

import cn.edw.myrpc.model.User;

/**
 * @author taoxu.xu
 * @date 8/15/2021 1:30 AM
 */
public interface UserService {
    User getUserById(Integer id);
    Boolean save(User user);
}
