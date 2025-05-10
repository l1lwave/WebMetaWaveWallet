package web.meta.wave.statements;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class AdminStatements {
    private final String txns = "txns";
    private final String users = "users";

    //Windows
    private final String allTxnsWindow = "allTxnsWindow";
    private final String allUsersWindow = "allUsersWindow";
}
