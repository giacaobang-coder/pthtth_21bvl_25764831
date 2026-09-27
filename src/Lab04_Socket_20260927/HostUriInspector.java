package Lab04_Socket_20260927;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

public class HostUriInspector {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java HostUriInspector <hostname> <uri>");
            return;
        }

        inspectHost(args[0]);
        inspectUri(args[1]);
    }

    private static void inspectHost(String hostname) {
        System.out.println("== Host: " + hostname + " ==");
        try {
            InetAddress[] addresses = InetAddress.getAllByName(hostname);
            for (InetAddress address : addresses) {
                String type;
                if (address instanceof Inet4Address) {
                    type = "IPv4";
                } else if (address instanceof Inet6Address) {
                    type = "IPv6";
                } else {
                    type = "Unknown";
                }
                System.out.println("- IP: " + address.getHostAddress() + " (" + type + ")");
                System.out.println("  Loopback: " + address.isLoopbackAddress());
                System.out.println("  Site local: " + address.isSiteLocalAddress());
            }
        } catch (UnknownHostException e) {
            System.err.println("Không phân giải được host: " + hostname);
        }
    }

    private static void inspectUri(String uriText) {
        System.out.println("== URI: " + uriText + " ==");
        try {
            URI uri = new URI(uriText);
            System.out.println("Scheme: " + uri.getScheme());
            System.out.println("Host: " + uri.getHost());
            System.out.println("Port: " + uri.getPort());
            System.out.println("Path: " + uri.getPath());
            System.out.println("Query: " + uri.getQuery());
            System.out.println("Fragment: " + uri.getFragment());
        } catch (URISyntaxException e) {
            System.err.println("URI không hợp lệ: " + e.getMessage());
        }
    }
}
