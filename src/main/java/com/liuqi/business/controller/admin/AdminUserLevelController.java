package com.liuqi.business.controller.admin;


import com.alibaba.fastjson.JSONObject;
import com.liuqi.base.BaseAdminController;
import com.liuqi.base.BaseService;
import com.liuqi.business.dto.UserTreeDto;
import com.liuqi.business.model.AddressRecordModelDto;
import com.liuqi.business.model.UserLevelModel;
import com.liuqi.business.model.UserLevelModelDto;
import com.liuqi.business.model.UserModelDto;
import com.liuqi.business.service.AddressRecordService;
import com.liuqi.business.service.UserLevelService;
import com.liuqi.response.ReturnResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/userLevel")
public class AdminUserLevelController extends BaseAdminController<UserLevelModel, UserLevelModelDto> {

    @Autowired
    private UserLevelService userLevelService;
    @Autowired
    private AddressRecordService addressRecordService;
    //jsp基础路径
    private final static String JSP_BASE_PTH = "admin";
    //模块
    private final static String BASE_MODUEL = "userLevel";
    //默认为""表示可以使用add和update。  重写add或update时，原有方法禁止使用 NOT_OPERATE="add,update"
    private final static String NOT_OPERATE = "add,update";

    @Override
    public BaseService getBaseService() {
        return this.userLevelService;
    }

    @Override
    public String getJspBasePath() {
        return JSP_BASE_PTH;
    }

    @Override
    public String getBaseModuel() {
        return BASE_MODUEL;
    }

    @Override
    public String getNotOperate() {
        return NOT_OPERATE;
    }

    @Override
    public String getDefaultExportName() {
        return DEFAULT_EXPORTNAME;
    }

    /*******待修改  排序  导出**********************************************************************************************************/
    //默认导出名称
    private final static String DEFAULT_EXPORTNAME = "用户层级";

    @Override
    public String[] getDefaultExportHeaders() {
        String[] headers = {"主键", "创建时间", "用户", "领导", "层级树", "层级树信息"};
        return headers;
    }

    @Override
    public String[] getDefaultExportColumns() {
        String[] columns = {"id", "createTime", "userName", "parentName", "treeLevel", "treeInfo"};
        return columns;
    }

    /*******自己代码**********************************************************************************************************/
    @Override
    protected void listHandle(UserLevelModelDto dto, HttpServletRequest request) {
        //设置用户
        super.listHandle(dto, request);
        //设置用户
        if (StringUtils.isNotEmpty(dto.getSucAddress())) {
            AddressRecordModelDto a1 = addressRecordService.getByAddress(dto.getSucAddress());
            dto.setUserId(a1 == null ? 0L : a1.getId());
        }
        //设置用户
        if (StringUtils.isNotEmpty(dto.getParentName())) {
            AddressRecordModelDto a2 = addressRecordService.getByAddress(dto.getParentName());
            dto.setParentId(a2 == null ? 0L : a2.getId());
        }
        if (StringUtils.isNotEmpty(dto.getParentName2())) {
            AddressRecordModelDto a3 = addressRecordService.getByAddress(dto.getParentName2());
            String searchTreeInfo = ",0,";
            if (a3 != null) {
                UserLevelModelDto level = userLevelService.getByUserId(a3.getId());
                searchTreeInfo = level.getTreeInfo();
            }
            dto.setSearchTreeInfo(searchTreeInfo);
        }
    }

    @RequestMapping("/toModifyParent")
    public String toUpdate(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, @RequestParam("id") Long id) {
        UserLevelModelDto m = userLevelService.getById(id);
        modelMap.put("m", m);
        this.toUpdateHandle(m, modelMap, request, response);
        return getJspBasePath() + "/" + getBaseModuel() + "/" + getBaseModuel() + "UpdateParent";
    }

    @RequestMapping("/updatePraent")
    @ResponseBody
    public ReturnResponse update(@RequestParam("id") Long id, @RequestParam("parentName") String parentName, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
        try {
            UserLevelModelDto m = userLevelService.getById(id);
            if (m == null) {
                return ReturnResponse.backFail("数据异常");
            }
            Long parentId = 0L;
            if (StringUtils.isNotEmpty(parentName)) {
                AddressRecordModelDto a = addressRecordService.getByAddress(parentName);
                if (a == null) {
                    return ReturnResponse.backFail("上级地址不存在");
                }
                parentId = a.getId();
            }
            userLevelService.changeLevel(m, parentId);
            return ReturnResponse.backSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResponse.backFail(e.getMessage());
        }
    }

    /**
     * 显示页面
     *
     * @param modelMap
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/toShow")
    public String toShow(@RequestParam(value = "userId", defaultValue = "0") Long userId,
                         ModelMap modelMap, HttpServletRequest request,
                         HttpServletResponse response) {
        this.toListHandle(modelMap, request, response);
        modelMap.put("userId", userId);
        return getJspBasePath() + "/" + getBaseModuel() + "/" + getBaseModuel() + "Show";
    }

    /**
     * 显示页面
     *
     * @param modelMap
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/getChild")
    @ResponseBody
    public String getChild(@RequestParam(value = "pId", defaultValue = "0") Long pId, ModelMap modelMap, HttpServletRequest request,
                           HttpServletResponse response) {
        List<UserTreeDto> list = userLevelService.getTreeByParentId(pId);
        return JSONObject.toJSONString(list);
    }

    /**
     * 显示页面
     *
     * @param modelMap
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/getChildById")
    @ResponseBody
    public String getChildByName(@RequestParam(value = "userId", defaultValue = "0") Long userId, ModelMap modelMap, HttpServletRequest request,
                                 HttpServletResponse response) {
        if (userId > 0) {
            List<UserTreeDto> list = userLevelService.getTreeByUserId(userId);
            return JSONObject.toJSONString(list);
        }
        return "暂无数据";
    }

}
