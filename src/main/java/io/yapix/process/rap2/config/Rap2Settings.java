package io.yapix.process.rap2.config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.Transient;
import io.yapix.base.sdk.rap2.AbstractClient.HttpSession;
import io.yapix.base.sdk.rap2.Rap2Client;
import io.yapix.base.sdk.rap2.model.AuthCookies;
import io.yapix.base.sdk.rap2.request.Rap2TestResult;
import io.yapix.base.util.PasswordSafeUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Rap2应用程序级别配置.
 */
@State(name = "YapixRap2Settings", storages = @Storage("YapixRap2Settings.xml"))
public class Rap2Settings implements PersistentStateComponent<Rap2Settings> {

    private static final String PASSWORD_KEY = "rap2";

    /**
     * 服务地址
     */
    private String url;

    /**
     * 网页根地址
     */
    private String webUrl;

    /**
     * 用户名
     */
    private String account;

    /**
     * 密码
     */
    @Transient
    private String password;

    /**
     * 登录后的cookies
     */
    private String cookies;

    /**
     * 授权cookies的有效期.
     */
    private long cookiesTtl;

    /** 授权用户id */
    private Long cookiesUserId;

    public static Rap2Settings getInstance() {
        Rap2Settings settings = ServiceManager.getService(Rap2Settings.class);
        settings.password = PasswordSafeUtils.getPassword(PASSWORD_KEY, settings.account);
        return settings;
    }

    @Nullable
    @Override
    public Rap2Settings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull Rap2Settings state) {
        PasswordSafeUtils.storePassword(PASSWORD_KEY, state.account, state.password);
        XmlSerializerUtil.copyBean(state, this);
    }

    /**
     * 配置是否有效
     */
    public boolean isValidate() {
        return StringUtils.isNotEmpty(url) && StringUtils.isNotEmpty(webUrl)
                && StringUtils.isNotEmpty(account) && StringUtils.isNotEmpty(password);
    }

    public Rap2TestResult testSettings(String captcha, HttpSession captchaSession) {
        // 测试账户
        try (Rap2Client client = new Rap2Client(this.getUrl(), this.getAccount(), this.getPassword(),
                this.getCookies(), this.getCookiesTtl(), this.getCookiesUserId())) {
            Rap2TestResult testResult = client.test(captcha, captchaSession);
            Rap2TestResult.Code code = testResult.getCode();
            if (code == Rap2TestResult.Code.OK) {
                HttpSession authSession = client.getAuthSession();
                if (authSession != null) {
                    testResult.setAuthCookies(new AuthCookies(authSession.getCookies(), authSession.getCookiesTtl()));
                    testResult.setAuthUser(client.getCurrentUser());
                }
            }
            return testResult;
        }
    }

    //----------------------generated----------------------//

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    @Transient
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCookies() {
        return cookies;
    }

    public void setCookies(String cookies) {
        this.cookies = cookies;
    }

    public long getCookiesTtl() {
        return cookiesTtl;
    }

    public void setCookiesTtl(long cookiesTtl) {
        this.cookiesTtl = cookiesTtl;
    }

    public Long getCookiesUserId() {
        return cookiesUserId;
    }

    public void setCookiesUserId(Long cookiesUserId) {
        this.cookiesUserId = cookiesUserId;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Rap2Settings)) {
            return false;
        }

        Rap2Settings that = (Rap2Settings) o;

        if (url != null ? !url.equals(that.url) : that.url != null) {
            return false;
        }
        if (webUrl != null ? !webUrl.equals(that.webUrl) : that.webUrl != null) {
            return false;
        }
        if (account != null ? !account.equals(that.account) : that.account != null) {
            return false;
        }
        return password != null ? password.equals(that.password) : that.password == null;
    }

    @Override
    public int hashCode() {
        int result = url != null ? url.hashCode() : 0;
        result = 31 * result + (webUrl != null ? webUrl.hashCode() : 0);
        result = 31 * result + (account != null ? account.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }
}
