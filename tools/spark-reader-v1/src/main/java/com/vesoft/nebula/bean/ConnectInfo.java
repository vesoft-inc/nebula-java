package com.vesoft.nebula.bean;

import com.google.common.base.Preconditions;
import com.vesoft.nebula.common.Checkable;
import java.io.Serializable;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

@Data
public class ConnectInfo implements Checkable, Serializable {

    private String spaceName;

    private String ip;

    private int storagePort;

    private String username;

    private String password;

    @Override
    public void check() throws IllegalArgumentException {
        Preconditions.checkArgument(StringUtils.isNotEmpty(spaceName),
                "The spaceName can't be null or empty");
        Preconditions.checkArgument(StringUtils.isNotEmpty(ip),
                "The connnect ip can't be null or empty");

        boolean isIllegalPort = (storagePort > 1024 && storagePort < 65536);
        Preconditions.checkArgument(isIllegalPort,
                "The port is not between 1024 and 65536");
    }
}
