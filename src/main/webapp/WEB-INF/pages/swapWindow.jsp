<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ua">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>WebMetaWave</title>
    <link rel="stylesheet" href="/style/Windows.css">
    <link rel="stylesheet" href="/style/SwapWindow.css">
</head>
<body>
<div class="container">
    <div class="head">
        <div class="head-center-area">
            <button class="back-btn">
                <a href="/network/1">
                    <img class="button-icon" src="/images/icons/fa-solid_arrow-up.svg" alt="back">
                </a>
            </button>

            <button class="transparent-button" onclick="window.location.href='/profile'">
                <h1 class="userNumber">
                    ${userNumber.substring(0, 6)}...${userNumber.substring(userNumber.length() - 4)}
                </h1>
            </button>
        </div>
    </div>

        <details class="dropdown">
            <summary class="dropdown-btn">${LabelNetworkName.substring(0, 7)}...<span class="arrow">▼</span></summary>
            <ul class="dropdown-menu">
                <c:forEach items="${networks}" var="network">
                    <li><a href="/swapToken/network=${network.id}">${network.name}</a></li>
                </c:forEach>
            </ul>
        </details>

    <form action="/swap" method="get">

    <div class="main-info-container">
            <div class="container-info">

                <details class="dropdown">
                    <summary class="dropdown-btn">${LabelTokenFromName}<span class="arrow">▼</span></summary>
                    <ul class="dropdown-menu">
                        <c:forEach items="${tokens}" var="token">
                            <li><a href="/swapToken/network=${selectedNetworkId}/tokenFrom=${token.id}/tokenTo=${selectedTokenToId}">${token.tokenName}</a></li>
                        </c:forEach>
                    </ul>
                </details>

                <h2 class="balance-value">Balance: ${userTokenBalance}</h2>



                    <input type="hidden" name="networkIdS" value="${selectedNetworkId}">
                    <input type="hidden" name="tokenFromIdS" value="${selectedTokenFromId}">
                    <input type="hidden" name="tokenToIdS" value="${selectedTokenToId}">
                    <input type="hidden" id="tokenValue" name="tokenFromValue" value="${tokenFromValue}">
                    <input type="hidden" id="ethTokenValue" name="ethTokenValue" value="${ethTokenValue}">
                    <input type="hidden" id="gas" name="gas" value="${swapGas}">
                    <input type="hidden" id="coef" name="coef" value="${coef}">


                <input type="text" class="input-token-value" id="valueInput" oninput="this.value = this.value.replace(/[^0-9.]/g, '').replace(/(\..*)\./g, '$1');" name="valueS" placeholder="0">
            </div>

            <div class="container-info">
                <details class="dropdown">
                    <summary class="dropdown-btn">${LabelTokenToName}<span class="arrow">▼</span></summary>
                    <ul class="dropdown-menu">
                        <c:forEach items="${tokens}" var="token">
                            <li><a href="/swapToken/network=${selectedNetworkId}/tokenFrom=${selectedTokenFromId}/tokenTo=${token.id}">${token.tokenName}</a></li>
                        </c:forEach>
                    </ul>
                </details>

                <input type="text" class="input-token-value" id="convertedValue" value="0" readonly>
                <br>
                <div>
                    Gas: <input style="font-size: 15px; width: 20%;" type="text" class="input-token-value" id="gasValue" value="0" readonly> ETH
                </div>

            </div>

            <button class="button-func" type="submit">
                <img class="button-icon" src="/images/icons/eva_swap-fill.svg" alt="swap">
                Swap
            </button>

            <c:if test="${transactionDone}">
                <div class="popup" style="border-left-width: 5px;
                                                border-left-style: solid;
                                                border-top-width: 5px;
                                                border-top-style: solid;
                                                border-right-width: 5px;
                                                border-right-style: solid;
                                                border-bottom-width: 5px;
                                                border-bottom-style: solid;">
                    <img class="correct-status" src="images/icons/Vector.svg" alt="">
                    <p>Transaction is done</p>
                    <a href="/swapToken">Back</a>
                </div>
            </c:if>

            <c:if test="${transactionFail}">
                <div class="popup" style="border-left-width: 5px;
                                                border-left-style: solid;
                                                border-top-width: 5px;
                                                border-top-style: solid;
                                                border-right-width: 5px;
                                                border-right-style: solid;
                                                border-bottom-width: 5px;
                                                border-bottom-style: solid;">
                    <img class="correct-status" src="images/icons/Vector1.svg" alt="">
                    <p>Transaction is fail</p>
                    <p>${failText}</p>
                    <a href="/swapToken">Back</a>
                </div>
            </c:if>

        </form>
        </div>
</div>
</body>

<script>
    document.addEventListener('DOMContentLoaded', function() {
        const valueInput = document.getElementById('valueInput');

        const gasValue = document.getElementById('gasValue');

        const gasRate = parseFloat(document.getElementById('gas').value);
        const tokenRate = parseFloat(document.getElementById('tokenValue').value);
        const ethRate = parseFloat(document.getElementById('ethTokenValue').value);

        const convertedValue = document.getElementById('convertedValue');
        const coef = parseFloat(document.getElementById('coef').value);

        valueInput.addEventListener('input', function() {
            const inputValue = parseFloat(this.value) || 0;
            const tokenCost = inputValue * tokenRate;
            const calculatedGas = tokenCost * (gasRate/100);
            const result = calculatedGas / ethRate;

            gasValue.value = result.toFixed(10);
        });

        valueInput.addEventListener('input', function() {
            const inputValue = parseFloat(this.value) || 0;
            const calculatedCoef = inputValue * coef;

            convertedValue.value = calculatedCoef.toFixed(6);
        });
    });
</script>

</html>