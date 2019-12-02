package com.lhc.ocat.mobileplatform.domain.dto;

import com.lhc.ocat.mobileplatform.domain.dos.ApplicationDO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

/**
 * @author lhc
 * @date 2019-11-14 18:19
 */
@Getter
@Setter
@ToString
public class Application {
    private Long id;
    /**
     * 客户端应用名称
     */
    private String name;
    /**
     * 客户端应用描述
     */
    private String description;
    /**
     * 客户端的应用唯一标识
     */
    private String appId;
    /**
     * 客户端的密钥
     */
    private String appSecret;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Application that = (Application) o;
        return Objects.equals(getName(), that.getName()) &&
                Objects.equals(getAppId(), that.getAppId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getAppId());
    }

    static public Application toApplication(ApplicationDO applicationDO) {
        Application application = new Application();
        application.setId(applicationDO.getId());
        application.setName(applicationDO.getName());
        application.setDescription(applicationDO.getDescription());
        application.setAppId(applicationDO.getAppId());
        application.setAppSecret(applicationDO.getAppSecret());
        return application;
    }
}
