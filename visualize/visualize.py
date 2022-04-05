import csv
import matplotlib.pyplot as plt
import numpy as np
import sys
from pyparsing import line


results = {}

if len(sys.argv) > 1:
    f = sys.argv[1]
else:
    f = "../output/output.csv"

with open(f) as csv_file:
    csv_reader = csv.reader(csv_file)
    for x, name, time, total_count in csv_reader:
        if x not in results:
            results[x] = []
        results[x].append({"name":name,"time":time,"total_count":total_count})

x_values = []
lines = {}
errors = []

for x in results:
    x_value = f"{int(int(x) / 100000000)}"
    x_values.append(x_value)
    for result in results[x]:
        name = result["name"]
        if name not in lines:
            lines[name] = []
        lines[name].append(int(result["time"]))

        if result["total_count"] != x:
            errors.append(name)

for line_name in lines:
    if line_name in errors and "Isolated" not in line_name:
        print(f"Ignoring {line_name} since it wasn't thread safe")
        continue
    plt.plot(x_values, lines[line_name], label=line_name)

plt.xlabel("Work done (*10^9)")
plt.ylabel("Time took (ms)")
plt.legend(loc="upper left")
plt.show()