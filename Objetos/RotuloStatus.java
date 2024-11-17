package Objetos;

public enum RotuloStatus  {
    PENDENTE((byte) 0),
    EM_ANDAMENTO((byte) 1),
    CONCLUIDO((byte) 2);

    private final byte value;

    RotuloStatus(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    public static RotuloStatus fromByte(byte value) {
        for (RotuloStatus status : RotuloStatus.values()) {
            if (status.getValue() == value) {
                return status;
            }
        }
        throw new IllegalArgumentException("Byte inválido");
    }
}
