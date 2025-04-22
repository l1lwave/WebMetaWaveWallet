<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="eng">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>WebMetaWave</title>
    <link rel="stylesheet" href="/style/Windows.css">
    <link rel="stylesheet" href="/style/LoginWindow.css">
</head>
<body>
<form action="newuser" method="post">
    <div class="container">
        <h1 class="LableText">
            SIGN UP
        </h1>
            <input type="email" id="email" name="email" placeholder="EMAIL" required>
            <input type="password" id="password" name="password" placeholder="PASSWORD" required>
            <input type="password" id="passwordCheck" name="passwordCheck" placeholder="CONFIRM PASSWORD" required>
            <button type="submit" class="neumorphic-button">SIGN UP</button>


        <c:if test="${emailError}">
            <p class="error-message">This email already exist!</p>
        </c:if>

        <c:if test="${nullError}">
            <p class="error-message">Enter password!</p>
        </c:if>

        <c:if test="${passwordLengthError}">
            <p class="error-message">Password must be longer than 8 symbols!</p>
        </c:if>

        <c:if test="${passError}">
            <p class="error-message">Passwords isn't equals!</p>
        </c:if>

        <a class="register" href="/login">Back</a>
    </div>
</form>
</body>
</html>
