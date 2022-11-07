/* Copyright (c) 2021 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.client.graph.data;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class CASignedSSLParam extends SSLParam {

    String caCrtFilePath;
    String crtFilePath;
    String keyFilePath;

    public CASignedSSLParam(String caCrtFilePath, String crtFilePath, String keyFilePath) {
        super(SignMode.CA_SIGNED);
        this.caCrtFilePath = caCrtFilePath;
        this.crtFilePath = crtFilePath;
        this.keyFilePath = keyFilePath;
    }
}
