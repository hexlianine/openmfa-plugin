import jenkins.model.Jenkins
import hudson.security.FullControlOnceLoggedInAuthorizationStrategy
import hudson.security.HudsonPrivateSecurityRealm

// This script runs on Jenkins startup.
// Keep it for local/dev only. Avoid committing real credentials.

def j = Jenkins.get()

// Create a local user database (no self-signup).
def realm = new HudsonPrivateSecurityRealm(false)

// Create a dev user only if it does not already exist.
// (Jenkins stores users on disk; on subsequent startups this should be a no-op.)
try {
  realm.loadUserByUsername("dev")
} catch (Exception ignored) {
  // Username not found -> create it.
  realm.createAccount("dev", "dev")
}

// Activate the local realm.
j.setSecurityRealm(realm)

// Authorization: logged-in users have full control; anonymous users have no read access.
def strategy = new FullControlOnceLoggedInAuthorizationStrategy()
strategy.setAllowAnonymousRead(false)
j.setAuthorizationStrategy(strategy)

// Persist security configuration.
j.save()
