<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ua">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>WebMetaWave</title>
    <link rel="stylesheet" href="/style/Windows.css">
    <link rel="stylesheet" href="/style/LoginWindow.css">
</head>
<body>
    <div class="container">
        <h1 class="LableText">Enter the code</h1>

        <c:if test="${errorCode}">
            <p class="error-message">Wrong code!</p>
        </c:if>

        <form action="code" method="POST">
            <input type="hidden" name="email" value="${email}">
            <input type="hidden" name="password" value="${password}">
            <input type="hidden" name="trueCode" value="${trueCode}">
            <input type="text" id="code" name="inputCode" placeholder="CODE">
            <button type="submit" class="neumorphic-button">CHECK</button>
        </form>

        <form action="/resendCode" method="POST">
            <input type="hidden" name="email" value="${email}">
            <input type="hidden" name="password" value="${password}">
            <input type="hidden" name="trueCode" value="${trueCode}">
            <button type="submit" class="register">Send Again</button>
        </form>

        <a class="register" href="/register">Back</a>
    </div>
</body>
</html>
