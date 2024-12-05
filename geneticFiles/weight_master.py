import os


def read_given_weights_file(given_weights_file):
    """
    Read and return weights from the weights file.
    """
    if not os.path.exists(given_weights_file):
        raise FileNotFoundError(f"{given_weights_file} not found.")

    with open(given_weights_file, "r") as file:
        return [int(weight.strip()) for weight in file.readline().split("\t")]


class GeneticFileHelper:
    def __init__(self, order_file="weight_order.txt", weights_file="run2/best.txt",
                 original_weights_file="original_weights.txt"):
        self.order_file = order_file
        self.weights_file = weights_file
        self.original_weights_file = original_weights_file

    def read_names(self):
        """
        Read and return names from the order file.
        """
        if not os.path.exists(self.order_file):
            raise FileNotFoundError(f"{self.order_file} not found.")

        with open(self.order_file, "r") as file:
            return file.read().strip().replace(" ", "").replace("Index", "").split("\n")

    def read_weights(self):
        return read_given_weights_file(self.weights_file)

    def read_original_weights(self):
        if not os.path.exists(self.original_weights_file):
            raise FileNotFoundError(f"{self.original_weights_file} not found.")

        with open(self.original_weights_file, "r") as file:
            return [int(weight.strip()) for weight in file.read().strip().split("\n")]

    def names_to_order(self, names):
        """
        Convert names to an ordered list based on prefixes.
        """
        order = [names.pop(0)]
        prefixes = ["s", "e"]
        for prefix in prefixes:
            for name in names:
                order.append(prefix + name)
        return order

    def read_order(self):
        """
        Read names and convert them to an order.
        """
        names = self.read_names()
        return self.names_to_order(names)

    def print_weights(self):
        """
        Print the weights in a format suitable for inclusion in Java code.
        """
        weights = self.read_weights()
        weights_string = "public int[] weights = {" + ", ".join(map(str, weights)) + "};"
        print(weights_string)

    def print_explained_weights(self):
        """
        Print weights along with their corresponding names and indices.
        """
        names = self.read_order()
        weights = self.read_weights()

        for i, (name, weight) in enumerate(zip(names, weights)):
            print(f"{i}: {name} -> {weight}")

    def print_weight_changes(self):
        """
        Print changes in weights along with names.
        """
        names = self.read_names()
        weights = self.read_weights()

        print(f"Change after {weights[0]} moves")
        weights.pop(0)
        names.pop(0)

        for i, name in enumerate(names):
            print(f"{name}: {weights[i]} -> {weights[len(names) + i]}")

    def print_declarations(self):
        """
        Print variable declarations for each order entry.
        """
        order = self.read_order()
        for i, name in enumerate(order):
            print(f"int {name[:1].lower() + name[1:]} = {i};")


    def add_metric(self, new_name, s_weight, e_weight):
        """
        Add a new metric and update both order and weights files.
        """
        names = self.read_names()
        weights = self.read_weights()
        names.append(new_name)

        weight_s_index = len(names) - 1
        weights.insert(weight_s_index, s_weight)
        weights.append(e_weight)

        self.rewrite_files(names, weights)

    def rewrite_files(self, names, weights):
        """
        Rewrite the order and weights files with updated data.
        """
        with open(self.order_file, "w") as order_file:
            order_file.write("\n".join(names) + "\n")

        with open(self.weights_file, "w") as weights_file:
            weights_file.write("\t".join(map(str, weights)) + "\t")

    def print_comparison(self):
        """
        Print weights and their changes side by side for direct comparison.
        """
        names = self.read_names()
        weights = self.read_weights()
        orig_weights = self.read_original_weights()

        print(f"Original Changes after {orig_weights[0]} Moves, New Changes after {weights[0]} Moves")
        weights.pop(0)
        orig_weights.pop(0)
        names.pop(0)

        print("Name\tos\toe\ts\tne")
        for i, name in enumerate(names):
            print(f"{name}\t{orig_weights[i]}\t{orig_weights[len(names) + i]}\t{weights[i]}\t{weights[len(names) + i]}")


if __name__ == "__main__":
    helper = GeneticFileHelper()
    print("-"*10)
    helper.print_comparison()
    print("-"*10)
    helper.print_weight_changes()
    print("-"*10)
    helper.print_weights()
