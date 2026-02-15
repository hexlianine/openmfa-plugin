let currentPage = 1;
let pageSize = 10;
let totalPages = 1;
let allRows = [];

/**
 * Changes the page size and re-renders the table.
 * @param {number|string} newSize - The new page size
 */
function changePageSize(newSize) {
  pageSize = parseInt(newSize, 10);
  currentPage = 1;
  renderPage();
}

/**
 * Navigates to a specific page.
 * @param {number} page - The page number to navigate to
 */
function goToPage(page) {
  if (page < 1 || page > totalPages) return;
  currentPage = page;
  renderPage();
}

/**
 * Renders the current page of the table.
 */
function renderPage() {
  var totalItems = allRows.length;
  totalPages = Math.ceil(totalItems / pageSize) || 1;

  if (currentPage > totalPages) {
    currentPage = totalPages;
  }

  var startIndex = (currentPage - 1) * pageSize;
  var endIndex = Math.min(startIndex + pageSize, totalItems);

  // Hide all rows, then show only current page
  for (var i = 0; i < allRows.length; i++) {
    allRows[i].style.display = 'none';
  }
  for (var i = startIndex; i < endIndex; i++) {
    allRows[i].style.display = '';
  }

  // Update pagination info
  var infoEl = document.getElementById('mfa-pagination-info');
  if (infoEl && totalItems > 0) {
    var showingStart = startIndex + 1;
    var showingEnd = endIndex;
    infoEl.textContent =
      paginationShowingText +
      ' ' +
      showingStart +
      ' ' +
      paginationToText +
      ' ' +
      showingEnd +
      ' ' +
      paginationOfText +
      ' ' +
      totalItems +
      ' ' +
      paginationEntriesText;
  } else if (infoEl) {
    infoEl.textContent = '';
  }

  // Update button states
  var firstBtn = document.getElementById('mfa-page-first');
  var prevBtn = document.getElementById('mfa-page-prev');
  var nextBtn = document.getElementById('mfa-page-next');
  var lastBtn = document.getElementById('mfa-page-last');

  if (firstBtn) firstBtn.disabled = currentPage <= 1;
  if (prevBtn) prevBtn.disabled = currentPage <= 1;
  if (nextBtn) nextBtn.disabled = currentPage >= totalPages;
  if (lastBtn) lastBtn.disabled = currentPage >= totalPages;

  // Render page numbers
  renderPageNumbers();
}

/**
 * Renders the page number buttons.
 */
function renderPageNumbers() {
  var container = document.getElementById('mfa-page-numbers');
  if (!container) return;

  container.innerHTML = '';

  var maxVisible = 5;
  var startPage = Math.max(1, currentPage - Math.floor(maxVisible / 2));
  var endPage = Math.min(totalPages, startPage + maxVisible - 1);

  if (endPage - startPage + 1 < maxVisible) {
    startPage = Math.max(1, endPage - maxVisible + 1);
  }

  for (var i = startPage; i <= endPage; i++) {
    var btn = document.createElement('button');
    btn.type = 'button';
    btn.className = 'mfa-mgmt-pagination-btn mfa-mgmt-pagination-num';
    if (i === currentPage) {
      btn.className += ' mfa-mgmt-pagination-active';
    }
    btn.textContent = i;
    btn.setAttribute('data-page', i);
    btn.onclick = (function (page) {
      return function () {
        goToPage(page);
      };
    })(i);
    container.appendChild(btn);
  }
}

/**
 * Initializes pagination for the user table.
 */
function initPagination() {
  var table = document.getElementById('mfa-users-table');
  if (!table) return;

  var tbody = table.querySelector('tbody');
  if (!tbody) return;

  allRows = Array.prototype.slice.call(tbody.querySelectorAll('.mfa-user-row'));

  // Hide pagination if only one page needed
  var paginationEl = document.getElementById('mfa-pagination');
  if (allRows.length <= pageSize && paginationEl) {
    // Still show it for consistency, but could hide if preferred
  }

  renderPage();
}

/**
 * Initialize page functionality on load.
 */
(function () {
  // Initialize pagination when DOM is ready
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initPagination);
  } else {
    initPagination();
  }
})();
