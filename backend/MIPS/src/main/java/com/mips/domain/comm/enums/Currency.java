package com.mips.domain.comm.enums;

public enum Currency {
    KRW("KRW", 0),
    USD("USD",2),
    JPY("JPY", 0),
    EUR("EUR", 2),
    CNY("CNY", 2);

    private final String unit;
    private final int scale;

    Currency(String unit, int scale) {
        this.unit = unit;
        this.scale = scale;
    }
    public String getUnit() { return unit; }
    public int getScale() {
        return scale;
    }


}
