<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ua">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>WebMetaWave</title>
    <link rel="stylesheet" href="/style/Windows.css">
    <link rel="stylesheet" href="/style/ProfileWindow.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
</head>
<body>
   <div class="container">
        <div class="head">
            <div class="head-center-area">
                <button class="back-btn" onclick="window.location.href='/profile'">
                    <img class="button-icon" src="/images/icons/fa-solid_arrow-up.svg" alt="back">
                </button>
    
                <h1 class="userNumber">
                    All Users
                </h1>
            </div>
        </div>

       <div class="buttons-function">
           <div>
               <button class="button-func"  id="ban_users">
                   <img class="button-icon" src="/images/icons/lucide_shield-ban.svg" alt="ban">
                   Ban Users
               </button>
               <button class="button-func"  id="unban_users">
                   <img class="button-icon" src="/images/icons/lucide_shield-ban.svg" alt="unban">
                   UnBan Users
               </button>
           </div>
           <br>
           <div>
               <button class="button-func"  id="admin_users">
                   <img class="button-icon" src="/images/icons/dashicons_admin-network.svg" alt="admin">
                   Give Admin Role
               </button>
               <button class="button-func"  id="unadmin_users">
                   <img class="button-icon" src="/images/icons/dashicons_admin-network.svg" alt="admin">
                   Get Admin Role
               </button>
           </div>
       </div>

    <div class="main-info-container">

        <div class="paralelButtons">
            <button class="import-token" onclick="window.location.href='/profile/allUsers/users'">
                Users
            </button>
            <button class="import-token"  onclick="window.location.href='/profile/allUsers/banned'">
                Banned Users
            </button>
            <button class="import-token"  onclick="window.location.href='/profile/allUsers/admins'">
                Admins
            </button>
        </div>

        <div class="tokens-list">
            <c:forEach items="${users}" var="user">
            <div class="token">
                <input type="checkbox" name="toChangeRole[]" value="${user.id}" id="checkbox_${user.id}"/>
                <span class="token-name">${user.email}</span>
                <div>
                    <span class="token-cost">
                            ${user.walletNumber.substring(0, 4)}...${user.walletNumber.substring(user.walletNumber.length() - 4)}
                    </span>
                </div>
            </div>
            </c:forEach>

        </div>
    </div>
   </div>
</body>

<script>
    $('#ban_users').click(function(){
        let data = { 'toChangeRole[]' : []};
        $(":checked").each(function() {
            data['toChangeRole[]'].push($(this).val());
        });
        $.post("/users/banned", data, function(data, status) {
            window.location.reload();
        });
    });


    $('#unban_users').click(function(){
        let data = { 'toChangeRole[]' : []};
        $(":checked").each(function() {
            data['toChangeRole[]'].push($(this).val());
        });
        $.post("/users/unbanned", data, function(data, status) {
            window.location.reload();
        });
    });


    $('#admin_users').click(function(){
        let data = { 'toChangeRole[]' : []};
        $(":checked").each(function() {
            data['toChangeRole[]'].push($(this).val());
        });
        $.post("/users/giveAdmin", data, function(data, status) {
            window.location.reload();
        });
    });


    $('#unadmin_users').click(function(){
        let data = { 'toChangeRole[]' : []};
        $(":checked").each(function() {
            data['toChangeRole[]'].push($(this).val());
        });
        $.post("/users/getAdmin", data, function(data, status) {
            window.location.reload();
        });
    });
</script>

</html>