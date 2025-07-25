package br.ufg.inf.integrador;

public class CrudOperation {
    public enum OperationType { CREATE, UPDATE, DELETE }
    public enum Source { ORM, ODM }

    private String entity;
    private OperationType operation;
    private Source source;
    private String data; // JSON do objeto serializado
    private String timestamp;

    // Construtores
    public CrudOperation() {}

    public CrudOperation(String entity, OperationType operation, Source source, String data, String timestamp) {
        this.entity = entity;
        this.operation = operation;
        this.source = source;
        this.data = data;
        this.timestamp = timestamp;
    }

    // Getters
    public String getEntity() {
        return entity;
    }

    public OperationType getOperation() {
        return operation;
    }

    public Source getSource() {
        return source;
    }

    public String getData() {
        return data;
    }

    public String getTimestamp() {
        return timestamp;
    }

    // Setters
    public void setEntity(String entity) {
        this.entity = entity;
    }

    public void setOperation(OperationType operation) {
        this.operation = operation;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    // toString()
    @Override
    public String toString() {
        return "CrudOperation{" +
                "entity='" + entity + '\'' +
                ", operation=" + operation +
                ", source=" + source +
                ", data='" + data + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
