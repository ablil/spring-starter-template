#!/usr/bin/env python3
"""
Composite action helper: read a k6 JSON summary and print Markdown to stdout.

Typical usage (from action.yml):
  python run.py path/to/summary.json --title "k6 load test summary" >> "$GITHUB_STEP_SUMMARY"
"""

from __future__ import annotations

import argparse
import json
import math
from pathlib import Path
from typing import Any, Dict


def get(d: Dict[str, Any], *keys: str, default=None):
    cur: Any = d
    for k in keys:
        if not isinstance(cur, dict) or k not in cur:
            return default
        cur = cur[k]
    return cur


def fmt_num(x: Any, digits: int = 2) -> str:
    if x is None:
        return "—"
    if isinstance(x, bool):
        return str(x)
    if isinstance(x, (int, float)):
        if isinstance(x, float) and (math.isnan(x) or math.isinf(x)):
            return "—"
        if isinstance(x, int) or float(x).is_integer():
            return f"{int(x)}"
        return f"{x:.{digits}f}"
    return str(x)


def fmt_ms(seconds: Any) -> str:
    if seconds is None:
        return "—"
    try:
        ms = float(seconds) * 1000.0
    except (TypeError, ValueError):
        return "—"
    if math.isnan(ms) or math.isinf(ms):
        return "—"
    if ms < 1000:
        return f"{ms:.0f} ms"
    return f"{ms/1000.0:.2f} s"


def metric(summary: Dict[str, Any], name: str) -> Dict[str, Any]:
    return get(summary, "metrics", name, default={}) or {}


def pick_status_metrics(metrics: Dict[str, Any]) -> Dict[str, Dict[str, Any]]:
    out: Dict[str, Dict[str, Any]] = {}
    for k, v in metrics.items():
        if not isinstance(k, str) or not k.startswith("http_reqs{status:"):
            continue
        status = k.removeprefix("http_reqs{status:").removesuffix("}")
        if isinstance(v, dict):
            out[status] = v
    return out


def build_markdown(summary: Dict[str, Any], title: str) -> str:
    metrics = summary.get("metrics", {}) if isinstance(summary.get("metrics"), dict) else {}

    http_reqs = metric(summary, "http_reqs")
    iterations = metric(summary, "iterations")
    vus = metric(summary, "vus")
    vus_max = metric(summary, "vus_max")

    http_req_failed = metric(summary, "http_req_failed")
    checks = metric(summary, "checks")
    duration = metric(summary, "http_req_duration")
    waiting = metric(summary, "http_req_waiting")

    data_sent = metric(summary, "data_sent")
    data_received = metric(summary, "data_received")

    status_metrics = pick_status_metrics(metrics)

    lines: list[str] = []
    lines.append(f"## {title}")
    lines.append("")
    lines.append("### Overview")
    lines.append("")
    lines.append("| Metric | Value |")
    lines.append("|---|---:|")
    lines.append(f"| Requests | {fmt_num(get(http_reqs, 'count'))} |")
    lines.append(f"| Request rate | {fmt_num(get(http_reqs, 'rate'))} /s |")
    lines.append(f"| Iterations | {fmt_num(get(iterations, 'count'))} |")
    lines.append(f"| Iteration rate | {fmt_num(get(iterations, 'rate'))} /s |")
    lines.append(f"| VUs (current) | {fmt_num(get(vus, 'value'))} |")
    lines.append(f"| VUs (max) | {fmt_num(get(vus_max, 'value', default=get(vus, 'max')))} |")
    lines.append("")

    fail_val = get(http_req_failed, "value")
    fail_pct = None
    try:
        if fail_val is not None:
            fail_pct = float(fail_val) * 100.0
    except (TypeError, ValueError):
        fail_pct = None

    lines.append("### Quality gates")
    lines.append("")
    lines.append("| Gate | Result |")
    lines.append("|---|---:|")
    lines.append(f"| HTTP request failure rate | {fmt_num(fail_pct)}% |")
    lines.append(f"| Checks passed | {fmt_num(get(checks, 'passes'))} |")
    lines.append(f"| Checks failed | {fmt_num(get(checks, 'fails'))} |")
    lines.append("")

    lines.append("### Latency")
    lines.append("")
    lines.append("| Metric | Avg | p(90) | p(95) | Median | Min | Max |")
    lines.append("|---|---:|---:|---:|---:|---:|---:|")
    lines.append(
        "| http_req_duration | "
        f"{fmt_ms(get(duration, 'avg'))} | {fmt_ms(get(duration, 'p(90)'))} | {fmt_ms(get(duration, 'p(95)'))} | "
        f"{fmt_ms(get(duration, 'med'))} | {fmt_ms(get(duration, 'min'))} | {fmt_ms(get(duration, 'max'))} |"
    )
    lines.append(
        "| http_req_waiting | "
        f"{fmt_ms(get(waiting, 'avg'))} | {fmt_ms(get(waiting, 'p(90)'))} | {fmt_ms(get(waiting, 'p(95)'))} | "
        f"{fmt_ms(get(waiting, 'med'))} | {fmt_ms(get(waiting, 'min'))} | {fmt_ms(get(waiting, 'max'))} |"
    )
    lines.append("")

    lines.append("### Network")
    lines.append("")
    lines.append("| Metric | Total | Rate |")
    lines.append("|---|---:|---:|")
    lines.append(f"| Data sent | {fmt_num(get(data_sent, 'count'))} bytes | {fmt_num(get(data_sent, 'rate'))} B/s |")
    lines.append(f"| Data received | {fmt_num(get(data_received, 'count'))} bytes | {fmt_num(get(data_received, 'rate'))} B/s |")
    lines.append("")

    if status_metrics:
        lines.append("### HTTP status breakdown (by request count)")
        lines.append("")
        lines.append("| Status | Count | Rate (/s) | Thresholds |")
        lines.append("|---:|---:|---:|---|")
        for status in sorted(status_metrics.keys(), key=lambda s: int(s) if s.isdigit() else s):
            sm = status_metrics[status]
            thresholds = sm.get("thresholds")
            thresh_str = "—"
            if isinstance(thresholds, dict) and thresholds:
                parts = [f"`{expr}`: {ok}" for expr, ok in thresholds.items()]
                thresh_str = ", ".join(parts)
            lines.append(f"| {status} | {fmt_num(get(sm, 'count'))} | {fmt_num(get(sm, 'rate'))} | {thresh_str} |")
        lines.append("")

    root_checks = get(summary, "root_group", "checks", default={})
    if isinstance(root_checks, dict) and root_checks:
        lines.append("### Checks (root group)")
        lines.append("")
        lines.append("| Check | Passes | Fails |")
        lines.append("|---|---:|---:|")
        for check_name, c in root_checks.items():
            if not isinstance(c, dict):
                continue
            lines.append(f"| {check_name} | {fmt_num(c.get('passes'))} | {fmt_num(c.get('fails'))} |")
        lines.append("")

    return "\n".join(lines).rstrip() + "\n"


def main() -> int:
    ap = argparse.ArgumentParser()
    ap.add_argument("summary_json", type=Path, help="Path to k6 summary.json")
    ap.add_argument("--title", default="k6 load test summary", help="Markdown header title")
    args = ap.parse_args()

    summary = json.loads(args.summary_json.read_text(encoding="utf-8"))
    print(build_markdown(summary, title=args.title), end="")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())