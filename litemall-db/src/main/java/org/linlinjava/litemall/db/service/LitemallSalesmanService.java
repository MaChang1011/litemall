package org.linlinjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.linlinjava.litemall.db.dao.LitemallSalesmanMapper;
import org.linlinjava.litemall.db.domain.LitemallSalesman;
import org.linlinjava.litemall.db.domain.LitemallSalesmanExample;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class LitemallSalesmanService {
    @Resource
    private LitemallSalesmanMapper litemallSalesmanMapper;


    public List<LitemallSalesman> querySalesmanSelective(String name, String phone, Integer page, Integer size, String sort, String order) {
        LitemallSalesmanExample example = new LitemallSalesmanExample();
        LitemallSalesmanExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(name)) {
            criteria.andNameLike("%" + name + "%");
        }
        if (!StringUtils.isEmpty(phone)) {
            criteria.andPhoneLike("%" + phone + "%");
        }

        criteria.andDeletedEqualTo(false);
        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }
        PageHelper.startPage(page, size);
        return litemallSalesmanMapper.selectByExample(example);
    }


    public void deleteById(Integer id) {
        litemallSalesmanMapper.logicalDeleteByPrimaryKey(id);
    }

    public void auditById(LitemallSalesman litemallSalesman) {
        litemallSalesmanMapper.updateByPrimaryKeySelective(litemallSalesman);
    }
}
