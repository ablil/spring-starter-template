#!/usr/bin/env python3

import os
import sys
import xml.etree.ElementTree as ET

def parse_junit_xml(file_path):
    """
    Parses a JUnit XML file and extracts test suite data.

    Args:
        file_path (str): Path to the JUnit XML file.

    Returns:
        list of dict: A list of dictionaries, each representing a test case.
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
    Converts test case data into Markdown format using a table.

    Args:
        test_cases (list of dict): List of test case data.

    Returns:
        str: A string in Markdown format.
    """
    #markdown = f"\n\n**{title}**\n\n"
    testClass = test_cases[0]['classname']
    hasAnyFailure = any(map(lambda testcase: testcase['status'] != 'PASSED', test_cases))
    markdown = f"<details><summary>{testClass} {'❌' if hasAnyFailure else '✅'}</summary>\n\n"
    markdown += "| Test Name |  Time (s) | Status | Message |\n"
    markdown += "|-----------|---------|--------|---------|\n"

    for case in test_cases:
        message = case.get('message', '')
        markdown += f"| {case['name']} | {case['time']} | {'✅' if case['status'] == 'PASSED' else '❌'} | {message} |\n"

    markdown += "\n\n</details>"
    return markdown

def convert_file(input_files, output_file):
    """
    Converts a JUnit XML file to a Markdown file.

    Args:
        input_file (list(str)): Path to the JUnit XML files.
        output_file (str): Path to the output Markdown file.
    """
    with open(output_file, 'w') as md_file:
        md_file.write("# Job summary\n\n")
        md_file.write("## Test results\n\n")
        for input_file in input_files:
            test_cases = parse_junit_xml(input_file)
            markdown = convert_to_markdown(test_cases, input_file.split('/')[-1])
            md_file.write(markdown)
        md_file.write("<hr />") # horizontal line


if __name__ == '__main__':
    base_path = sys.argv[1] if len(sys.argv) > 1 else 'build/test-results/test'
    assert os.path.exists(base_path), f"{base_path} NOT found"

    filenames = [f"{base_path}/{filename}" for filename in os.listdir(base_path) if filename.endswith('.xml')]
    convert_file(filenames, 'output.md')
