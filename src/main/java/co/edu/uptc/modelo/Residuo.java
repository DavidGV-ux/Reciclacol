package co.edu.uptc.modelo;

import java.sql.Date;

public class Residuo {
    private String tipoMaterial;
    private double peso;
    private Date fechaEntrega;

    public Residuo(String tipoMaterial, double peso, Date fechaEntrega) {
        if (peso < 0) {
            throw new IllegalArgumentException("El peso no puede ser negativo");
        }
        this.tipoMaterial = tipoMaterial;
        this.peso = peso;
        this.fechaEntrega = fechaEntrega;
    }

    public String getTipoMaterial() {
        return tipoMaterial;
    }

    public double getPeso() {
        return peso;
    }

    public Date getFechaEntrega() {
        return fechaEntrega;
    }
}
