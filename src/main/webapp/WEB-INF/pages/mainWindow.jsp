<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ua">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>WebMetaWave</title>
    <link rel="stylesheet" href="/style/Windows.css">
    <link rel="stylesheet" href="/style/mainWindow.css">
</head>
<body>
<div class="container">
    <div class="head">
        <div class="head-center-area">
            <details class="dropdown">
                <summary class="dropdown-btn">${LabelNetworkName.substring(0, 5)}...<span class="arrow">▼</span></summary>
                <ul class="dropdown-menu">
                    <c:forEach items="${networks}" var="network">
                        <li><a href="/network/${network.id}">${network.name}</a></li>
                    </c:forEach>
                </ul>
            </details>

            <button class="transparent-button" onclick="window.location.href='/profile'">
                <h1 class="userNumber">
                    ${userNumber.substring(0, 6)}...${userNumber.substring(userNumber.length() - 4)}
                </h1>
            </button>
        </div>
    </div>

    <div class="balance">
        $<fmt:formatNumber value="${userBalance}" type="number" minFractionDigits="2" maxFractionDigits="2"/> USD
    </div>


    <div class="buttons-function">
        <button class="button-func" onclick="window.location.href='/swapToken'">
            <img class="button-icon" src="/images/icons/eva_swap-fill.svg">
            Swap
        </button>
        <button class="button-func" onclick="window.location.href='/bridgeToken'">
            <img class="button-icon" src="/images/icons/mingcute_bridge-line.svg">
            Bridge
        </button>
        <button class="button-func" onclick="window.location.href='/sendToken'">
            <img class="button-icon" src="/images/icons/mingcute_send-line.svg">
            Send
        </button>
    </div>

    <div class="main-info-container">
        <button class="import-token" onclick="window.location.href='/${currentNetworkId}/importToken'">
            + Import token
        </button>

        <div class="tokens-list">
            <c:forEach items="${balances}" var="balance">
                <div class="token">
                    <span class="token-name">${balance.metaToken.getTokenName()}</span>
                    <div>
                        <span class="token-cost">
                            <span>
                            $<fmt:formatNumber
                                    value="${balance.balance.multiply(balance.metaToken.getTokenValue())}"
                                    type="number"
                                    minFractionDigits="2"
                                    maxFractionDigits="2"/>
                            USD
                            </span>
                        <br>
                        <span class="token-value">${balance.balance} ${balance.metaToken.getTokenSymbol()}</span>
                    </div>
                </div>
            </c:forEach>
        </div>

    </div>
</div>
</body>
</html>