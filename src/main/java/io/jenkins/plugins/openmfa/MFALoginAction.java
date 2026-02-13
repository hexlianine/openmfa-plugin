package io.jenkins.plugins.openmfa;

import hudson.Extension;
import hudson.model.InvisibleAction;
import hudson.model.RootAction;
import hudson.model.User;
import io.jenkins.plugins.openmfa.base.MFAContext;
import io.jenkins.plugins.openmfa.constant.PluginConstants;
import io.jenkins.plugins.openmfa.service.SessionService;
import io.jenkins.plugins.openmfa.util.JenkinsUtil;
import io.jenkins.plugins.openmfa.util.TOTPUtil;

import jakarta.servlet.http.HttpSession;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.HttpResponses;
import org.kohsuke.stapler.Stapler;
import org.kohsuke.stapler.interceptor.RequirePOST;

/**
 * Action that provides the MFA login page where users enter their TOTP code.
 */
@Extension
public class MFALoginAction extends InvisibleAction implements RootAction {

  /**
   * Handles TOTP code verification via POST.
   */
  @RequirePOST
  @SuppressWarnings("lgtm[jenkins/no-permission-check]")
  public HttpResponse doVerify() {
    User user = User.current();
    if (user == null) {
      return HttpResponses.forbidden();
    }

    var req = Stapler.getCurrentRequest2();
    String totpCode = req.getParameter(PluginConstants.FormParameters.TOTP_CODE);

    MFAUserProperty mfaProperty = MFAUserProperty.forUser(user);
    if (mfaProperty == null || !mfaProperty.verifyCode(totpCode)) {
      return HttpResponses.redirectViaContextPath(
        "/" + PluginConstants.Urls.LOGIN_ACTION_URL + "?error=invalid"
      );
    }

    // Mark MFA as verified in session
    MFAContext.i()
      .getService(SessionService.class)
      .verifySession(req);

    // Redirect to root
    return HttpResponses.redirectViaContextPath("/");
  }

  /**
   * Get the form parameter name for TOTP code (for Jelly views).
   */
  public String getFormParamTotpCode() {
    return PluginConstants.FormParameters.TOTP_CODE;
  }

  /**
   * Gets the username from the current user.
   */
  public String getPendingUsername() {
    return JenkinsUtil.getCurrentUser().map(User::getId).orElse(null);
  }

  /**
   * Get the security check endpoint (for Jelly views).
   */
  public String getSecurityCheckEndpoint() {
    return PluginConstants.Urls.SECURITY_CHECK_ENDPOINT;
  }

  @Override
  public String getUrlName() {
    return PluginConstants.Urls.LOGIN_ACTION_URL;
  }

  /**
   * Check if there's a pending MFA authentication.
   */
  public boolean hasPendingAuth() {
    return getPendingUsername() != null;
  }

  /**
   * Check if the current session has already passed MFA verification.
   *
   * @return true if the current session has already passed MFA verification,
   *         false otherwise
   */
  public boolean isSessionMFAVerified() {
    var req = Stapler.getCurrentRequest2();
    if (req == null) {
      return false;
    }

    HttpSession session = req.getSession(false);
    return session != null
      && MFAContext.i()
        .getService(SessionService.class)
        .isVerifiedSession(session);
  }

}
