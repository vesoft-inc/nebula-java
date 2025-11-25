/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.util;

import com.vesoft.nebula.driver.graph.utils.ZoneOffsetUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZoneOffsetUtilTest {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Test
    public void testBuildOffset() {
        assert ("Z".equals(ZoneOffsetUtil.buildOffset(0)));
        assert ("-18:00".equals(ZoneOffsetUtil.buildOffset(-64800)));
        assert ("+18:00".equals(ZoneOffsetUtil.buildOffset(64800)));
        assert ("+08:00".equals(ZoneOffsetUtil.buildOffset(8 * 60 * 60)));
        assert ("-08:00".equals(ZoneOffsetUtil.buildOffset(-8 * 60 * 60)));
        assert ("+08:30".equals(ZoneOffsetUtil.buildOffset(8 * 60 * 60 + 30 * 60)));
        assert ("-08:30".equals(ZoneOffsetUtil.buildOffset(-(8 * 60 * 60 + 30 * 60))));

        assert ("+00:16:40".equals(ZoneOffsetUtil.buildOffset(1000)));
        assert ("-00:16:40".equals(ZoneOffsetUtil.buildOffset(-1000)));
        assert ("+00:33:20".equals(ZoneOffsetUtil.buildOffset(2000)));
        assert ("-00:33:20".equals(ZoneOffsetUtil.buildOffset(-2000)));
    }
}
