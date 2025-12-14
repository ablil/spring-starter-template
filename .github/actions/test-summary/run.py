#!/usr/bin/env python3

import os
import sys
import argparse
import xml.etree.ElementTree as ET


def parse_junit_xml(file_path):
    """
    Parses a JUnit XML file and extracts test suite data.
    """
    test_cases = []
    tree = ET.parse(file_path)
    root = tree.getroot()

    for testcase in root.iter('testcase'):
        case = {
            'name': testcase.get('name'),
            'classname': testcase.get('classname'),
            'time': testcase.get('time'),
            'status': 'PASSED'
        }

        failure = testcase.find('failure')
        error = testcase.find('error')
        if failure is not None:
            case['status'] = 'FAILED'
            case['message'] = failure.get('message')
        elif error is not None:
            case['status'] = 'ERROR'
            case['message'] = error.get('message')

        test_cases.append(case)

    return test_cases


def convert_to_markdown(test_cases, title):
    """
    Converts test case data into Markdown format using a collapsible section
    and a bullet list (instead of a Markdown table).
    """
    # ... existing code ...
    if not test_cases:
        return f"<details><summary>{title} ⚠️ (no testcases found)</summary>\n\n</details>"

    testClass = test_cases[0].get('classname') or title
    hasAnyFailure = any(map(lambda testcase: testcase['status'] != 'PASSED', test_cases))

    total = len(test_cases)
    passed = sum(1 for tc in test_cases if tc.get("status") == "PASSED")
    failed = total - passed

    markdown = (
        f"<details><summary>{testClass} "
        f"{'❌' if hasAnyFailure else '✅'} "
        f"({passed} passed, {failed} failed)</summary>\n\n"
    )

    markdown += "<ul>\n"
    for case in test_cases:
        name = case.get("name", "(unnamed test)")
        time_s = case.get("time", "")
        status = case.get("status", "")
        icon = "✅" if status == "PASSED" else "❌"
        message = (case.get("message") or "").strip()

        line = f"<li><b>{icon} {name}</b>"
        if time_s != "":
            line += f" <i>({time_s}s)</i>"

        if message:
            wrapped = message.replace("\r\n", "\n").replace("\r", "\n").replace("\n", "<br/>")
            line += (
                "<details><summary>message</summary>\n\n"
                f"{wrapped}\n\n"
                "</details>"
            )

        line += "</li>\n"
        markdown += line

    markdown += "</ul>\n\n</details>"
    return markdown
    # ... existing code ...


def convert_files(input_files, output_file, heading=None):
    """
    Converts multiple JUnit XML files to a single Markdown file (appending).
    """
    with open(output_file, 'a') as md_file:
        if heading:
            md_file.write(f"\n\n## {heading}\n\n")

        for input_file in input_files:
            test_cases = parse_junit_xml(input_file)
            title = os.path.basename(input_file)
            markdown = convert_to_markdown(test_cases, title)
            md_file.write(markdown)


def list_xml_files(base_path):
    return [
        os.path.join(base_path, filename)
        for filename in os.listdir(base_path)
        if filename.endswith('.xml')
    ]


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description="Convert JUnit XML reports to GitHub step summary Markdown.")
    parser.add_argument(
        "paths",
        nargs="*",
        help="One or more directories containing JUnit XML files (e.g. build/test-results/test).",
    )
    parser.add_argument(
        "--output",
        default="output.md",
        help="Output Markdown file path (default: output.md).",
    )
    args = parser.parse_args()

    paths = args.paths or ['build/test-results/test']

    # Start fresh each run
    with open(args.output, 'w') as md_file:
        md_file.write("# Job summary\n\n")
        md_file.write("## Test results\n\n")

    any_found = False
    for base_path in paths:
        if not os.path.exists(base_path):
            print(f"{base_path} NOT found, skipping")
            continue

        xml_files = list_xml_files(base_path)
        if not xml_files:
            print(f"No .xml files found in {base_path}, skipping")
            continue

        any_found = True
        convert_files(xml_files, args.output, heading=base_path)

    with open(args.output, 'a') as md_file:
        md_file.write("\n<hr />\n")

    if not any_found:
        # Keep output.md valid even if nothing was found
        print("No test reports found in any provided directory.")