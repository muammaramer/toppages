<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="jcr" uri="http://www.jahia.org/tags/jcr" %>
<%@ taglib prefix="ui" uri="http://www.jahia.org/tags/uiComponentsLib" %>
<%@ taglib prefix="functions" uri="http://www.jahia.org/tags/functions" %>
<%@ taglib prefix="query" uri="http://www.jahia.org/tags/queryLib" %>
<%@ taglib prefix="utility" uri="http://www.jahia.org/tags/utilityLib" %>
<%@ taglib prefix="s" uri="http://www.jahia.org/tags/search" %>
<%--@elvariable id="currentNode" type="org.jahia.services.content.JCRNodeWrapper"--%>
<%--@elvariable id="out" type="java.io.PrintWriter"--%>
<%--@elvariable id="script" type="org.jahia.services.render.scripting.Script"--%>
<%--@elvariable id="scriptInfo" type="java.lang.String"--%>
<%--@elvariable id="workspace" type="java.lang.String"--%>
<%--@elvariable id="renderContext" type="org.jahia.services.render.RenderContext"--%>
<%--@elvariable id="currentResource" type="org.jahia.services.render.Resource"--%>
<%--@elvariable id="url" type="org.jahia.services.render.URLGenerator"--%>

<template:addResources insert="false" type="javascript" resources="jquery.js"/>


<c:set var="getActionUrl" value="${url.base}${currentNode.path}.getTopPages.do"/>
<c:set var="updateActionUrl" value="${url.base}${currentNode.path}.updateTopPages.do"/>

<c:set var="resultDiv" value="result-${currentNode.name}"/>
<c:set var="messagesDiv" value="messages-${currentNode.name}"/>
<c:set var="updateButton" value="updateBtn-${currentNode.name}"/>
<c:set var="title" value="${currentNode.properties['jcr:title'].string}"/>

<c:set var="customCSS" value="${currentNode.properties['customCSS'].string}"/>

<c:if test="${not empty customCSS}">
    <template:addResources>
        <style type="text/css">
            ${customCSS}
        </style>
    </template:addResources>

</c:if>

<c:if test="${renderContext.editMode}">

    <c:set var="updateError" value="${currentNode.properties['lastErrorReceived'].string}"/>
    <c:if test="${not empty updateError}">
        <div class="alert alert-danger">
            <p><b> <fmt:message key="toppages.result.error"/> </b> <br> ${updateError} </p>
        </div>

    </c:if>

    <div id="${messagesDiv}" style="display: none;">
        <button type="button" class="close" data-dismiss="alert">&times;</button>
        <div id="message">
        </div>
    </div>

    <button type="submit" id="${updateButton}" type="button" class="btn btn-primary"> Update Top Pages</button>

</c:if>

<div id="${resultDiv}">

</div>


<template:addResources>
    <script language="JavaScript">

        $(document).ready(function () {
            var resultDiv = $('#${resultDiv}');
            var messagesDiv = $("#${messagesDiv}");
            var title = "${title}";
            getTopPages("${getActionUrl}", resultDiv,messagesDiv,title,false);
            $("#${updateButton}").click(function () {
                getTopPages("${updateActionUrl}", resultDiv,messagesDiv,title,true);
            });
        });

        function getTopPages(actionUrl, resultDiv, messagesDiv,title,update) {
            console.info("getting topPages");
            $.getJSON(actionUrl,
                function (text) {

                    if (text.topPages) {

                        var divHtml = "<h3>"+ title+"</h3>";

                        divHtml += "<ul>"
                        $.each(text.topPages, function (i, page) {
                            divHtml += "<li> <a href=\"" + page.href + "\">" + page.title + "</a> </li>";
                        });
                        divHtml += "</ul>";
                        resultDiv.html(divHtml);
                        if (update) {
                            messagesDiv.find('#message').html("Top pages updated");
                            messagesDiv.removeClass();
                            messagesDiv.addClass("alert alert-success");
                            messagesDiv.show();
                        }

                    }

                    if (text.errorMessages) {
                        var errors = text.errorMessages;
                        var msgs = "";
                        errors.forEach(function (msg) {
                            msgs += msg + "\n";

                        });
                        messagesDiv.find('#message').html(msgs);
                        messagesDiv.removeClass();
                        messagesDiv.addClass("alert alert-danger");
                        messagesDiv.show();
                    }

                    messagesDiv.on("close.bs.alert", function () {
                        messagesDiv.hide();
                        return false;
                    });

                });

        }


    </script>
</template:addResources>





