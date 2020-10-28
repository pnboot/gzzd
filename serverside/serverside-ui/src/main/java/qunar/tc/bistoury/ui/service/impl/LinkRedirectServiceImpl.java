/*
 * Copyright (C) 2019 Qunar, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package qunar.tc.bistoury.ui.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import qunar.tc.bistoury.ui.config.UrlRedirectMapConfig;
import qunar.tc.bistoury.ui.service.URLRedirectService;

import java.util.HashMap;
import java.util.Map;

/**
 * @author leix.xie
 * @date 2019/7/10 15:37
 * @describe
 */
@Service
public class LinkRedirectServiceImpl implements URLRedirectService {

    // todo 这些配置可以优化到配置文件
    static public Map<String, String> URL_REDIRECT_MAP = new HashMap() {{
        put("monitor.help.url", "https://github.com/qunarcorp/bistoury/blob/master/docs/cn/monitor.md");
        put("debug.help.url", "https://github.com/qunarcorp/bistoury/blob/master/docs/cn/debug.md");
        put("jstack.help.url", "https://github.com/qunarcorp/bistoury/blob/master/docs/cn/jstack.md");
        put("jmap.help.url", "https://github.com/qunarcorp/bistoury/blob/master/docs/cn/jmap.md");
        put("gitlab.private.token.url", "https://github.com/settings/tokens");
        put("bistoury.ui.dev", "http://bistoury.dev.example.com");
        put("bistoury.ui.beta", "http://bistoury.beta.example.com");
        put("bistoury.ui.prod", "http://bistoury.prod.example.com");
    }};

    @Autowired
    UrlRedirectMapConfig urlRedirectMapConfig;

    public LinkRedirectServiceImpl() {
        // debug
        //System.out.println("x");
    }

    @Override
    public String getURLByName(final String name) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(name), "url name cannot be null or empty");
        String url = URL_REDIRECT_MAP.get(name);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(url), "url cannot be null or empty");
        return url;
    }
}
