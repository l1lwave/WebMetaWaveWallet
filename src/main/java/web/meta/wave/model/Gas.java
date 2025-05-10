package web.meta.wave.model;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class Gas {
    private final BigDecimal swapGas = BigDecimal.valueOf(1);
    private final BigDecimal bridgeGas = BigDecimal.valueOf(1.5);
    private final BigDecimal sendGas = BigDecimal.valueOf(1.8);

}
