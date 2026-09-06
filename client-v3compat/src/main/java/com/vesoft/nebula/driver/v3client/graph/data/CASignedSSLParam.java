/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.v3client.graph.data;

/**
 * CA-signed TLS configuration, matching the v3 client.
 */
public class CASignedSSLParam extends SSLParam {
    private String caCrtFilePath;
    private String crtFilePath;
    private String keyFilePath;

    public CASignedSSLParam() {
        super(SignMode.CA_SIGNED);
    }

    public CASignedSSLParam(String caCrtFilePath, String crtFilePath, String keyFilePath) {
        super(SignMode.CA_SIGNED);
        this.caCrtFilePath = caCrtFilePath;
        this.crtFilePath = crtFilePath;
        this.keyFilePath = keyFilePath;
    }

    public String getCaCrtFilePath() {
        return caCrtFilePath;
    }

    public String getCrtFilePath() {
        return crtFilePath;
    }

    public String getKeyFilePath() {
        return keyFilePath;
    }
}
