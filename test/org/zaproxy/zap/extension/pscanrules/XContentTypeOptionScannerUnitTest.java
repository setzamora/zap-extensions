/*
 * Zed Attack Proxy (ZAP) and its related class files.
 *
 * ZAP is an HTTP/HTTPS proxy for assessing web application security.
 *
 * Copyright 2016 The ZAP development team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zaproxy.zap.extension.pscanrules;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.parosproxy.paros.core.scanner.Plugin.AlertThreshold;
import org.parosproxy.paros.network.HttpHeader;
import org.parosproxy.paros.network.HttpMalformedHeaderException;
import org.parosproxy.paros.network.HttpMessage;

public class XContentTypeOptionScannerUnitTest extends PassiveScannerTest {

    @Override
    protected XContentTypeOptionsScanner createScanner() {
        return new XContentTypeOptionsScanner();
    }

    @Test
    public void xContentTypeOptionsPresent() throws HttpMalformedHeaderException {
        
        HttpMessage msg = new HttpMessage();
        msg.setRequestHeader("GET https://www.example.com/test/ HTTP/1.1");
        
        msg.setResponseBody("<html></html>");
        msg.setResponseHeader(
                "HTTP/1.1 200 OK\r\n" +
                "Server: Apache-Coyote/1.1\r\n" +
                "X-Content-Type-Options: nosniff\r\n" +
                "Content-Type: text/html;charset=ISO-8859-1\r\n" +
                "Content-Length: " + msg.getResponseBody().length() + "\r\n");
        rule.scanHttpResponseReceive(msg, -1, this.createSource(msg));

        assertThat(alertsRaised.size(), equalTo(0));
    }

    @Test
    public void xContentTypeOptionsAbsent() throws HttpMalformedHeaderException {
        
        HttpMessage msg = new HttpMessage();
        msg.setRequestHeader("GET https://www.example.com/test/ HTTP/1.1");
        
        msg.setResponseBody("<html></html>");
        msg.setResponseHeader(
                "HTTP/1.1 200 OK\r\n" +
                "Server: Apache-Coyote/1.1\r\n" +
                "Content-Type: text/html;charset=ISO-8859-1\r\n" +
                "Content-Length: " + msg.getResponseBody().length() + "\r\n");
        rule.scanHttpResponseReceive(msg, -1, this.createSource(msg));

        assertThat(alertsRaised.size(), equalTo(1));
        assertThat(alertsRaised.get(0).getParam(), equalTo(HttpHeader.X_CONTENT_TYPE_OPTIONS));
        assertThat(alertsRaised.get(0).getEvidence(), equalTo(""));
    }

    @Test
    public void xContentTypeOptionsBad() throws HttpMalformedHeaderException {
        
        HttpMessage msg = new HttpMessage();
        msg.setRequestHeader("GET https://www.example.com/test/ HTTP/1.1");
        
        msg.setResponseBody("<html></html>");
        msg.setResponseHeader(
                "HTTP/1.1 200 OK\r\n" +
                "Server: Apache-Coyote/1.1\r\n" +
                "X-Content-Type-Options: sniff\r\n" +
                "Content-Type: text/html;charset=ISO-8859-1\r\n" +
                "Content-Length: " + msg.getResponseBody().length() + "\r\n");
        rule.scanHttpResponseReceive(msg, -1, this.createSource(msg));

        assertThat(alertsRaised.size(), equalTo(1));
        assertThat(alertsRaised.get(0).getParam(), equalTo(HttpHeader.X_CONTENT_TYPE_OPTIONS));
        assertThat(alertsRaised.get(0).getEvidence(), equalTo("sniff"));
    }

    @Test
    public void xContentTypeOptionsAbsentRedirectLow() throws HttpMalformedHeaderException {
        
        rule.setLevel(AlertThreshold.LOW);

        HttpMessage msg = new HttpMessage();
        msg.setRequestHeader("GET https://www.example.com/test/ HTTP/1.1");
        
        msg.setResponseBody("<html></html>");
        msg.setResponseHeader(
                "HTTP/1.1 301 Moved Permanently\r\n" +
                "Server: Apache-Coyote/1.1\r\n" +
                "Location: http://www.example.org/test2\r\n" +
                "Content-Type: text/html;charset=ISO-8859-1\r\n" +
                "Content-Length: " + msg.getResponseBody().length() + "\r\n");
        rule.scanHttpResponseReceive(msg, -1, this.createSource(msg));

        assertThat(alertsRaised.size(), equalTo(1));
        assertThat(alertsRaised.get(0).getParam(), equalTo(HttpHeader.X_CONTENT_TYPE_OPTIONS));
        assertThat(alertsRaised.get(0).getEvidence(), equalTo(""));
    }
    
    @Test
    public void xContentTypeOptionsAbsentRedirectMed() throws HttpMalformedHeaderException {
        
        rule.setLevel(AlertThreshold.MEDIUM);

        HttpMessage msg = new HttpMessage();
        msg.setRequestHeader("GET https://www.example.com/test/ HTTP/1.1");
        
        msg.setResponseBody("<html></html>");
        msg.setResponseHeader(
                "HTTP/1.1 301 Moved Permanently\r\n" +
                "Server: Apache-Coyote/1.1\r\n" +
                "Location: http://www.example.org/test2\r\n" +
                "Content-Type: text/html;charset=ISO-8859-1\r\n" +
                "Content-Length: " + msg.getResponseBody().length() + "\r\n");
        rule.scanHttpResponseReceive(msg, -1, this.createSource(msg));

        assertThat(alertsRaised.size(), equalTo(0));
    }

    @Test
    public void xContentTypeOptionsAbsentRedirectHigh() throws HttpMalformedHeaderException {
        
        rule.setLevel(AlertThreshold.HIGH);

        HttpMessage msg = new HttpMessage();
        msg.setRequestHeader("GET https://www.example.com/test/ HTTP/1.1");
        
        msg.setResponseBody("<html></html>");
        msg.setResponseHeader(
                "HTTP/1.1 301 Moved Permanently\r\n" +
                "Server: Apache-Coyote/1.1\r\n" +
                "Location: http://www.example.org/test2\r\n" +
                "Content-Type: text/html;charset=ISO-8859-1\r\n" +
                "Content-Length: " + msg.getResponseBody().length() + "\r\n");
        rule.scanHttpResponseReceive(msg, -1, this.createSource(msg));

        assertThat(alertsRaised.size(), equalTo(0));
    }
}