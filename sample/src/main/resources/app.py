import os

# Read inputs from environment variables
a = int(os.getenv('A', '0'))
b = int(os.getenv('B', '0'))

# Perform computation and output the result
result = a + b
print(result)