#!/usr/bin/env bash
docker build -t kotlin-async .
cpus=4
docker run -v $(pwd)/output:/app/output -e MAX=10000000000 -e SCALE=1000000000 -e MODES="shared" -e CPUS=$cpus --cpus=$cpus kotlin-async