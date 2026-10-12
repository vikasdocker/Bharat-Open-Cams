(function () {
  "use strict";

  var feedGrid = document.getElementById("feed-grid");
  var countNode = document.getElementById("feed-count");
  var searchInput = document.getElementById("search-input");
  var cityFilter = document.getElementById("city-filter");
  var categoryFilter = document.getElementById("category-filter");
  var feeds = [];

  document.getElementById("current-year").textContent = String(new Date().getFullYear());

  function escapeHtml(value) {
    return String(value == null ? "" : value).replace(/[&<>"']/g, function (char) {
      return {"&":"&amp;","<":"&lt;",">":"&gt;",'"':"&quot;","'":"&#39;"}[char];
    });
  }

  function safeHttpsUrl(value) {
    try {
      var parsed = new URL(String(value || ""));
      return parsed.protocol === "https:" ? parsed.href : "";
    } catch (error) {
      return "";
    }
  }

  function labelForCategory(category) {
    var labels = {
      traffic: "Traffic",
      tourism: "Tourism",
      weather: "Weather",
      environment: "Environment",
      "public-venue": "Public places",
      "other-approved": "Other approved"
    };
    return labels[category] || "Public view";
  }

  function approvedFeeds(items) {
    return items.filter(function (feed) {
      return feed &&
        feed.status === "approved" &&
        feed.authorization &&
        feed.authorization.status === "approved" &&
        typeof feed.publisher_name === "string" &&
        safeHttpsUrl(feed.publisher_url) &&
        safeHttpsUrl(feed.source_page_url) &&
        typeof feed.attribution === "string" &&
        feed.authorization.evidence_reference &&
        ["link-only", "embedding", "redistribution"].indexOf(feed.authorization.reuse_scope) !== -1;
    });
  }

  function addCityOptions(items) {
    var cities = Array.from(new Set(items.map(function (feed) { return feed.city; }).filter(Boolean)));
    cities.sort(function (a, b) { return a.localeCompare(b); });
    cities.forEach(function (city) {
      var option = document.createElement("option");
      option.value = city;
      option.textContent = city;
      cityFilter.appendChild(option);
    });
  }

  function renderEmpty(title, message, linkText, linkUrl) {
    var link = "";
    if (linkText && linkUrl) {
      link = '<a class="text-link" href="' + escapeHtml(linkUrl) + '" target="_blank" rel="noreferrer">' + escapeHtml(linkText) + ' ↗</a>';
    }
    feedGrid.innerHTML = '<div class="empty-state"><div class="empty-illustration" aria-hidden="true">◎</div><h3>' +
      escapeHtml(title) + '</h3><p>' + escapeHtml(message) + '</p>' + link + '</div>';
  }

  function renderCard(feed) {
    var name = escapeHtml(feed.name);
    var city = escapeHtml(feed.city);
    var area = escapeHtml(feed.area || "");
    var publisher = escapeHtml(feed.publisher_name);
    var description = escapeHtml(feed.description || "An officially published public view with source details and attribution.");
    var category = escapeHtml(labelForCategory(feed.category));
    var source = safeHttpsUrl(feed.viewing_url) || safeHttpsUrl(feed.source_page_url);
    var linkLabel = feed.viewing_method === "official-link" || !safeHttpsUrl(feed.viewing_url) ? "Open source" : "View camera";
    var locationLabel = area ? city + " · " + area : city;
    return '<article class="feed-card">' +
      '<div class="feed-thumb"><span class="category-pill">' + category + '</span><span class="status-pill">✓ Verified source</span></div>' +
      '<div class="feed-card-body"><h3>' + name + '</h3>' +
      '<div class="feed-meta"><span aria-hidden="true">⌖</span><span>' + locationLabel + '</span></div>' +
      '<p class="feed-description">' + description + '</p>' +
      '<div class="feed-card-bottom"><span class="feed-publisher" title="' + publisher + '">Published by ' + publisher + '</span>' +
      '<a class="feed-open" href="' + escapeHtml(source) + '" target="_blank" rel="noopener noreferrer">' + linkLabel + ' ↗</a></div></div></article>';
  }

  function render() {
    var query = searchInput.value.trim().toLocaleLowerCase();
    var city = cityFilter.value;
    var category = categoryFilter.value;
    var filtered = feeds.filter(function (feed) {
      var searchable = [feed.name, feed.city, feed.area, feed.publisher_name, feed.description, feed.category].join(" ").toLocaleLowerCase();
      return (!query || searchable.indexOf(query) !== -1) &&
        (!city || feed.city === city) &&
        (!category || feed.category === category);
    });

    countNode.textContent = String(feeds.length);
    feedGrid.setAttribute("aria-busy", "false");

    if (!feeds.length) {
      renderEmpty("The directory is just getting started", "There are no verified feeds published yet. We will list a camera only after its official publisher and the permitted viewing method have been checked.", "Suggest an official source", "https://github.com/vikasdocker/Bharat-Open-Cams/issues/new?template=feed-submission.yml");
      return;
    }
    if (!filtered.length) {
      renderEmpty("No matching public views", "Try a different city, category or search term. Only approved, permission-verified sources appear here.", "", "");
      return;
    }
    feedGrid.innerHTML = filtered.map(renderCard).join("");
  }

  searchInput.addEventListener("input", render);
  cityFilter.addEventListener("change", render);
  categoryFilter.addEventListener("change", render);

  fetch("data/feeds.json", {cache: "no-store"})
    .then(function (response) {
      if (!response.ok) throw new Error("Catalog request failed");
      return response.json();
    })
    .then(function (catalog) {
      if (!Array.isArray(catalog)) throw new Error("Catalog must be an array");
      feeds = approvedFeeds(catalog);
      addCityOptions(feeds);
      render();
    })
    .catch(function () {
      renderEmpty("The catalog could not be loaded", "Please try refreshing the page. The directory will not invent camera listings when its verified catalog is unavailable.", "View project status", "https://github.com/vikasdocker/Bharat-Open-Cams");
      feedGrid.setAttribute("aria-busy", "false");
      countNode.textContent = "—";
    });
})();