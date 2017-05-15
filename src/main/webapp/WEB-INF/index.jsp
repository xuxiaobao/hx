<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
  <meta charset="UTF-8"/>
  <title>鸿信嘉诚流量充值平台</title>
  <link rel="icon" href="../favicon.gif"/>

  <!-- EXTJS Import -->
  <LINK rel="stylesheet" type="text/css" href="css/app.css"/>
  <LINK rel="stylesheet" type="text/css" href="css/index.css"/>
  <LINK rel="stylesheet" type="text/css" href="extjs/resources/css/ext-all.css"/>
  <LINK rel="stylesheet" type="text/css" href="extjs/TabScrollerMenu/tab-scroller-menu.css"/>
  <LINK rel="stylesheet" type="text/css" href="extjs/ColumnHeaderGroup/ColumnHeaderGroup.css"/>

  <script language="javascript" type="text/javascript" src="datepicker/WdatePicker.js"></script>

  <SCRIPT type="text/javascript" src="extjs/ext-base.js"></SCRIPT>
  <SCRIPT type="text/javascript" src="extjs/ext-all.js"></SCRIPT>
  <!--<SCRIPT type="text/javascript" src="http://extjs-public.googlecode.com/svn/tags/extjs-3.4.1.1/release/adapter/ext/ext-base.js"></SCRIPT>-->
  <!--<SCRIPT type="text/javascript" src="http://extjs-public.googlecode.com/svn/tags/extjs-3.4.1.1/release/ext-all.js"></SCRIPT>-->
  <SCRIPT type="text/javascript" src="extjs/Ext.TabCloseMenu.js"></SCRIPT>
  <SCRIPT type="text/javascript" src="extjs/TabScrollerMenu/Ext.TabScrollerMenu.js"></SCRIPT>
  <SCRIPT type="text/javascript" src="extjs/ColumnHeaderGroup/ColumnHeaderGroup.js"></SCRIPT>

  <!-- our script -->
  <SCRIPT type="text/javascript"  src="app/app.js"></SCRIPT>
  <SCRIPT type="text/javascript"  src="app/util.js"></SCRIPT>
  <SCRIPT type="text/javascript"  src="data/Region.js"></SCRIPT>
  <SCRIPT type="text/javascript"  src="app/Ext.ux.ChinaRegionField.js"></SCRIPT>
  <SCRIPT type="text/javascript"  src="app/view/Desktop.js"></SCRIPT>
  <SCRIPT type="text/javascript"  src="app/view/desktop/Head.js"></SCRIPT>
  <SCRIPT type="text/javascript"  src="app/view/desktop/MainTab.js"></SCRIPT>
  <SCRIPT type="text/javascript"  src="app/view/desktop/Menu.js"></SCRIPT>

  <SCRIPT type="text/javascript"  src="app/view/user/ChangePwd.js"></SCRIPT>
  <SCRIPT type="text/javascript"  src="app/view/user/UserList.js"></SCRIPT>
  <SCRIPT type="text/javascript"  src="app/view/user/AddUser.js"></SCRIPT>
  <SCRIPT type="text/javascript"  src="app/view/user/EditUser.js"></SCRIPT>
  <SCRIPT type="text/javascript"  src="app/view/user/AddBalance.js"></SCRIPT>
  <SCRIPT type="text/javascript"  src="app/view/user/DeductBalance.js"></SCRIPT>

  <SCRIPT type="text/javascript"  src="app/view/product/ProductList.js"></SCRIPT>
  <SCRIPT type="text/javascript"  src="app/view/product/EditProduct.js"></SCRIPT>

  <SCRIPT type="text/javascript"  src="app/view/blacknum/BlackNumList.js"></SCRIPT>
  <SCRIPT type="text/javascript"  src="app/view/blacknum/EditBlackNum.js"></SCRIPT>

  <SCRIPT type="text/javascript"  src="app/view/bill/BillList.js"></SCRIPT>
  <SCRIPT type="text/javascript"  src="app/view/bill/BillStatList.js"></SCRIPT>
  <SCRIPT type="text/javascript"  src="app/view/bill/disCountList.js"></SCRIPT>
  <SCRIPT type="text/javascript"  src="app/view/order/OrderList.js"></SCRIPT>
  <SCRIPT type="text/javascript"  src="app/view/order/OrderStatList.js"></SCRIPT>
  <SCRIPT type="text/javascript"  src="app/view/order/ManualList.js"></SCRIPT>
  <SCRIPT type="text/javascript"  src="app/view/order/Batch.js"></SCRIPT>

  <SCRIPT type="text/javascript"  src="app/view/sp/SpList.js"></SCRIPT>
  <SCRIPT type="text/javascript"  src="app/view/sp/EditSp.js"></SCRIPT>
  <SCRIPT type="text/javascript"  src="app/view/sp/addSp.js"></SCRIPT>


  <SCRIPT type="text/javascript"  src="app/view/usersuplimit/UserSupLimit.js"></SCRIPT>
  <SCRIPT type="text/javascript"  src="app/view/usersuplimit/addUserSupLimit.js"></SCRIPT>
  <SCRIPT type="text/javascript"  src="app/view/usersuplimit/EditUserSupLimit.js"></SCRIPT>


  <SCRIPT type="text/javascript"  src="app/view/discount/Discount.js"></SCRIPT>
  <SCRIPT type="text/javascript"  src="app/view/discount/addDiscount.js"></SCRIPT>
  <SCRIPT type="text/javascript"  src="app/view/discount/EditDiscount.js"></SCRIPT>
  
  <SCRIPT type="text/javascript"  src="app/view/failstat/PhoneStatList.js"></SCRIPT>
  <SCRIPT type="text/javascript"  src="app/view/failstat/ReasonStatList.js"></SCRIPT>
  
  <SCRIPT type="text/javascript"  src="app/view/export/ExportList.js"></SCRIPT>

  <style type="text/css">
    /*解决Extjs grid不允许选择复制的问题*/

    .x-selectable, .x-selectable * {
      -moz-user-select: text!important;
      -khtml-user-select: text!important;
      -webkit-user-select: text!important;
      -ms-user-select: text!important;
      user-select: text!important;
    }
  </style>
</head>
<body scroll="no" id="docs">
<div id="loading-mask" style=""></div>
<div id="loading">
  <div class="loading-indicator">
    <img  src="../images/index/extanim32.gif" width="32" height="32" style="margin-right: 8px;"
         align="absmiddle"/>Loading application ...
  </div>
</div>
<div id="header">
  <a href="/" target="_blank" style="float: right; margin-right: 10px;">
    <!--<img  src="images/login/logo.png" style="width: 83px; height: 24px; margin-top: 1px;"/>-->
  </a>
    <span style="float: right; margin-right: 10px; margin-top: 5px; color:white; ">
        欢迎您， <span onclick=""><c:out value="${loginUser.userName}"/></span> ! &nbsp;&nbsp;
        <a href="api/system/user/logout"><span style="color:white;">注销</span></a>
        <a href="#" onclick="changePwd()"><span style="color:white;">修改密码</span></a>
        <script type="application/javascript" language="JavaScript">
          /*<![CDATA[*/
          <c:if test="${loginUser.role=='ROLE_ADMIN'}" var="isAdmin"/>

          var IS_ADMIN = ${isAdmin};
          console.log("IS_ADMIN="+IS_ADMIN);

          var CURR_USER= '${loginUser.userName}';
          console.log("CURR_USER="+CURR_USER);
          /*]]>*/
        </script>
    </span>

  <div class="api-title">鸿信嘉诚充值 - 管理系统</div>
</div>

<!--<div id="classes"></div>-->

<!--<div id="main"></div>-->
</body>
<script>
  function token(){

  }

  function changePwd() {

    var win = Ext.getCmp("changepwd-win");

    if (win) {
      win.show(this);
    } else {
      win = new Desktop.ChangePwdWin();
      win.show(this);
    }
  }
</script>