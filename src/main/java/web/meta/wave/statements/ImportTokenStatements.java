package web.meta.wave.statements;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ImportTokenStatements {
    private final String codeErrorExist = "codeErrorExist";
    private final String userNumber = "userNumber";
    private final String currentNetworkId = "currentNetworkId";
    private final String idInput = "idInput";
    private final boolean isTrue = true;

    //Windows
    private final String importTokenWindow = "importTokenWindow";
    private final String redirect = "redirect:/network/";
}
