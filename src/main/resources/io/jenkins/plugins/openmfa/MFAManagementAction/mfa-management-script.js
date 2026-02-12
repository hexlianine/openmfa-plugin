/**
 * MFA Management Dashboard Scripts
 */

var _pendingResetForm = null;

/**
 * Get localized labels and messages for the reset dialog from the DOM.
 */
function _getResetDialogLabels() {
  var el = document.getElementById('mfa-reset-dialog-messages');
  if (!el) return null;
  return {
    title: el.getAttribute('data-title') || '',
    messagePrefix: el.getAttribute('data-message-prefix') || '',
    messageSuffix: el.getAttribute('data-message-suffix') || '',
    okText: el.getAttribute('data-ok-text') || '',
    cancelText: el.getAttribute('data-cancel-text') || ''
  };
}

/**
 * Shows the reset MFA confirmation dialog using Jenkins Design Library dialogs.
 * Falls back to the browser confirm() if the dialog API is unavailable.
 * @param {string} userId - The ID of the user to reset
 * @param {HTMLFormElement} form - The form to submit on confirm
 */
function showResetConfirm(userId, form) {
  var labels = _getResetDialogLabels();
  var message = (labels ? labels.messagePrefix : '') + ' ' + userId + (labels ? labels.messageSuffix : '');

  // Fallback if the Design Library dialog API is not available
  if (typeof dialog === 'undefined') {
    if (window.confirm(message)) {
      form.submit();
    }
    return;
  }

  _pendingResetForm = form;

  dialog
    .confirm(labels ? labels.title : '', {
      message: message,
      okText: labels ? labels.okText : undefined,
      cancelText: labels ? labels.cancelText : undefined,
      type: 'destructive'
    })
    .then(function(confirmed) {
      if (confirmed && _pendingResetForm) {
        _pendingResetForm.submit();
      }
      _pendingResetForm = null;
    });
}

/**
 * Show notificationBar if server rendered notification data is present.
 */
function initNotification() {
  var el = document.getElementById('mfa-notification-data');
  if (!el || typeof notificationBar === 'undefined') return;
  var msg = el.getAttribute('data-msg');
  var type = el.getAttribute('data-type');
  if (!msg) return;
  var barType = type === 'error' ? notificationBar.ERROR : notificationBar.SUCCESS;
  notificationBar.show(msg, barType);
}

/**
 * Initialize page functionality on load.
 * Wires up event handlers without using inline JavaScript.
 */
(function() {
  function init() {
    initNotification();

    var resetForms = document.querySelectorAll('.mfa-mgmt-reset-form');
    for (var j = 0; j < resetForms.length; j++) {
      (function(form) {
        var resetBtn = form.querySelector('button');
        if (resetBtn) {
          resetBtn.addEventListener('click', function() {
            var userIdInput = form.querySelector('input[name="userId"]');
            if (userIdInput) {
              showResetConfirm(userIdInput.value, form);
            }
          });
        }
      })(resetForms[j]);
    }
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init);
  } else {
    init();
  }
})();
