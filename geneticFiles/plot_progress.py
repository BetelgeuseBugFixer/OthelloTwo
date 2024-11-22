import sys

import matplotlib.pyplot as plt
import pandas as pd
from scipy.signal import savgol_filter


def load_data(file_path):
    data = pd.read_csv(file_path, sep="\t", header=None, names=["Category", "Generation", "Average Points"])
    return data


def plot_file_separately(data, title, output_file):
    plt.figure(figsize=(16, 6))

    for category in data["Category"].unique():
        subset = data[data["Category"] == category]
        plt.plot(subset["Generation"], subset["Average Points"], label=category)

    plt.title(title)
    plt.xlabel("Generation")
    plt.ylabel("Average Points")
    plt.legend()
    plt.grid(True)

    # Save the plot
    plt.savefig(output_file)
    print(f"Plot saved to {output_file}")
    plt.close()

def plot_each_category(data, title_prefix, output_prefix):
    for category in data["Category"].unique():
        subset = data[data["Category"] == category]
        plt.figure(figsize=(16, 6))
        plt.plot(subset["Generation"], subset["Average Points"])
        plt.title(f"{title_prefix} - {category}")
        plt.xlabel("Generation")
        plt.ylabel("Average Points")
        plt.grid(True)
        output_file = f"{output_prefix}_{category}.png"
        plt.savefig(output_file)
        print(f"Plot saved to {output_file}")
        plt.close()


def plot_average(data, title, output_file, smooth=False, window=51, poly=3):
    """
    Plot the average values across all categories, with optional smoothing.
    """
    avg_data = data.groupby("Generation").mean().reset_index()

    plt.figure(figsize=(16, 6))

    if smooth:
        # Apply Savitzky-Golay filter for smoothing
        smooth_points = savgol_filter(avg_data["Average Points"], window_length=window, polyorder=poly)
        plt.plot(avg_data["Generation"], smooth_points, label="Smoothed Average Points", color="blue", alpha=0.8)
    else:
        plt.plot(avg_data["Generation"], avg_data["Average Points"], label="Average", color="blue")

    plt.title(title)
    plt.xlabel("Generation")
    plt.ylabel("Average Points")
    plt.grid(True)
    plt.legend()
    plt.savefig(output_file)
    print(f"Average plot saved to {output_file}")
    plt.close()


def main(against_all_file, against_best_file):
    """
    Main function to plot the two files.
    """
    # Load data from both files
    against_all_data = load_data(against_all_file)
    against_best_data = load_data(against_best_file)

    plot_file_separately(against_all_data, "Benchmark With all Agents","plots/allBenchmark.png")

    plot_each_category(against_best_data, "Against Best Agents", "plots/best_agents")
    plot_average(against_best_data, "Average Against Best Agents", "plots/average_best_agents.png")
    plot_average(against_best_data, "Smoothed Average Against Best Agents", "plots/smoothed_average_best_agents.png", smooth=True, window=51, poly=3)


if __name__ == "__main__":
    against_all_file = "benchMarkAgainstAll.tsv"
    against_best_file = "benchMarkAgainstBest.tsv"
    if len(sys.argv) > 2:
        against_all_file = sys.argv[0]
        against_best_file = sys.argv[2]

    main(against_all_file, against_best_file)
