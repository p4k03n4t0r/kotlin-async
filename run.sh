#!/usr/bin/env bash
# docker build -t kotlin-async . # --network common .
mode="returned"
cpus=4
docker run -v $(pwd)/output:/app/output -e MAX=10000000000 -e SCALE=1000000000 -e MODES=$mode -e CPUS=$cpus -e CONCURRENT=1 --cpus=$cpus kotlin-async