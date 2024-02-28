# Parameter Control in Evolutionary Algorithms

This project is an exploration of evolutionary algorithms and their behaviour with different parameters.
This project is using the [jenetics](https://github.com/jenetics/jenetics) framework.

## Usage

For this project you need Java 21 and Maven installed. Prepare the project with:

```shell
mvn clean install
```

After that, you can run any of the classes with main-methods. These are:

- Comparison: Can compare different EA with each other and plot their fitness.
- DefaultEARechenberg: Runs and EA with rechenberg mutation.
- Tuning: Calls the DefaultEA with the given parameter and plots the fitness.
