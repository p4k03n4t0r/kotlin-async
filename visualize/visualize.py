import csv
import matplotlib.pyplot as plt
import numpy as np

filter="Returned"

results = {}

with open('../output.csv') as csv_file:
    csv_reader = csv.reader(csv_file)
    for x, name, time, total_count in csv_reader:
        if x not in results:
            results[x] = []
        results[x].append({"name":name,"time":time,"total_count":total_count})

x_values = []
lines = {}

for x in results:
    x_value = f"{(int(x) / 100000000)}*10^9"
    x_values.append(x_value)
    for result in results[x]:
        name = result["name"]
        if filter not in name:
            continue
        if name not in lines:
            lines[name] = []
        lines[name].append(int(result["time"]))

for line_name in lines:
    plt.plot(x_values, lines[line_name], label=line_name)

plt.ylabel("ms")
plt.legend(loc="upper left")
plt.show()