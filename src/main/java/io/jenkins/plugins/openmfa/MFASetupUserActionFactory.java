package io.jenkins.plugins.openmfa;

import hudson.Extension;
import hudson.model.Action;
import hudson.model.TransientUserActionFactory;
import hudson.model.User;
import java.util.Collection;
import java.util.Collections;
import jenkins.model.Jenkins;

/**
 * Adds the MFA setup action under user pages: /user/&lt;id&gt;/mfa-setup.
 */
@Extension
public class MFASetupUserActionFactory extends TransientUserActionFactory {

  @Override
  public Collection<? extends Action> createFor(User user) {
    // Keep the link visible only to the user themselves or admins.
    User current = User.current();
    boolean isSelf =
      current != null
        && current.getId() != null
        && current.getId().equals(user.getId());
    boolean isAdmin = Jenkins.get().hasPermission(Jenkins.ADMINISTER);

    if (!isSelf && !isAdmin) {
      return Collections.emptyList();
    }

    return Collections.singletonList(new MFASetupAction(user));
  }
}
