
import java.io.*;
import java.util.*;

/**
 * Hotel Room Booking System - console application (PS 18)
 * Concepts: ArrayList, HashMap, Searching, Sorting, Methods, File Handling.
 * Rooms are stored as objects in an ArrayList; a HashMap gives O(1) lookup by room number.
 */
class Room {
    String number, type, guest;
    int price, nights;
    boolean booked;

    Room(String number, String type, int price, boolean booked, String guest, int nights) {
        this.number = number; this.type = type; this.price = price;
        this.booked = booked; this.guest = guest; this.nights = nights;
    }

    String line() {
        return number + " | " + type + " | " + price + " | " + (booked ? "Booked" : "Available");
    }

    String toFileLine() {
        return number + "," + type + "," + price + "," + booked + "," + (guest == null ? "" : guest) + "," + nights;
    }
}

public class Hotelroombookingsystem  {
    static final String FILE = "rooms.txt";
    static ArrayList<Room> rooms = new ArrayList<>();
    static HashMap<String, Room> roomMap = new HashMap<>();
    static Scanner sc = new Scanner(System.in);

    // ---------- File handling ----------
    static void loadRooms() {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE))) {
            String ln;
            while ((ln = br.readLine()) != null) {
                ln = ln.trim();
                if (ln.isEmpty()) continue;
                String[] p = ln.split(",", -1);
                if (p.length < 3) continue;
                boolean booked = p.length > 3 && p[3].trim().equalsIgnoreCase("true");
                String guest = p.length > 4 ? p[4].trim() : "";
                int nights = p.length > 5 && !p[5].trim().isEmpty() ? Integer.parseInt(p[5].trim()) : 0;
                Room r = new Room(p[0].trim(), p[1].trim(), Integer.parseInt(p[2].trim()), booked, guest, nights);
                rooms.add(r);
                roomMap.put(r.number, r);
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Could not load " + FILE + ": " + e.getMessage());
        }
    }

    static void saveRooms() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE))) {
            for (Room r : rooms) pw.println(r.toFileLine());
        } catch (IOException e) {
            System.out.println("Could not save " + FILE + ": " + e.getMessage());
        }
    }

    // ---------- Features ----------
    static void viewRooms() {
        System.out.println("Room | Type | Price | Status");
        for (Room r : rooms) System.out.println(r.line());
    }

    static void bookRoom() {
        String no = ask("Room: ");
        Room r = roomMap.get(no);
        if (r == null) { System.out.println("Room " + no + " not found."); return; }
        if (r.booked) { System.out.println("Room " + no + " is already booked."); return; }
        String guest = ask("Guest: ");
        int nights;
        try { nights = Integer.parseInt(ask("Nights: ")); }
        catch (NumberFormatException e) { System.out.println("Invalid number of nights."); return; }
        if (nights <= 0) { System.out.println("Nights must be at least 1."); return; }
        r.booked = true; r.guest = guest; r.nights = nights;
        System.out.println("Room " + no + " booked for " + guest + ". Total: " + (r.price * nights));
        if (countAvailable() == 0) System.out.println("Available rooms: 0");
    }

    static void searchAvailable() {
        String type = ask("Type: ");
        boolean found = false;
        for (Room r : rooms)
            if (!r.booked && r.type.equalsIgnoreCase(type)) {
                System.out.println(r.number + " | " + r.type + " | " + r.price + " | Available");
                found = true;
            }
        if (!found) System.out.println("No available " + type + " rooms.");
    }

    static void checkOut() {
        String no = ask("Room: ");
        Room r = roomMap.get(no);
        if (r == null) { System.out.println("Room " + no + " not found."); return; }
        if (!r.booked) { System.out.println("Room " + no + " is not booked."); return; }
        int bill = r.price * r.nights;
        r.booked = false; r.guest = ""; r.nights = 0;
        System.out.println("Room " + no + " checked out. Bill: " + bill + ". Status: Available");
    }

    static void sortByPriceDesc() {
        ArrayList<Room> copy = new ArrayList<>(rooms);
        copy.sort((a, b) -> Integer.compare(b.price, a.price));
        for (Room r : copy) System.out.println(r.number + " | " + r.price);
    }

    static int countAvailable() {
        int c = 0;
        for (Room r : rooms) if (!r.booked) c++;
        return c;
    }

    static String ask(String prompt) {
        System.out.println(prompt);
        return sc.hasNextLine() ? sc.nextLine().trim() : "";
    }

    static void menu() {
        System.out.println("\n=== Hotel Room Booking System ===");
        System.out.println("1. View rooms");
        System.out.println("2. Book room");
        System.out.println("3. Search available by type");
        System.out.println("4. Check out");
        System.out.println("5. Sort by price (high to low)");
        System.out.println("6. Exit");
        System.out.println("Choose option:");
    }

    public static void main(String[] args) {
        loadRooms();
        while (true) {
            menu();
            if (!sc.hasNextLine()) break;
            String ch = sc.nextLine().trim();
            switch (ch) {
                case "1": viewRooms(); break;
                case "2": bookRoom(); break;
                case "3": searchAvailable(); break;
                case "4": checkOut(); break;
                case "5": sortByPriceDesc(); break;
                case "6": saveRooms(); System.out.println("Data saved. Goodbye!"); return;
                default: System.out.println("Invalid option.");
            }
        }
        saveRooms();
    }
}