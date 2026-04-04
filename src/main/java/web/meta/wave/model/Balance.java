package web.meta.wave.model;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Balance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "network_id")
    private Network network;
    @ManyToOne
    @JoinColumn(name = "token_id")
    private MetaToken metaToken;
    @ManyToOne
    @JoinColumn(name = "custom_user_id")
    private CustomUser customUser;
    @Column(precision = 56, scale = 6)
    private BigDecimal balance;

    public Balance(Network network,
                   MetaToken metaToken,
                   CustomUser customUser,
                   BigDecimal balance) {
        this.network = network;
        this.metaToken = metaToken;
        this.customUser = customUser;
        this.balance = balance;
    }
}
