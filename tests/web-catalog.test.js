const assert = require("node:assert/strict");
const fs = require("node:fs");
const path = require("node:path");
const vm = require("node:vm");

const appSource = fs.readFileSync(path.join(__dirname, "..", "app.js"), "utf8");

function makeElement() {
  return {
    value: "",
    textContent: "",
    innerHTML: "",
    attributes: {},
    handlers: {},
    children: [],
    setAttribute(name, value) { this.attributes[name] = value; },
    addEventListener(name, handler) { this.handlers[name] = handler; },
    appendChild(child) { this.children.push(child); }
  };
}

function approvedPage(id, overrides = {}) {
  return {
    id,
    name: "Chennai weather station webcam",
    description: "Public outdoor weather station view",
    publisher_name: "KeaWeather",
    publisher_url: "https://kwschennai.com/",
    source_page_url: "https://kwschennai.com/keacam.htm",
    city: "Chennai",
    area: "Nungambakkam",
    category: "weather",
    viewing_method: "official-link",
    authorization: {
      status: "approved",
      authorization_type: "publisher-public-page",
      reuse_scope: "link-only",
      evidence_reference: "https://kwschennai.com/keacam.htm",
      terms_review_note: "Public camera page reviewed; external link only."
    },
    attribution: "Source: KeaWeather",
    status: "approved",
    verified_at: "2026-10-12",
    review_due_at: "2026-11-12",
    ...overrides
  };
}

async function launchWith(catalog) {
  const ids = [
    "feed-grid", "feed-count", "search-input",
    "city-filter", "category-filter", "directory-note", "current-year",
    "live-wall", "live-count"
  ];
  const elements = Object.fromEntries(ids.map(id => [id, makeElement()]));
  const document = {
    getElementById: id => elements[id] || (elements[id] = makeElement()),
    createElement: tagName => Object.assign(makeElement(), { tagName })
  };
  const context = {
    document,
    URL,
    fetch: async () => ({ ok: true, json: async () => catalog }),
    console,
    Set,
    Array,
    Date,
    String,
    Promise
  };
  vm.runInNewContext(appSource, context, { filename: "app.js" });
  // Wait for the mocked fetch and its rendering continuation.
  await new Promise(resolve => setImmediate(resolve));
  return elements;
}

async function main() {
  {
    const good = approvedPage("kea-chennai");
    const pending = approvedPage("private-unapproved", {
      name: "Private residential camera",
      status: "pending-review"
    });
    const missingReview = approvedPage("missing-note", {
      authorization: {
        status: "approved",
        authorization_type: "publisher-public-page",
        reuse_scope: "link-only",
        evidence_reference: "https://kwschennai.com/keacam.htm"
      }
    });
    const nationwideHub = approvedPage("india-wide-camera-map", {
      name: "India-wide public camera map",
      city: "India",
      category: "traffic"
    });
    const directMediaUrlOnPublicPage = approvedPage("bad-direct-url", {
      viewing_url: "https://kwschennai.com/not-approved-image.jpg"
    });
    const embeddedWithoutPermission = approvedPage("bad-embed", {
      viewing_method: "approved-embed",
      authorization: {
        status: "approved",
        authorization_type: "publisher-public-page",
        reuse_scope: "link-only",
        evidence_reference: "https://kwschennai.com/keacam.htm",
        terms_review_note: "Publisher page reviewed; this is only a public link."
      }
    });
    const elements = await launchWith([good, pending, missingReview, embeddedWithoutPermission, directMediaUrlOnPublicPage, nationwideHub]);
    assert.match(elements["feed-grid"].innerHTML, /Chennai weather station webcam/);
    assert.match(elements["feed-grid"].innerHTML, /India-wide public camera map/);
    assert.ok(elements["feed-grid"].innerHTML.indexOf("India-wide public camera map") < elements["feed-grid"].innerHTML.indexOf("Chennai weather station webcam"));
    assert.match(elements["feed-grid"].innerHTML, /https:\/\/kwschennai\.com\/keacam\.htm/);
    assert.doesNotMatch(elements["feed-grid"].innerHTML, /Private residential camera/);
    assert.doesNotMatch(elements["feed-grid"].innerHTML, /not-approved-image\.jpg/);
    assert.equal(elements["feed-count"].textContent, "2");
    assert.ok(elements["feed-grid"].innerHTML.includes("template=feed-removal.yml"));
    assert.equal(elements["feed-grid"].attributes["aria-busy"], "false");

    elements["search-input"].value = "Delhi";
    elements["search-input"].handlers.input();
    assert.match(elements["feed-grid"].innerHTML, /No matching public views/);
    assert.doesNotMatch(elements["feed-grid"].innerHTML, /Chennai weather station webcam/);
  }

  {
    const embeddable = approvedPage("official-live-stream", {
      id: "official-live-stream",
      name: "Official public live stream",
      publisher_name: "Example broadcaster",
      publisher_url: "https://example.org/",
      source_page_url: "https://example.org/live",
      viewing_method: "approved-embed",
      viewing_url: "https://www.youtube.com/embed/AbCdEf12345",
      terms_url: "https://www.youtube.com/t/terms",
      authorization: {
        status: "approved",
        authorization_type: "publisher-terms",
        reuse_scope: "embedding",
        evidence_reference: "https://example.org/live",
        terms_review_note: "The publisher page intentionally displays this stream using the provider-hosted embedded player."
      }
    });
    const unsafeEmbed = approvedPage("untrusted-embed", {
      name: "Untrusted embed",
      viewing_method: "approved-embed",
      viewing_url: "https://unknown-camera.example/stream",
      terms_url: "https://example.org/terms",
      authorization: {
        status: "approved",
        authorization_type: "publisher-terms",
        reuse_scope: "embedding",
        evidence_reference: "https://example.org/live",
        terms_review_note: "This entry should fail the provider allow-list and never create an iframe."
      }
    });
    const elements = await launchWith([embeddable, unsafeEmbed]);
    assert.equal(elements["live-count"].textContent, "1");
    assert.match(elements["live-wall"].innerHTML, /Official public live stream/);
    assert.match(elements["live-wall"].innerHTML, /autoplay=1/);
    assert.match(elements["live-wall"].innerHTML, /mute=1/);
    assert.match(elements["live-wall"].innerHTML, /allow="autoplay;/);
    assert.doesNotMatch(elements["live-wall"].innerHTML, /unknown-camera/);
    assert.doesNotMatch(elements["live-wall"].innerHTML, /Untrusted embed/);
  }

  {
    const elements = await launchWith({ not: "an array" });
    assert.match(elements["feed-grid"].innerHTML, /The catalog could not be loaded/);
    assert.equal(elements["feed-grid"].attributes["aria-busy"], "false");
    assert.equal(elements["feed-count"].textContent, "—");
  }

  process.stdout.write("Web catalog tests passed: approval filters, provider allow-list, muted autoplay, link-only boundary, search, and failure state.\n");
}

main().catch(error => {
  console.error(error);
  process.exitCode = 1;
});
