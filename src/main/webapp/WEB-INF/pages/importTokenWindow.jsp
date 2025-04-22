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
    <link rel="stylesheet" href="/style/AddTokenWindow.css">
</head>
<body>
<form action="/${currentNetworkId}/importToken" method="POST">
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

    <div class="main-info-container">
        <div class="container-info">
            <h1 class="text-value">
                Enter token UCID
            </h1>

            <input type="text" class="wallet-number-input" name="code" placeholder="">
        </div>
    </div>

    <c:if test="${codeError ne null}">
        <p class="error-message">Token allready exist in your network!</p>
    </c:if>

    <c:if test="${codeErrorExist ne null}">
    <p class="error-message">Token with this code isn`t exist!</p>
    </c:if>

    <button type="submit" class="button-func">
        Add Token
    </button>
</form>


</body>

</html>