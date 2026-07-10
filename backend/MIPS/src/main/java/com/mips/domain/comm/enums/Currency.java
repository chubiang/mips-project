package com.mips.domain.comm.enums;

public enum Currency {
    KRW(0),
    USD(2),
    JPY(0),
    EUR(2),
    CNY(2);

    private final int scale;

    Currency(int scale) {
        this.scale = scale;
    }

    public int getScale() {
        return scale;
    }
}
