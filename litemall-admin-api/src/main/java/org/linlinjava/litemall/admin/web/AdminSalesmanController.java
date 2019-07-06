package org.linlinjava.litemall.admin.web;


import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.linlinjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.core.validator.Order;
import org.linlinjava.litemall.core.validator.Sort;
import org.linlinjava.litemall.db.domain.LitemallSalesman;
import org.linlinjava.litemall.db.service.LitemallSalesmanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/salesman")
@Validated
public class AdminSalesmanController {
    @Autowired
    private LitemallSalesmanService litemallSalesmanService;

    @RequiresPermissions("admin:salesman:list")
    @RequiresPermissionsDesc(menu = {"团长管理", "团长管理"}, button = "查询")
    @GetMapping("/list")
    public Object list(String name,String phone,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @Sort @RequestParam(defaultValue = "add_time") String sort,
                       @Order @RequestParam(defaultValue = "desc") String order) {
        List<LitemallSalesman> litemallSalesmanList = litemallSalesmanService.querySalesmanSelective(name, phone, page, limit, sort, order);
        return ResponseUtil.okList(litemallSalesmanList);
    }

    @RequiresPermissions("admin:salesman:delete")
    @RequiresPermissionsDesc(menu = {"团长管理", "团长管理"}, button = "删除")
    @PostMapping("/delete")
    public Object delete(@RequestBody LitemallSalesman litemallSalesman) {
        Integer id = litemallSalesman.getId();
        if (id == null) {
            return ResponseUtil.badArgument();
        }
        litemallSalesmanService.deleteById(id);
        return ResponseUtil.ok();
    }

    @RequiresPermissions("admin:salesman:audit")
    @RequiresPermissionsDesc(menu = {"团长管理", "团长管理"}, button = "删除")
    @PostMapping("/audit")
    public Object audit(@RequestBody LitemallSalesman litemallSalesman) {
        Integer id = litemallSalesman.getId();
        if (id == null) {
            return ResponseUtil.badArgument();
        }
        litemallSalesmanService.auditById(litemallSalesman);
        return ResponseUtil.ok();
    }
}
