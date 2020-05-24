import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.sun.net.httpserver.HttpServer;

class ApiServer {

    ApiServer() {
    }

    public void serve() throws IOException {
        int serverPort = 8000;
        HttpServer server = HttpServer.create(new InetSocketAddress(serverPort), 0);
        server.createContext("/api/fib", (exchange -> {

            if ("GET".equals(exchange.getRequestMethod())) {
                Map<String, List<String>> params = splitQuery(exchange.getRequestURI().getRawQuery());

                String noInputText = "1";
                String input = params.getOrDefault("input", List.of(noInputText)).stream().findFirst().orElse(noInputText);
                Integer inputValue = Integer.parseInt(input);
                String respText = String.format("Fibonacci of %s is %s", inputValue, fib(inputValue));
                exchange.sendResponseHeaders(200, respText.getBytes().length);
                OutputStream output = exchange.getResponseBody();
                output.write(respText.getBytes());
                output.flush();
            } else {
                exchange.sendResponseHeaders(405, -1);// 405 Method Not Allowed
            }
        }));
        server.setExecutor(null); // creates a default executor

        System.out.println("Started listening on port " + serverPort);
        server.start();
    }

    public static Map<String, List<String>> splitQuery(String query) {
        if (query == null || "".equals(query)) {
            return Collections.emptyMap();
        }

        return Pattern.compile("&").splitAsStream(query).map(s -> Arrays.copyOf(s.split("="), 2))
                .collect(groupingBy(s -> decode(s[0]), mapping(s -> decode(s[1]), toList())));

    }

    private static String decode(final String encoded) {
        try {
            return encoded == null ? null : URLDecoder.decode(encoded, "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 is a required encoding", e);
        }
    }

    private native long fib(long n);
}