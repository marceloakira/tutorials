package br.ufg.inf.sga.model;

public enum StatusMatricula {
    CONFIRMADA("Confirmada"),
    SOLICITADA("Solicitada"),
    CANCELADA("Cancelada");

    private final String label;

    StatusMatricula(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
