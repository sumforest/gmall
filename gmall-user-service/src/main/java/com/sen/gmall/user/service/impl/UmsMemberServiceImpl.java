package com.sen.gmall.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.sen.gmall.api.beans.UmsMember;
import com.sen.gmall.api.service.UmsMemberService;
import com.sen.gmall.user.mapper.UmsMemberMapper;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

/**
 * @Auther: Sen
 * @Date: 2019/10/27 03:49
 * @Description:
 */
@Service
public class UmsMemberServiceImpl implements UmsMemberService {

    @Autowired
    private UmsMemberMapper umsMemberMapper;

    @Override
    public List<UmsMember> listUmsMember() {
        return umsMemberMapper.selectAll();
    }

}
