# Action, InvisibleAction, and RootAction

Technical reference for Jenkins action types and when to use each.

## Overview

| Type | Package | Purpose |
|------|---------|---------|
| **Action** | `hudson.model.Action` | Interface for attachable UI/data. Provides `getDisplayName()`, `getIconFileName()`, `getUrlName()`. |
| **InvisibleAction** | `hudson.model.InvisibleAction` | Abstract base class that implements Action with no UI presence. Returns `null` for `getDisplayName()` and `getIconFileName()`. |
| **RootAction** | `hudson.model.RootAction` | Marker interface extending Action and ExtensionPoint. Actions registered at Jenkins root URL when annotated with `@Extension`. |

## Action

**Package:** `hudson.model.Action`

The base interface for actions in Jenkins. Actions can be attached to builds, projects, or the root. They expose optional UI elements and URL-accessible content.

### Key Methods

| Method | Returns | Purpose |
|--------|---------|---------|
| `getDisplayName()` | String or null | Label for breadcrumbs, page headers |
| `getIconFileName()` | String or null | Icon shown in sidebar; `null` = not shown |
| `getUrlName()` | String or null | URL path segment for Stapler routing |

## InvisibleAction

**Package:** `hudson.model.InvisibleAction`

An abstract class implementing Action with defaults suited for actions that should not appear in the Jenkins UI. Designed for endpoints that exist only to serve HTTP requests.

### Behavior

- **getDisplayName():** Returns `null` (final – cannot override)
- **getIconFileName():** Returns `null` (action hidden from sidebar)
- **getUrlName():** Returns `null` by default; override to expose a URL

### When to Use

- Action exists solely as a URL endpoint (e.g. login form, webhook handler)
- No need for sidebar visibility or display name
- Want to avoid accidentally showing the action in the UI

### Limitation

Because `getDisplayName()` is final, you cannot customize the display name. If you need one (e.g. for breadcrumbs), use `implements RootAction` with a custom `getIconFileName()` returning `null` instead.

## RootAction

**Package:** `hudson.model.RootAction`

A marker interface for actions bound at the Jenkins root. Implementations with `@Extension` are auto-discovered and registered. RootAction extends both Action and ExtensionPoint.

### Behavior

- **URL:** `<root>/<getUrlName()>` (e.g. `/mfa-login`)
- **Registration:** `@Extension` makes it available at root level
- **Visibility:** Controlled by `getIconFileName()` – return `null` to hide from sidebar

## Combining InvisibleAction and RootAction

For root-level endpoints that should not appear in the UI, extend InvisibleAction and implement RootAction:

```java
@Extension
public class MFALoginAction extends InvisibleAction implements RootAction {
  @Override
  public String getUrlName() {
    return "mfa-login";
  }
  // getIconFileName() and getDisplayName() inherited as null from InvisibleAction
}
```

**Why both?** InvisibleAction does not implement RootAction. RootAction is required for `@Extension` to register the action at Jenkins root. InvisibleAction provides the “no UI” defaults so you don’t have to override `getIconFileName()`.

## Comparison

| Aspect | Action | InvisibleAction | RootAction |
|--------|--------|-----------------|------------|
| **Type** | Interface | Abstract class | Interface |
| **Extends** | — | Action | Action, ExtensionPoint |
| **getDisplayName()** | Optional | Always null (final) | Optional |
| **getIconFileName()** | Optional | Always null | Optional |
| **Root registration** | No | No | Yes (with @Extension) |
| **Typical use** | Attached to build/job | Invisible endpoints | Root-level URLs |

## References

- [Action Javadoc](https://javadoc.jenkins.io/hudson/model/Action.html)
- [InvisibleAction Javadoc](https://javadoc.jenkins.io/hudson/model/InvisibleAction.html)
- [RootAction Javadoc](https://javadoc.jenkins.io/hudson/model/RootAction.html)
- OpenMFA: `MFALoginAction` (extends InvisibleAction, implements RootAction)
- See also: [ManagementLink vs RootAction](managementlink-vs-rootaction.md)
