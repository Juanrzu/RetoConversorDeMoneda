import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

public class Conversor {

    private static final String API_URL = "https://v6.exchangerate-api.com/v6/588ebcd8f8f2932740c75741/latest/USD"; // Reemplaza con tu URL

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);

        // 1. Llamada a la API
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // 2. Mostrar JSON recibido (opcional, para depuración)
        System.out.println("=== Respuesta de la API ===");
        System.out.println(response.body());
        System.out.println("===========================");

        // 3. Obtener tasas de conversión
        Map<String, Double> rates = getRates(response.body());

        if (rates == null) {
            System.out.println("⚠ No se pudieron obtener las tasas de conversión");
            return;
        }

        // 4. Menú de opciones
        while (true) {
            System.out.println("\n=== Conversor de Moneda ===");
            System.out.println("1) Dolar =>> Peso argentino ");
            System.out.println("2) Peso argentino =>> Dolar");
            System.out.println("3) Dolar =>> Real brasileño");
            System.out.println("4) Real brasileño =>> Dolar");
            System.out.println("5) Dolar =>> Peso Colombiano");
            System.out.println("6) Peso Colombiano =>> Dolar");
            System.out.println("0) Salir");
            System.out.print("Seleccione una opción: ");

            int opcion = sc.nextInt();
            if (opcion == 0) break;

            System.out.print("Ingrese la cantidad a convertir: ");
            double cantidad = sc.nextDouble();

            switch (opcion) {
                case 1 -> convertir(cantidad, "USD", "ARS", rates, true);
                case 2 -> convertir(cantidad, "ARS", "USD", rates, false);
                case 3 -> convertir(cantidad, "USD", "BRL", rates, true);
                case 4 -> convertir(cantidad, "BRL", "USD", rates, false);
                case 5 -> convertir(cantidad, "USD", "COP", rates, true);
                case 6 -> convertir(cantidad, "COP", "USD", rates, false);
                default -> System.out.println("⚠ Opción inválida");
            }
        }

        sc.close();
        System.out.println("¡Gracias por usar el conversor!");
    }

    // Método para convertir y mostrar resultado
    private static void convertir(double cantidad, String from, String to, Map<String, Double> rates, boolean desdeUSD) {
        Double tasa = rates.get(to);
        if (tasa == null) {
            System.out.println("⚠ No se encontró la tasa para " + to);
            return;
        }

        double resultado = desdeUSD ? cantidad * tasa : cantidad / tasa;
        System.out.printf("%.2f %s = %.2f %s%n", cantidad, from, resultado, to);
    }

    // Método para obtener el Map de tasas
    private static Map<String, Double> getRates(String jsonResponse) {
        JsonObject json = JsonParser.parseString(jsonResponse).getAsJsonObject();

        // Cambiamos "conversion_rates" según tu JSON actual
        JsonObject ratesJson = json.getAsJsonObject("conversion_rates");

        if (ratesJson == null) return null;

        Type type = new TypeToken<Map<String, Double>>() {}.getType();
        return new Gson().fromJson(ratesJson, type);
    }
}
