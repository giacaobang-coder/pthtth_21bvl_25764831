package Lab06_RMI_20261027;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.rmi.Naming;

public class PrimeClient {
    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        String primeURL = "rmi://" + host + ":1101/PrimeObject";

        try (BufferedReader console = new BufferedReader(
                new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
            PrimeItf object = (PrimeItf) Naming.lookup(primeURL);
            System.out.println("Nhập số nguyên cần kiểm tra. Nhập QUIT để thoát.");

            while (true) {
                System.out.print("n = ");
                String line = console.readLine();
                if (line == null || line.trim().equalsIgnoreCase("QUIT")) break;
                int n;
                try {
                    n = Integer.parseInt(line.trim());
                } catch (NumberFormatException e) {
                    System.err.println("\"" + line.trim() + "\" không phải số nguyên, nhập lại.");
                    continue;
                }
                boolean prime = object.isPrime(n);
                System.out.println("Server: " + n + (prime ? " là" : " không phải là") + " số nguyên tố");
            }
        } catch (Exception e) {
            System.err.println("Lỗi client: " + e);
        }
    }
}
