/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.graph.data;

public class SelfSignedSSLParam extends SSLParam {
    private String crtFilePath;
    private String keyFilePath;
    private String password;

    public SelfSignedSSLParam(String crtFilePath, String keyFilePath, String password) {
        super(SignMode.SELF_SIGNED);
        this.crtFilePath = crtFilePath;
        this.keyFilePath = keyFilePath;
        this.password = password;
    }

    public String getCrtFilePath() {
        return crtFilePath;
    }

    public String getKeyFilePath() {
        return keyFilePath;
    }

    public String getPassword() {
        return password;
    }
}
