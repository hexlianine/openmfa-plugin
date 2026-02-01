package io.jenkins.plugins.openmfa.service.model;

import lombok.Getter;

/**
 * Data transfer object containing user MFA status information.
 */
@Getter
public class UserInfo {

  /** The user's display name */
  private final String fullName;

  /** Whether MFA is currently enabled for this user */
  private final boolean mfaEnabled;

  /** The user's unique identifier */
  private final String userId;

  public UserInfo(final String userId, final String fullName,
    final boolean mfaEnabled) {
    this.userId = userId;
    this.fullName = fullName;
    this.mfaEnabled = mfaEnabled;
  }

  /**
   * Returns the CSS class to use for the status badge.
   */
  public String getStatusClass() {
    return mfaEnabled ? "mfa-status-enabled" : "mfa-status-disabled";
  }

  /**
   * Returns a human-readable status string for the MFA state.
   */
  public String getStatusText() {
    return mfaEnabled ? "Enabled" : "Disabled";
  }

  public boolean isMfaEnabled() {
    return mfaEnabled;
  }

}
