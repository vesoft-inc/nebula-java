/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.encoder;

import com.vesoft.nebula.HostAddr;
import com.vesoft.nebula.meta.EdgeItem;
import com.vesoft.nebula.meta.IdName;
import com.vesoft.nebula.meta.SpaceItem;
import com.vesoft.nebula.meta.TagItem;
import java.util.List;
import java.util.Map;

public interface MetaCache {

    SpaceItem getSpace(String spaceName);

    List<IdName> listSpaces();

    TagItem getTag(String spaceName, String tagName);

    List<TagItem> listTags(String spaceName);

    EdgeItem getEdge(String spaceName, String edgeName);

    List<EdgeItem> listEdges(String spaceName);

    Map<Integer, List<HostAddr>> getPartsAlloc(String spaceName);
}
