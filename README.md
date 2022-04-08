# kotlin-async

Blog post at: https://p4k03n4t0r.github.io/2022/04/07/kotlin-deconstructed-threads-and-coroutines.html

## How to use

Run `run.sh` to build a Docker container and execute the program in an isolated environment. The following configuration can be changed in the script using environment variables:

* **CONCURRENT**: Amount of concurrent threads that are run.
* **MAX**: Maximum amount of work to do.
* **SCALE**: Scale of which the work will increase, the amount of measurement that will be done can be calculated by dividing the max by the scale.
* **MODES**: Only specific modes can be run by providing a comma seperated list. Possible values: isolated, returned, shared. If not provided, all modes will be tested.
* **OUTPUT_FILE**: The name of the output file to which the results must be ridden. The output is in the CSV format. If not specified the name of the output file will be `output.csv`.

If the Docker container has succesfully run, the result will be placed in the `output` folder.

The results can be visualized by running the `visualize.py` script in the `visualize` folder. Before the first use run `pip install -r requirements.txt` to install the necessary dependencies. If no arguments are provided when running the script, the default output location and file will be visualized.