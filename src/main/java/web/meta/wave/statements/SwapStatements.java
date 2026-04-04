package web.meta.wave.statements;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class SwapStatements {
    private final String networksList = "networks";
    private final String tokensList = "tokens";
    private final String userNumber = "userNumber";
    private final String labelNetworkName = "LabelNetworkName";
    private final String networks = "Networks";
    private final String labelTokenFromName = "LabelTokenFromName";
    private final String tokenFrom = "Token From";
    private final String labelTokenToName = "LabelTokenToName";
    private final String tokenTo = "Token To";
    private final String coef = "coef";
    private final String swapGas = "swapGas";
    private final int zero = 0;
    private final String selectedNetworkId = "selectedNetworkId";
    private final String selectedTokenFromId = "selectedTokenFromId";
    private final String selectedTokenToId = "selectedTokenToId";
    private final String userTokenBalance = "userTokenBalance";
    private final String ethTokenValue = "ethTokenValue";
    private final String tokenFromValue = "tokenFromValue";
    private final String parametersError = "You don't have enough parameters";
    private final String sameTokensError = "You choose same tokens";
    private final String notEnouthFundsError = "You don't have enough funds💵❌";
    private final String notEnouthGas = "You don't have enough funds for gas🚀";
    private final String error = "Parameters error";
    private final boolean trueChoose = true;
    private final boolean falseChoose = false;
    private final String transactionDone = "transactionDone";
    private final String transactionFail = "transactionFail";
    private final String failText = "failText";
    private final String GAS_EMAIL = "gas@a.com";
    private final long ETH_CODE = 1027L;
    private final long gasNetwork = 1L;
    private final String noError = "OK";


    //Windows
    private final String swapWindow = "swapWindow";


}
