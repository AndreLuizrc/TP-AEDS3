package Utils;

public enum Status {
    PENDENTE((byte) 0),
    EM_ANDAMENTO((byte) 1),
    CONCLUIDO((byte) 2);

    private final byte value;

    Status(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    public static Status fromByte(byte value) {
        for (Status status : Status.values()) {
            if (status.getValue() == value) {
                return status;
            }
        }
        throw new IllegalArgumentException("Byte inválido");
    }
}
