package co.edu.uptc.util;

public class ValidadorResiduo {

    // Validar que el tipo de material no esté vacío y sea un tipo válido
    public static boolean validarTipoMaterial(String tipoMaterial) {
        String[] tiposValidos = {"Plástico", "Vidrio", "Cartón", "Metal"};
        for (String tipo : tiposValidos) {
            if (tipo.equalsIgnoreCase(tipoMaterial)) {
                return true;
            }
        }
        return false;
    }

    // Validar que el peso del residuo sea positivo
    public static boolean validarPeso(double peso) {
        return peso > 0;
    }
}