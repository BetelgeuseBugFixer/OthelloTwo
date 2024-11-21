import sys

import matplotlib.pyplot as plt
import pandas as pd


def load_data(file_path):
    """
    Load and process data from the file.
    """
    data = pd.read_csv(file_path, sep="\t", header=None, names=["Category", "Generation", "Average Points"])
    return data


def plot_file(ax, data, title):
    """
    Plot data on the given Axes object.
    """
    for category in data["Category"].unique():
        subset = data[data["Category"] == category]
        ax.plot(subset["Generation"], subset["Average Points"], label=category, marker='o')

    ax.set_title(title)
    ax.set_xlabel("Generation")
    ax.set_ylabel("Average Points")
    ax.legend()
    ax.grid(True)


def main(against_all_file, against_best_file):
    """
    Main function to plot the two files.
    """
    # Load data from both files
    against_all_data = load_data(against_all_file)
    against_best_data = load_data(against_best_file)

    # Create subplots
    fig, axes = plt.subplots(1, 2, figsize=(12, 6), sharey=True)

    # Plot the data
    plot_file(axes[0], against_all_data, "against all")
    plot_file(axes[1], against_best_data, "against best")

    # Adjust layout and show the plots
    plt.tight_layout()
    plt.savefig("benchmarks.png")


if __name__ == "__main__":
    against_all_file = "benchMarkAgainstAll.tsv"
    against_best_file = "benchMarkAgainstBest.tsv"
    if len(sys.argv) > 2:
        against_all_file = sys.argv[0]
        against_best_file = sys.argv[2]

    main(against_all_file, against_best_file)
