# Large Buffer External Sorting Project

## Overview
This project implements an efficient external sorting algorithm using a large buffer to manage records stored in files. The program sorts records based on calculated intersection probability and provides multiple input options.

## Features
- Generates random records or allows manual input.
- Reads records from an existing file.
- Uses a large buffer for efficient external sorting.
- Displays file contents at various stages.
- Outputs sorted records with optional additional calculations.

## Prerequisites
Ensure you have:
- **Java Development Kit (JDK) 8 or higher**
- **An IDE or terminal for running Java programs**

## Project Structure
```
|-- data/                     # Directory for input/output files
|   |-- tmp/                  # Temporary files used during sorting
|-- src/                      # Source files
|   |-- Main.java             # Main program execution
|   |-- LargeBufferSort.java  # Sorting logic with large buffer
|   |-- Utility.java          # Helper methods for file handling and calculations
|   |-- Record.java           # Record structure with probability fields
|-- README.md                 # Project documentation
```

## Running the Program
1. **Compile the Java files:**
   ```sh
   javac src/*.java -d bin/
   ```
2. **Run the program:**
   ```sh
   java -cp bin Main
   ```
3. **Follow on-screen instructions to:**
   - Generate random records.
   - Manually enter records.
   - Load records from a file.
   - Sort records using an external sorting algorithm.
   - Display or save intermediate results.

## Sorting Algorithm
The program implements **two-phase multi-way external merge sort**:
1. **Initial Run Formation**: Reads records into memory, sorts them, and writes them to temporary files.
2. **Multi-way Merging**: Merges sorted runs in multiple phases until a single sorted file remains.

## Configuration Parameters
You can modify sorting behavior by adjusting these constants in `LargeBufferSort.java`:
- `BUFFER_SIZE`: Number of records per buffer (default: **250**)
- `NUM_BUFFERS`: Number of buffers used (default: **20**)
- `BLOCK_SIZE`: Number of records processed per phase (`BUFFER_SIZE * NUM_BUFFERS`)

## Sample Input Format
Each line in the input file should contain **three space-separated numbers** representing:
```
probA probB probUnion
```
Example:
```
0.3 0.5 0.7
0.2 0.4 0.6
```

## Output Files
- `data/input_<recordCount>_sorted.txt`: Sorted records based on intersection probability.
- `data/input_<recordCount>_sorted_with_intersection.txt`: Sorted records with additional computed intersection probability.

## Additional Features
- **Display intermediate files**: View contents after each sorting phase.
- **Save intermediate files**: Store intermediate sorting stages in separate files.
- **Error handling**: Ensures file existence and handles invalid inputs gracefully.

## Future Enhancements
- Implement parallel processing for faster sorting.
- Optimize buffer size dynamically based on system memory.
- Support for additional file formats (CSV, JSON).



