package Lab06_RMI_20261027;

import java.rmi.Naming;

public class HelloClient {
    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        String helloURL = "rmi://" + host + ":1099/HelloObject";

        try {
            HelloItf object = (HelloItf) Naming.lookup(helloURL);
            String message = object.sayHello();
            System.out.println("Server: " + message);
        } catch (Exception e) {
            System.err.println("Lỗi client: " + e);
        }
    }
}
