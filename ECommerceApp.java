/**
 * E-commerce application that allows users to view products, add/remove items to/from the cart, view the cart, and checkout.
 *
 * @author Your Name
 */
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.Collectors;

public class ECommerceApp {

    // Product Class
    /**
     * Represents a product in the e-commerce platform.
     */
    static class Product {
        private int id;
        private String name;
        private double price;

        public Product(int id, String name, double price) {
            this.id = id;
            this.name = name;
            this.price = price;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public double getPrice() {
            return price;
        }

        @Override
        public String toString() {
            return id + ". " + name + " - $" + price;
        }
    }

    // Cart Class
    /**
     * Represents the user's cart in the e-commerce platform.
     */
    static class Cart {
        private ArrayList<Product> cartItems;
        private double totalPrice;

        public Cart() {
            cartItems = new ArrayList<>();
            totalPrice = 0.0;
        }

        /**
         * Adds a product to the cart.
         * @param product the product to add to the cart
         */
        public void addProduct(Product product) {
            cartItems.add(product);
            totalPrice += product.getPrice();
            System.out.println(product.getName() + " added to the cart.\n");
        }

        /**
         * Removes a product from the cart by its ID.
         * @param productId the ID of the product to remove
         */
        public void removeProduct(int productId) {
            Product toRemove = null;
            for (Product product : cartItems) {
                if (product.getId() == productId) {
                    toRemove = product;
                    break;
                }
            }
            if (toRemove != null) {
                cartItems.remove(toRemove);
                totalPrice -= toRemove.getPrice();
                System.out.println(toRemove.getName() + " removed from the cart.\n");
            } else {
                System.out.println("Product not found in the cart.\n");
            }
        }

        /**
         * Displays the contents of the cart.
         */
        public void viewCart() {
            if (cartItems.isEmpty()) {
                System.out.println("Your cart is empty.\n");
                return;
            }

            System.out.println("Items in your cart:");
            for (Product product : cartItems) {
                System.out.println(product.getName() + " - $" + product.getPrice());
            }
            System.out.println("Total Price: $" + totalPrice + "\n");
        }

        /**
         * Returns the total price of all items in the cart.
         * @return the total price of the cart items
         */
        public double getTotalPrice() {
            return totalPrice;
        }

        /**
         * Checks if the cart is empty.
         * @return true if the cart is empty, false otherwise
         */
        public boolean isEmpty() {
            return cartItems.isEmpty();
        }
    }

    /**
     * Main application entry point.
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ArrayList<Product> productList = new ArrayList<>();
        Cart cart = new Cart();

        // Sample Product List
        productList.add(new Product(1, "Laptop", 800.00));
        productList.add(new Product(2, "Smartphone", 500.00));
        productList.add(new Product(3, "Headphones", 50.00));
        productList.add(new Product(4, "Keyboard", 30.00));
        productList.add(new Product(5, "Mouse", 20.00));

        System.out.println("Welcome to the E-Commerce Platform!\n");

        while (true) {
            System.out.println("1. View Products");
            System.out.println("2. Add to Cart");
            System.out.println("3. Remove from Cart");
            System.out.println("4. View Cart");
            System.out.println("5. Checkout");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("\nAvailable Products:");
                    for (Product product : productList) {
                        System.out.println(product);
                    }
                    System.out.println();
                    break;

                case 2:
                    System.out.print("Enter Product ID to add to cart: ");
                    int addId = scanner.nextInt();
                    Product toAdd = productList.stream()
                            .filter(p -> p.getId() == addId)
                            .findFirst()
                            .orElse(null);
                    if (toAdd != null) {
                        cart.addProduct(toAdd);
                    } else {
                        System.out.println("Invalid Product ID.\n");
                    }
                    break;

                case 3:
                    System.out.print("Enter Product ID to remove from cart: ");
                    int removeId = scanner.nextInt();
                    cart.removeProduct(removeId);
                    break;

                case 4:
                    cart.viewCart();
                    break;

                case 5:
                    if (cart.isEmpty()) {
                        System.out.println("Your cart is empty. Add items to proceed.\n");
                    } else {
                        System.out.println("Order Summary:");
                        cart.viewCart();
                        System.out.println("Thank you for shopping with us!\n");
                        return;
                    }
                    break;

                case 6:
                    System.out.println("Exiting the platform. Thank you for visiting!");
                    return;

                default:
                    System.out.println("Invalid choice. Please try again.\n");
            }

            // Add more features
            System.out.println("Do you want to search for products by name? (y/n) ");
            String searchChoice = scanner.next();
            if (searchChoice.equalsIgnoreCase("y")) {
                System.out.print("Enter product name to search: ");
                String searchName = scanner.next();
                ArrayList<Product> searchResult = productList.stream()
                        .filter(p -> p.getName().toLowerCase().contains(searchName.toLowerCase()))
                        .collect(Collectors.toCollection(ArrayList::new));
                if (!searchResult.isEmpty()) {
                    System.out.println("Search result:");
                    for (Product product : searchResult) {
                        System.out.println(product);
                    }
                    System.out.println();
                } else {
                    System.out.println("No products found with the given name.\n");
                }
            }
        }
    }
}

