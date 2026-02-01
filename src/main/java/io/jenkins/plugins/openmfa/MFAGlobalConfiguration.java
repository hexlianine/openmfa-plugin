package io.jenkins.plugins.openmfa;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.ExtensionList;
import io.jenkins.plugins.openmfa.constant.UIConstants;
import jenkins.model.GlobalConfiguration;
import jenkins.model.GlobalConfigurationCategory;
import lombok.Getter;
import lombok.Setter;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.StaplerRequest2;

/**
 * Global configuration for OpenMFA plugin.
 * Provides system-wide settings for MFA functionality.
 */
@Extension
@Getter
@Setter
public class MFAGlobalConfiguration extends GlobalConfiguration {

  /**
   * Get the singleton instance of this configuration.
   */
  @NonNull
  public static MFAGlobalConfiguration get() {
    return ExtensionList.lookupSingleton(MFAGlobalConfiguration.class);
  }

  /** Whether to enforce MFA for API tokens */
  private boolean enforceForApiTokens = false;

  /** Grace period in days for users to set up MFA after it becomes required */
  private int gracePeriodDays = 7;

  /** The issuer name shown in authenticator apps */
  private String issuer = UIConstants.Defaults.DEFAULT_ISSUER;

  /** Whether MFA is required for all users */
  private boolean requireMFA = UIConstants.Defaults.DEFAULT_REQUIRE_MFA;

  public MFAGlobalConfiguration() {
    // Load persisted configuration when Jenkins starts
    load();
  }

  @Override
  public boolean configure(StaplerRequest2 req, JSONObject json)
    throws FormException {
    req.bindJSON(this, json); // Bind the JSON object to the configuration object
    save(); // Save the configuration object
    return true;
  }

  @NonNull
  @Override
  public GlobalConfigurationCategory getCategory() {
    return GlobalConfigurationCategory
      .get(GlobalConfigurationCategory.Security.class);
  }

  /**
   * Get the default issuer name (for Jelly views).
   */
  public String getDefaultIssuer() {
    return UIConstants.Defaults.DEFAULT_ISSUER;
  }

  @NonNull
  @Override
  public String getDisplayName() {
    return UIConstants.DisplayNames.OPENMFA_GLOBAL_CONFIGURATION;
  }

  @DataBoundSetter
  public void setEnforceForApiTokens(boolean enforceForApiTokens) {
    this.enforceForApiTokens = enforceForApiTokens;
  }

  @DataBoundSetter
  public void setGracePeriodDays(int gracePeriodDays) {
    this.gracePeriodDays = Math.max(0, gracePeriodDays);
  }

  @DataBoundSetter
  public void setIssuer(String issuer) {
    this.issuer =
      issuer != null
        && !issuer.trim().isEmpty()
          ? issuer.trim()
          : UIConstants.Defaults.DEFAULT_ISSUER;
  }

  @DataBoundSetter
  public void setRequireMFA(boolean requireMFA) {
    this.requireMFA = requireMFA;
  }
}
