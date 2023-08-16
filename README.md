# TourGuide
[![build](https://github.com/NextSap/JAVA.oc-8/actions/workflows/ci.yml/badge.svg)](https://github.com/NextSap/JAVA.oc-8/actions/workflows/ci.yml)

TourGuide est une application de guidage touristique.

## Benchmarks
### LocationBenchmark
![LocationBenchmark.png](.readme%2FLocationBenchmark.png)
### RewardsBenchmark
![RewardsBenchmark.png](.readme%2FRewardsBenchmark.png)

## Installation
### Prérequis
- Java 17
- Maven 3.6.3

### Lancement
```bash
mvn clean install -DskipTests
java -jar target/tourguide-0.0.1.jar
```