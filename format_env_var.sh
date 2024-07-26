#!/bin/bash

# Get the environment variable
input_var="$1"

# Initialize the output variable
output_var=""

# Loop through each character in the input variable and add a space
for (( i=0; i<${#input_var}; i++ )); do
  output_var+="${input_var:$i:1} "
done

# Remove the trailing space
output_var=${output_var% }

# Print the formatted variable
echo "$output_var"