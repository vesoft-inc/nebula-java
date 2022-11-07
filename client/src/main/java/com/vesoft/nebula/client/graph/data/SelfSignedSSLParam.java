/* Copyright (c) 2021 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.client.graph.data;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class SelfSignedSSLParam extends SSLParam {
    String crtFilePath;
    String keyFilePath;
    String password;

    public SelfSignedSSLParam(String crtFilePath, String keyFilePath, String password) {
        super(SignMode.SELF_SIGNED);
        this.crtFilePath = crtFilePath;
        this.keyFilePath = keyFilePath;
        this.password = password;
    }
}
