def read_order():
    names = read_names()
    return names_to_order(names)


def names_to_order(names):
    order = [names.pop(0)]
    prefixes = ["s", "e"]
    for prefix in prefixes:
        for name in names:
            order.append(prefix + name)

    return order


def read_names():
    with open("weight_order.txt", "r") as order_file:
        return order_file.read().replace(" ", "").split("\n")[:-1]


def read_weights():
    with open("weights.tsv", 'r') as file:
        return [int(field.strip()) for field in file.readline().split("\t")]


def print_weights():
    weights = read_weights()
    to_print = "public int[] weights = {"
    for weight in weights:
        to_print += str(weight) + ", "

    print(to_print[:-2] + "};")


def print_explained_weights():
    names = read_order()
    weights = read_weights()

    for i, (name, weights) in enumerate(zip(names, weights)):
        print(str(i) + ": " + name + " -> " + str(weights))


def print_weight_changes():
    names = read_names()
    weights = read_weights()

    print("change after " + str(weights[0]) + " moves")
    weights.pop(0)
    names.pop(0)

    for i, name in enumerate(names):
        print(name + ": " + str(weights[i]) + " -> " + str(weights[len(names) + i]))


def print_declarations():
    order = read_order()
    for i, name in enumerate(order):
        print("int " + name + " = " + str(i) + ";")


def add_metric(new_name, s_weight, e_weight):
    names = read_names()
    weights = read_weights()
    names.append(new_name)

    weight_s_index = len(names) - 1
    weights.insert(weight_s_index, s_weight)
    weights.append(e_weight)

    rewrite_files(names, weights)


def rewrite_files(names, weights):
    with open("order", "w") as order_file:
        for name in names:
            order_file.write(name)
            order_file.write("\n")

    with open("weights", "w") as weights_file:
        for weight in weights:
            weights_file.write(str(weight))
            weights_file.write("\t")



print_weight_changes()
