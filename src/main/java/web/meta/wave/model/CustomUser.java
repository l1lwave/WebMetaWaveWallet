package web.meta.wave.model;

import lombok.*;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class CustomUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String password;
    private String walletNumber;
    @Enumerated(EnumType.STRING)
    private UserRole role;

    public CustomUser(String email,
                      String password,
                      String walletNumber,
                      UserRole role) {
        this.email = email;
        this.password = password;
        this.walletNumber = walletNumber;
        this.role = role;
    }
}
