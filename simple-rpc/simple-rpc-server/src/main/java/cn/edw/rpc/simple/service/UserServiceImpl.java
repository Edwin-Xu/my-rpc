package cn.edw.rpc.simple.service;

import cn.edw.myrpc.api.UserService;
import cn.edw.myrpc.model.User;

/**
 * @author taoxu.xu
 * @date 8/15/2021 11:37 PM
 */
public class UserServiceImpl implements UserService {
    @Override
    public User getUserById(Integer id) {
        return new User(id,"edw",true, 2000.0999);
    }

    @Override
    public Boolean save(User user) {
        return null;
    }
}
