import copy
import json
import unittest
from pathlib import Path

from jsonschema import Draft202012Validator, FormatChecker

ROOT = Path(__file__).resolve().parents[1]
SCHEMA = json.loads((ROOT / "data" / "feeds.schema.json").read_text(encoding="utf-8"))
VALIDATOR = Draft202012Validator(SCHEMA, format_checker=FormatChecker())

VALID_APPROVED_FEED = {
    "id": "sample-public-webcam",
    "name": "Official city viewpoint",
    "description": "An intentionally public camera page published by the city authority.",
    "publisher_name": "Example City Authority",
    "publisher_url": "https://city.example.gov/",
    "source_page_url": "https://city.example.gov/public-camera",
    "terms_url": "https://city.example.gov/terms",
    "city": "Example City",
    "area": "Central viewpoint",
    "category": "tourism",
    "viewing_method": "official-link",
    "authorization": {
        "status": "approved",
        "authorization_type": "publisher-terms",
        "reuse_scope": "link-only",
        "evidence_reference": "https://city.example.gov/terms",
        "reviewer": "maintainer"
    },
    "attribution": "Source: Example City Authority",
    "removal_contact_url": "https://city.example.gov/contact",
    "status": "approved",
    "verified_at": "2026-10-12",
    "review_due_at": "2026-11-12"
}


class CatalogSchemaTests(unittest.TestCase):
    def errors(self, entry):
        return list(VALIDATOR.iter_errors([entry]))

    def test_empty_catalog_is_valid(self):
        self.assertEqual(list(VALIDATOR.iter_errors([])), [])

    def test_verified_public_source_fixture_is_valid(self):
        self.assertEqual(self.errors(VALID_APPROVED_FEED), [])

    def test_approved_feed_requires_approved_authorization(self):
        feed = copy.deepcopy(VALID_APPROVED_FEED)
        feed["authorization"]["status"] = "pending"
        self.assertTrue(self.errors(feed), "An approved listing must have approved authorization evidence.")

    def test_non_https_publisher_is_rejected(self):
        feed = copy.deepcopy(VALID_APPROVED_FEED)
        feed["publisher_url"] = "http://city.example.gov/"
        self.assertTrue(self.errors(feed), "Publisher URLs must use HTTPS.")

    def test_terms_url_is_required(self):
        feed = copy.deepcopy(VALID_APPROVED_FEED)
        del feed["terms_url"]
        self.assertTrue(self.errors(feed), "A listing must record the applicable terms.")

 
    def test_embedding_requires_embedding_or_redistribution_permission(self):
        feed = copy.deepcopy(VALID_APPROVED_FEED)
        feed["viewing_method"] = "approved-embed"
        self.assertTrue(self.errors(feed), "Link-only permission must not be used for an embedded player.")
        feed["authorization"]["reuse_scope"] = "embedding"
        self.assertEqual(self.errors(feed), [])

    def test_direct_stream_requires_redistribution_permission(self):
        feed = copy.deepcopy(VALID_APPROVED_FEED)
        feed["viewing_method"] = "approved-stream"
        self.assertTrue(self.errors(feed), "A direct stream must not be used under link-only permission.")
        feed["authorization"]["reuse_scope"] = "redistribution"
        self.assertEqual(self.errors(feed), [])

    def test_unknown_publication_status_is_rejected(self):
        feed = copy.deepcopy(VALID_APPROVED_FEED)
        feed["status"] = "online"
        self.assertTrue(self.errors(feed), "A feed must use a controlled review status.")


if __name__ == "__main__":
    unittest.main()
