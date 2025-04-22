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
<form action="/login_security_check" method="POST">
    <div class="container">
        <h1 class="LableText">
            LOG IN
        </h1>
            <input type="text" id="email" name="email" placeholder="EMAIL" required>
            <input type="password" id="password" name="password" placeholder="PASSWORD" required>
            <button type="submit" class="neumorphic-button">LOG IN</button>

            <c:if test="${param.error ne null}">
                <p class="error-message">Password or email isn't correct!</p>
            </c:if>

            <c:if test="${param.banned ne null}">
                <div class="popup" style="border-left-width: 5px;
                                                    border-left-style: solid;
                                                    border-top-width: 5px;
                                                    border-top-style: solid;
                                                    border-right-width: 5px;
                                                    border-right-style: solid;
                                                    border-bottom-width: 5px;
                                                    border-bottom-style: solid;">
                    <img class="correct-status" src="/images/icons/Vector1.svg" alt="">
                    <p>THIS USER IS BANNED BY ADMIN</p>
                    <p>if this is a mistake, write to us by email</p>
                    <p>webmetawave@gmail.com</p>
                    <a href="/login">Back</a>
                </div>
            </c:if>

        <a class="register" href="/register">Join to us</a>
    </div>
</form>
</body>
</html>
