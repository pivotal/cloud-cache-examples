#!/usr/bin/env python3

from jinja2 import Environment, FileSystemLoader
import yaml

env = Environment(
    loader=FileSystemLoader('.')
)

template = env.get_template('./jobs-template.yml')
variable_data = yaml.safe_load(open('./task-parameters.yml'))

print(template.render(variable_data))