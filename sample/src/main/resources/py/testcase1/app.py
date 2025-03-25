import os
import random

def random_walk(steps, seed=None):
    if seed is not None:
        random.seed(seed)
    current_value = 0
    for _ in range(steps):
        step = random.choice([-1, 1])
        current_value += step
    print(current_value)  # Только конечное значение!

steps = int(os.getenv('STEPS', '10'))
seed = os.getenv('SEED', None)
random_walk(steps, seed)