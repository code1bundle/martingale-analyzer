import os
import random
import math

def simulate_stock_price(initial_price, volatility, drift, steps, seed=None):
    if seed is not None:
        random.seed(seed)

    price = initial_price
    for _ in range(steps):
        change_percent = drift + volatility * random.gauss(0, 1)
        price *= (1 + change_percent)
    print(f"{price:.8f}") # Только конечная цена, с достаточной точностью

initial_price = float(os.getenv('INITIAL_PRICE', '100'))
volatility = float(os.getenv('VOLATILITY', '0.01'))
drift = float(os.getenv('DRIFT', '0.0001'))
steps = int(os.getenv('STEPS', '10'))
seed = os.getenv('SEED', None)
simulate_stock_price(initial_price, volatility, drift, steps, seed)