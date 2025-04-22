package web.meta.wave.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class MetaToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String tokenName;
    private String tokenSymbol;
    private Long tokenCode;
    private BigDecimal tokenValue;

    public MetaToken(String tokenName,
                     String tokenSymbol,
                     Long tokenCode,
                     BigDecimal tokenValue) {
        this.tokenName = tokenName;
        this.tokenSymbol = tokenSymbol;
        this.tokenCode = tokenCode;
        this.tokenValue = tokenValue;
    }
}
