package web.meta.wave.model;

import java.math.BigDecimal;

public class Gas {
    private BigDecimal swapGas = BigDecimal.valueOf(1);
    private BigDecimal bridgeGas = BigDecimal.valueOf(1.5);
    private BigDecimal sendGas = BigDecimal.valueOf(1.8);

    public BigDecimal getSwapGas() {
        return swapGas;
    }

    public BigDecimal getBridgeGas() {
        return bridgeGas;
    }

    public BigDecimal getSendGas() {
        return sendGas;
    }
}
