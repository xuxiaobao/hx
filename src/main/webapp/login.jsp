<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
  <title>鸿信嘉诚流量充值平台——登录</title>
  <link rel="icon"  href="../favicon.gif"/>
  <link type="text/css" rel="stylesheet"  href="css/login.css"/>
  <script  src="jquery/jquery-1.9.1.min.js" type="application/javascript"></script>
</head>

<body>
<div class="container">
  <!--<video  src="../images/login/background.webm" autoplay="autoplay" loop="loop"></video>-->
  <div class="main">
    <div class="clearfix">
      <div class="logo fl"></div>
      <div class="logo-title" style="color:royalblue; margin-left:-35px;">鸿信嘉诚流量充值平台</div>
    </div>
    <div class="clearfix">
      <div class="fl">
        <div class="motto"></div>
      </div>
      <form id="fm1" class="login" action="api/system/user/login" method="post" style="margin-right:-180px">
          <c:if test="${param.logout}">
              <p class="note-warn">您已退出！</p>
          </c:if>
          <c:if test="${param.error}">
              <p class="note-error"><strong>天呐，</strong>登录失败，请重试！.</p>
          </c:if>

        <input id="account" name="username" class="txt" accesskey="n" type="text" value=""
               autocomplete="false"/>
        <input id="pwd" name="password" class="txt" accesskey="p" type="password" value="" autocomplete="off"/>

        <div class="row check">
          <input id="remember_me" name="remember_me" tabindex="3" accesskey="w" type="checkbox"/>
          <label for="remember_me">记住账号</label>
        </div>
        <div class="row btn-row">
          <input class="btn" name="submit" accesskey="l" value="登录" tabindex="4" type="submit"/>
        </div>
      </form>
    </div>
    <div class="footer">Copyright 2015-2018 xxx科技有限公司 all rights reserved. 苏ICP备xx-xxxxx</div>
  </div>
</div>
<script type="text/javascript">
  $(document).ready(function () {
    var h = $(document).height();
    var w = $(document).width();
    $(".container").height(h);
//        $("video").width(w);
//        $(".motto").fadeIn('500', function () {
//            setTimeout(function () {
    $('.motto').animate({'left': 30}, 500);
//            }, 300);
//        });
    $("#account").focus(function () {
      $(this).addClass("txtFocus");
      $(this).css("backgroundPosition", "-250px -179px");
    });
    $("#account").blur(function () {
      $(this).removeClass("txtFocus");
      $(this).css("backgroundPosition", "-250px 0");
    });
    $("#pwd").focus(function () {
      $(this).addClass("txtFocus");
      $(this).css("backgroundPosition", "-250px -252px");
    });
    $("#pwd").blur(function () {
      $(this).removeClass("txtFocus");
      $(this).css("backgroundPosition", "-250px -73px");
    });
    $("#verify").focus(function () {
      $(this).addClass("txtFocus");
      $(this).css("backgroundPosition", "-250px -325px");
    });
    $("#verify").blur(function () {
      $(this).removeClass("txtFocus");
      $(this).css("backgroundPosition", "-250px -146px");
    });
    $(".btn").mouseover(function () {
      $(this).addClass("btnHover");
    });
    $(".btn").mousedown(function () {
      $(this).addClass("btnDown");
    });
    $(".btn").mouseout(function () {
      $(this).removeClass("btnHover");
      $(this).removeClass("btnDown");
    });
  });
</script>

</body>
</html>
