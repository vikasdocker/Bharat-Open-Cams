(function () {
  "use strict";

  var feedGrid = document.getElementById("feed-grid");
  var countNode = document.getElementById("feed-count");
  var searchInput = document.getElementById("search-input");
  var cityFilter = document.getElementById("city-filter");
  var categoryFilter = document.getElementById("category-filter");
  var liveGrid = document.getElementById("live-wall");
  var liveCount = document.getElementById("live-count");
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

  // Only provider-hosted embed URLs are allowed in auto-play players. Never iframe
  // a publisher's whole website or accept arbitrary URLs from catalog data.
  function safeEmbedUrl(value) {
    try {
      var parsed = new URL(String(value || ""));
      if (parsed.protocol !== "https:") return "";
      var host = parsed.hostname.toLowerCase();
      var youtubeHost = host === "youtube.com" || host === "www.youtube.com" ||
        host === "youtube-nocookie.com" || host === "www.youtube-nocookie.com";
      var youtubeParts = parsed.pathname.split("/");
      if (youtubeHost && youtubeParts.length === 3 && youtubeParts[1] === "embed" && /^[A-Za-z0-9_-]{6,}$/.test(youtubeParts[2])) return parsed.href;
      var vimeoParts = parsed.pathname.split("/");
      if (host === "player.vimeo.com" && vimeoParts.length === 3 && vimeoParts[1] === "video" && /^[0-9]+$/.test(vimeoParts[2])) return parsed.href;
      // OpenCCTV publishes an explicit iframe snippet for individual camera wrappers.
      // Keep this allow-list narrow: only canonical India camera detail pages are accepted.
      if (host === "opencctv.org" && !parsed.search && !parsed.hash &&
          /^\/cameras\/india\/[a-z0-9-]+\/[a-z0-9-]+\/[a-z0-9-]+-\d{4,}$/.test(parsed.pathname)) return parsed.href;
      return "";
    } catch (error) {
      return "";
    }
  }

  function isOpenCctvEmbed(value) {
    return safeEmbedUrl(value).indexOf("https://opencctv.org/cameras/india/") === 0;
  }

  function autoplayEmbedUrl(value) {
    var parsed = new URL(safeEmbedUrl(value));
    // OpenCCTV's copied iframe snippet is its full hosted viewer URL. Do not add
    // undocumented query parameters to that URL; its image refresh is source-controlled.
    if (parsed.hostname === "opencctv.org") return parsed.href;
    parsed.searchParams.set("autoplay", "1");
    parsed.searchParams.set("mute", "1");
    parsed.searchParams.set("playsinline", "1");
    if (parsed.hostname.indexOf("youtube") !== -1) parsed.searchParams.set("rel", "0");
    return parsed.href;
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
    var allowedAuthorizationTypes = ["publisher-terms", "written-permission", "explicit-public-license", "publisher-public-page", "publisher-embed-feature"];
    var allowedViewingMethods = ["official-link", "approved-embed", "approved-stream"];
    return items.filter(function (feed) {
      if (!feed || feed.status !== "approved" || !feed.authorization) return false;
      var auth = feed.authorization;
      var authorizationType = auth.authorization_type;
      var method = feed.viewing_method;
      var scope = auth.reuse_scope;
      if (auth.status !== "approved") return false;
      if (!safeHttpsUrl(feed.publisher_url) || !safeHttpsUrl(feed.source_page_url)) return false;
      if (typeof feed.publisher_name !== "string" || typeof feed.attribution !== "string" || !feed.attribution.trim()) return false;
      if (!auth.evidence_reference || typeof auth.terms_review_note !== "string" || auth.terms_review_note.trim().length < 20) return false;
      if (allowedAuthorizationTypes.indexOf(authorizationType) === -1) return false;
      if ((authorizationType === "publisher-terms" || authorizationType === "explicit-public-license") && !safeHttpsUrl(feed.terms_url)) return false;
      if (allowedViewingMethods.indexOf(method) === -1) return false;
      if (["link-only", "embedding", "redistribution"].indexOf(scope) === -1) return false;
      if (method === "approved-embed" && ["embedding", "redistribution"].indexOf(scope) === -1) return false;
      if (method === "approved-stream" && scope !== "redistribution") return false;
      if (method === "approved-embed" && !safeEmbedUrl(feed.viewing_url)) return false;
      if (authorizationType === "publisher-embed-feature" &&
          (method !== "approved-embed" || scope !== "embedding" || !isOpenCctvEmbed(feed.viewing_url) ||
           feed.viewing_url !== feed.source_page_url)) return false;
      if (authorizationType === "publisher-public-page" &&
          (method !== "official-link" || scope !== "link-only" || Object.prototype.hasOwnProperty.call(feed, "viewing_url"))) return false;
      return true;
    }).sort(function (a, b) {
      var nationwideA = String(a.id || "").indexOf("india-") === 0;
      var nationwideB = String(b.id || "").indexOf("india-") === 0;
      if (nationwideA !== nationwideB) return nationwideA ? -1 : 1;
      var cityOrder = String(a.city || "").localeCompare(String(b.city || ""));
      return cityOrder || String(a.name || "").localeCompare(String(b.name || ""));
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
    var publicPageOnly = feed.authorization && feed.authorization.authorization_type === "publisher-public-page";
    var source = publicPageOnly
      ? safeHttpsUrl(feed.source_page_url)
      : safeHttpsUrl(feed.viewing_url) || safeHttpsUrl(feed.source_page_url);
    var linkLabel = publicPageOnly || feed.viewing_method === "official-link" || !safeHttpsUrl(feed.viewing_url)
      ? "Open source"
      : "View camera";
    var locationLabel = area ? city + " · " + area : city;
    return '<article class="feed-card">' +
      '<div class="feed-thumb"><span class="category-pill">' + category + '</span><span class="status-pill">✓ Verified source</span></div>' +
      '<div class="feed-card-body"><h3>' + name + '</h3>' +
      '<div class="feed-meta"><span aria-hidden="true">⌖</span><span>' + locationLabel + '</span></div>' +
      '<p class="feed-description">' + description + '</p>' +
      '<div class="feed-card-bottom"><span class="feed-publisher" title="' + publisher + '">Published by ' + publisher + '</span>' +
      '<span class="feed-actions"><a class="feed-open" href="' + escapeHtml(source) + '" target="_blank" rel="noopener noreferrer">' + linkLabel + ' ↗</a>' +
      '<a class="feed-report" href="https://github.com/vikasdocker/Bharat-Open-Cams/issues/new?template=feed-removal.yml" target="_blank" rel="noopener noreferrer">Report / remove</a></span></div></div></article>';
  }

  function renderLiveWall(items) {
    var liveFeeds = items.filter(function (feed) {
      return feed.viewing_method === "approved-embed" &&
        safeEmbedUrl(feed.viewing_url) &&
        feed.authorization && feed.authorization.status === "approved";
    });
    liveCount.textContent = String(liveFeeds.length);
    liveGrid.setAttribute("aria-busy", "false");

    if (!liveFeeds.length) {
      liveGrid.innerHTML = '<div class="live-empty"><span class="live-empty-icon" aria-hidden="true">↗</span><div><h3>No auto-play stream is verified yet</h3><p>The current catalog contains public source pages and directories, but no individual stream is approved for embedding. Those cameras remain available below and open on their publisher sites. A stream will appear here after its official embed and viewing permission are checked.</p><a class="text-link" href="https://github.com/vikasdocker/Bharat-Open-Cams/blob/main/docs/FEED_POLICY.md" target="_blank" rel="noreferrer">Read the viewing policy ↗</a></div></div>';
      return;
    }

    liveGrid.innerHTML = liveFeeds.map(function (feed, index) {
      var name = escapeHtml(feed.name);
      var city = escapeHtml(feed.city);
      var area = escapeHtml(feed.area || "");
      var publisher = escapeHtml(feed.publisher_name);
      var loading = index < 2 ? "eager" : "lazy";
      var embedUrl = escapeHtml(autoplayEmbedUrl(feed.viewing_url));
      var publisherUrl = escapeHtml(safeHttpsUrl(feed.source_page_url));
      var label = area ? city + " · " + area : city;
      var playerLabel = isOpenCctvEmbed(feed.viewing_url) ? "PUBLIC CAMERA VIEW" : "AUTO-PLAY · MUTED";
      var playbackNote = isOpenCctvEmbed(feed.viewing_url)
        ? "Source-hosted camera viewer; refresh and availability are controlled by OpenCCTV and its upstream source."
        : "Playback and availability are controlled by the publisher.";
      return '<article class="live-card">' +
        '<div class="live-card-heading"><div><span class="live-stream-label"><span class="live-pulse" aria-hidden="true"></span> ' + playerLabel + '</span><h3>' + name + '</h3><p>' + label + ' · Published by ' + publisher + '</p></div></div>' +
        '<div class="live-player"><iframe src="' + embedUrl + '" title="' + name + '" loading="' + loading + '" referrerpolicy="strict-origin-when-cross-origin" allow="autoplay; encrypted-media; picture-in-picture; fullscreen" allowfullscreen></iframe></div>' +
        '<div class="live-card-footer"><span>' + playbackNote + '</span><a href="' + publisherUrl + '" target="_blank" rel="noopener noreferrer">Publisher page ↗</a></div>' +
        '</article>';
    }).join("");
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
      renderLiveWall(feeds);
      render();
    })
    .catch(function () {
      renderLiveWall([]);
      renderEmpty("The catalog could not be loaded", "Please try refreshing the page. The directory will not invent camera listings when its verified catalog is unavailable.", "View project status", "https://github.com/vikasdocker/Bharat-Open-Cams");
      feedGrid.setAttribute("aria-busy", "false");
      countNode.textContent = "—";
    });
})();