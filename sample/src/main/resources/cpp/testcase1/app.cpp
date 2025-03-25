// script.cpp
#include <iostream>
#include <random>
#include <cstdlib>

int main() {
    int steps = std::atoi(std::getenv("STEPS"));
    int seed = 0;
    const char* seed_env = std::getenv("SEED");
    if(seed_env) {
        seed = std::atoi(seed_env);
    }


    std::mt19937 generator(seed ? seed : time(0)); // Mersenne Twister engine
    std::uniform_int_distribution<> distribution(-1, 1); // Равномерное распределение [-1, 1]

    int current_value = 0;
    for (int i = 0; i < steps; ++i) {
        int step = distribution(generator);
        current_value += step;
    }

    std::cout << current_value << std::endl;
    return 0;
}