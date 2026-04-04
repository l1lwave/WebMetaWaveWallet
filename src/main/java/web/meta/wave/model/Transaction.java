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
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String date;
    @ManyToOne
    @JoinColumn(name = "user_from_id")
    private CustomUser userFrom;
    @ManyToOne
    @JoinColumn(name = "user_to_id")
    private CustomUser userTo;
    @ManyToOne
    @JoinColumn(name = "token_from_id")
    private MetaToken tokenFrom;
    @ManyToOne
    @JoinColumn(name = "token_to_id")
    private MetaToken tokenTo;
    private BigDecimal amountFrom;
    private BigDecimal amountTo;
    @Enumerated(EnumType.STRING)
    private Status status;

    public Transaction(String date, CustomUser userFrom, CustomUser userTo, MetaToken tokenFrom, MetaToken tokenTo, BigDecimal amountFrom, BigDecimal amountTo, Status status) {
        this.date = date;
        this.userFrom = userFrom;
        this.userTo = userTo;
        this.tokenFrom = tokenFrom;
        this.tokenTo = tokenTo;
        this.amountFrom = amountFrom;
        this.amountTo = amountTo;
        this.status = status;
    }
}
