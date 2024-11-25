import sys

import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
from scipy.signal import savgol_filter
import seaborn as sns


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


def add_to_df(origin_factors, best_weights_factors, labels):
    data = []
    for i, label in enumerate(labels):
        data.append(["handcrafted", label, origin_factors[i]])
        data.append(["genetic", label, best_weights_factors[i]])
    #remove move change entries
    data.pop(0)
    data.pop(0)
    return pd.DataFrame(data=data, columns=["agent", "weight", "weight factor"])


def plot_weights_changes_for_best(best_weights_file, original_weights_file, name_file):
    origin_factors = get_origin_factor(original_weights_file)
    best_weights_factors = get_best_weights(best_weights_file)
    labels = get_weight_names(name_file)

    df = add_to_df(origin_factors, best_weights_factors, labels)

    sns.set(style="whitegrid")

    # Create a bar plot
    plt.figure(figsize=(15, 7.5))
    ax=sns.barplot(data=df, x="weight", y="weight factor", hue="agent", palette="muted")
    plt.xticks(rotation=90)  # Rotate 45 degrees and align to the right
    ax.grid(True, which='major', axis='x', linestyle='--', linewidth=0.5, color='gray')  # Y-axis gridlines
    plt.gcf().subplots_adjust(bottom=0.3)  # Increase space at the bottom

    # Add labels and title
    plt.xlabel("Weight Factors", fontsize=12)
    plt.ylabel("Values", fontsize=12)
    plt.title("Comparison of Handcrafted and Genetic Weight Factors", fontsize=14)
    plt.legend(title="Agent", fontsize=10)


    plt.show()


def get_weight_names(name_file):
    with open(name_file) as file:
        names = file.read().split("\n")
        return [names[0]] + ["s" + name.replace("Index","") for name in names[1:] if name] + ["e" + name.replace("Index","") for name in names[1:] if name]


def get_origin_factor(original_weights_file):
    with open(original_weights_file) as file:
        weights = file.read().split("\n")
        weights = [int(w) for w in weights if w.strip()]
    return normalize_weights(weights)


def get_best_weights(best_weights_file):
    with open(best_weights_file) as file:
        weights = file.readline().split("\t")
    weights = [int(w) for w in weights if w.strip()]
    return normalize_weights(weights)


def normalize_weights(weights):
    weights_sum = np.sum(np.abs(weights[1:]))
    first_weight = weights[0]
    weights = [w / weights_sum for w in weights[1:]]
    return [first_weight] + weights


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
    plt.close()


def plot_population_distribution(population_file,name_file,out_file):
    labels=get_weight_names(name_file)

def main(against_all_file, against_best_file, original_weights, best_weights, weight_names):
    # Load data from both files
    against_all_data = load_data(against_all_file)
    against_best_data = load_data(against_best_file)

    plot_file_separately(against_all_data, "Benchmark With all Agents", "plots/allBenchmark.png")

    plot_each_category(against_best_data, "Against Best Agents", "plots/best_agents")
    plot_average(against_best_data, "Average Against Best Agents", "plots/average_best_agents.png")
    plot_average(against_best_data, "Smoothed Average Against Best Agents",
                 "plots/smoothed_average_best_agents.png", smooth=True, window=51, poly=3)
    plot_weights_changes_for_best(best_weights, original_weights, weight_names)


if __name__ == "__main__":
    against_all_file = "benchmarkAgainstAll.tsv"
    against_best_file = "benchmarkAgainstBest.tsv"
    original_weights = "original_weights.txt"
    best_weights = "best.txt"
    weight_names = "weight_order.txt"
    population="weights.tsv"
    if len(sys.argv) > 2:
        against_all_file = sys.argv[0]
        against_best_file = sys.argv[2]
    #plot_weights_changes_for_best(best_weights, original_weights, weight_names)
    # main(against_all_file, against_best_file, original_weights, best_weights, weight_names)
