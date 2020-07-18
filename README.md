# Probit and Percentage Conversion Program
Uses the (Finney and Stevens 1948) function to calculate percentages from a probit and uses this to make a table that can be used to find a probit from a percentage with fairly accurate results

Citation: Finney, D. J., and W. L. Stevens. ?A Table for the Calculation of Working Probits and Weights in Probit Analysis.? Biometrika, vol. 35, no. 1/2, 1948, pp. 191?201. JSTOR, www.jstor.org/stable/2332639. Accessed 22 Jan. 2020.

------------------------------------------------------------------------------------------------------------------------------------------

This program has three functions
# Create table
Uses the percentages function to calculate percentages for probits from 0 - 9 in increments of 0.01
It will take about an hour to finish depending on your CPU and writes the outputs to the data.txt file

# Find percentages
Uses the function in (Finney and Stevens 1948) to calculate a percentage given a probit

# Find probits
Uses the data.txt file to create a lookup table that can be used to find a the closest probit given a percentage
