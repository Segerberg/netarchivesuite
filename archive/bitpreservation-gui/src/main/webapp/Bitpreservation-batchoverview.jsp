<%--
File:       $Id$
Revision:   $Revision$
Author:     $Author$
Date:       $Date$

The Netarchive Suite - Software to harvest and preserve websites
Copyright 2004-2012 The Royal Danish Library, the Danish State and
University Library, the National Library of France and the Austrian
National Library.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA

--%><%@page import="dk.netarkivet.common.utils.I18n,
                    dk.netarkivet.common.webinterface.HTMLUtils,
                    dk.netarkivet.archive.webinterface.BatchGUI"
            pageEncoding="UTF-8"
%><%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"
%><fmt:setLocale value="<%=HTMLUtils.getLocale(request)%>" scope="page"
/><fmt:setBundle scope="page" basename="<%=dk.netarkivet.archive.Constants.TRANSLATIONS_BUNDLE%>"/><%!
    private static final I18n I18N 
            = new I18n(dk.netarkivet.archive.Constants.TRANSLATIONS_BUNDLE);
            
%><%
    HTMLUtils.setUTF8(request);
    HTMLUtils.generateHeader(pageContext);
%>
<h3 class="page_heading"><fmt:message key="pagetitle;batchjob.overview"/></h3>
<%
    BatchGUI.getBatchOverviewPage(pageContext);
%>
<%
    HTMLUtils.generateFooter(out);
%>