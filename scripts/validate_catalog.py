"""Validate the project feed catalog and enforce publication authorization gates."""
import json
import sys
from pathlib import Path

from jsonschema import Draft202012Validator, FormatChecker

ROOT = Path(__file__).resolve().parents[1]
SCHEMA_PATH = ROOT / "data" / "feeds.schema.json"
CATALOG_PATHS = [ROOT / "data" / "feeds.json", ROOT / "data" / "feeds.example.json"]


def read_json(path: Path):
    try:
        return json.loads(path.read_text(encoding="utf-8"))
    except (OSError, json.JSONDecodeError) as exc:
        raise ValueError(f"{path.relative_to(ROOT)}: {exc}") from exc


def main() -> int:
    schema = read_json(SCHEMA_PATH)
    Draft202012Validator.check_schema(schema)
    validator = Draft202012Validator(schema, format_checker=FormatChecker())
    error_count = 0

    for path in CATALOG_PATHS:
        data = read_json(path)
        errors = sorted(validator.iter_errors(data), key=lambda item: list(item.absolute_path))
        for error in errors:
            location = "$" + "".join(f"[{part}]" if isinstance(part, int) else f".{part}" for part in error.absolute_path)
            print(f"ERROR {path.relative_to(ROOT)} {location}: {error.message}")
            error_count += 1

        if not isinstance(data, list):
            continue
        seen_ids = set()
        for index, feed in enumerate(data):
            if not isinstance(feed, dict):
                continue
            feed_id = feed.get("id")
            if feed_id in seen_ids:
                print(f"ERROR {path.relative_to(ROOT)}[{index}].id: duplicate id {feed_id!r}")
                error_count += 1
            if feed_id:
                seen_ids.add(feed_id)

            if feed.get("status") == "approved":
                auth = feed.get("authorization") or {}
                if auth.get("status") != "approved":
                    print(f"ERROR {path.relative_to(ROOT)}[{index}]: approved feed lacks approved authorization status")
                    error_count += 1
                if not auth.get("evidence_reference") or not feed.get("terms_url"):
                    print(f"ERROR {path.relative_to(ROOT)}[{index}]: approved feed lacks authorization/terms evidence")
                    error_count += 1
                if not feed.get("publisher_name") or not feed.get("attribution"):
                    print(f"ERROR {path.relative_to(ROOT)}[{index}]: approved feed lacks publisher attribution")
                    error_count += 1
                method = feed.get("viewing_method")
                scope = auth.get("reuse_scope")
                if method == "approved-embed" and scope not in ("embedding", "redistribution"):
                    print(f"ERROR {path.relative_to(ROOT)}[{index}]: embedding requires embedding or redistribution permission")
                    error_count += 1
                if method == "approved-stream" and scope != "redistribution":
                    print(f"ERROR {path.relative_to(ROOT)}[{index}]: direct stream use requires explicit redistribution permission")
                    error_count += 1

    if error_count:
        print(f"Catalog validation failed with {error_count} error(s).")
        return 1
    print("Catalog validation passed. All listed feeds satisfy schema and approval gates.")
    return 0


if __name__ == "__main__":
    sys.exit(main())