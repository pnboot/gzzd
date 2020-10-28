package qunar.tc.bistoury.proxy.web.controller.ui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import qunar.tc.bistoury.serverside.bean.ApiResult;
import qunar.tc.bistoury.serverside.util.ResultHelper;
import qunar.tc.bistoury.ui.model.PrivateToken;
import qunar.tc.bistoury.ui.security.LoginContext;

/**
 * 忽略gitlab相关请求
 */
@Controller
@RequestMapping("/api/settings/token")
public class PrivateTokenApiController {

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult saveToken(@RequestParam final String privateToken) {
        final String username = LoginContext.getLoginContext().getLoginUser();
        return ResultHelper.fail("保存 Git Private Token 失败");
    }

    @RequestMapping("/query")
    @ResponseBody
    public ApiResult<PrivateToken> queryToken() {
        final String userCode = LoginContext.getLoginContext().getLoginUser();
        return ResultHelper.fail(-2, "请先配置private token");
    }
}
