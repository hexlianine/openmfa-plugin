package io.jenkins.plugins.openmfa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.jenkins.plugins.openmfa.constant.UIConstants;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

@WithJenkins
class MFAGlobalConfigurationTest extends JenkinsRule {

  @Test
  void testDefaultConfiguration(JenkinsRule j) {
    MFAGlobalConfiguration config = MFAGlobalConfiguration.get();

    assertNotNull(config);
    assertFalse(config.isRequireMFA());
    assertEquals(UIConstants.Defaults.DEFAULT_ISSUER, config.getIssuer());
  }

  @Test
  void testGetDefaultIssuer(JenkinsRule j) {
    MFAGlobalConfiguration config = MFAGlobalConfiguration.get();

    assertEquals(UIConstants.Defaults.DEFAULT_ISSUER, config.getDefaultIssuer());
  }

  @Test
  void testGetDisplayName(JenkinsRule j) {
    MFAGlobalConfiguration config = MFAGlobalConfiguration.get();

    assertEquals(
      UIConstants.DisplayNames.OPENMFA_GLOBAL_CONFIGURATION, config.getDisplayName()
    );
  }

  @Test
  void testPersistence(JenkinsRule j) {
    MFAGlobalConfiguration config = MFAGlobalConfiguration.get();

    // Set custom values
    config.setRequireMFA(true);
    config.setIssuer("TestCompany");
    config.save();

    // Reload configuration
    config.load();

    // Verify values persisted
    assertTrue(config.isRequireMFA());
    assertEquals("TestCompany", config.getIssuer());
  }

  @Test
  void testSetIssuer(JenkinsRule j) {
    MFAGlobalConfiguration config = MFAGlobalConfiguration.get();

    config.setIssuer("MyCompany");
    assertEquals("MyCompany", config.getIssuer());

    // Test trimming
    config.setIssuer("  SpacedName  ");
    assertEquals("SpacedName", config.getIssuer());

    // Test empty string defaults to Jenkins
    config.setIssuer("");
    assertEquals(UIConstants.Defaults.DEFAULT_ISSUER, config.getIssuer());

    // Test null defaults to Jenkins
    config.setIssuer(null);
    assertEquals(UIConstants.Defaults.DEFAULT_ISSUER, config.getIssuer());
  }

  @Test
  void testSetRequireMFA(JenkinsRule j) {
    MFAGlobalConfiguration config = MFAGlobalConfiguration.get();

    config.setRequireMFA(true);
    assertTrue(config.isRequireMFA());

    config.setRequireMFA(false);
    assertFalse(config.isRequireMFA());
  }
}
