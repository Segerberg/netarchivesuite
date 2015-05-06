/*
 *  This file is part of the Heritrix web crawler (crawler.archive.org).
 *
 *  Licensed to the Internet Archive (IA) by one or more individual 
 *  contributors. 
 *
 *  The IA licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package dk.netarkivet.harvester.harvesting.extractor;

import static org.archive.modules.extractor.Hop.SPECULATIVE;
import static org.archive.modules.extractor.LinkContext.JS_MISC;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.URIException;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.exception.NestableRuntimeException;
import org.archive.io.ReplayCharSequence;
import org.archive.modules.CrawlURI;
import org.archive.modules.extractor.Extractor;
import org.archive.modules.extractor.LinkContext;
import org.archive.net.UURI;
import org.archive.util.DevUtils;
import org.archive.util.TextUtils;
import org.archive.util.UriUtils;

/**
 * Processes Javascript files for strings that are likely to be
 * crawlable URIs.
 *
 * NOTE: This processor may open a ReplayCharSequence from the 
 * CrawlURI's Recorder, without closing that ReplayCharSequence, to allow
 * reuse by later processors in sequence. In the usual (Heritrix) case, a 
 * call after all processing to the Recorder's endReplays() method ensures
 * timely close of any reused ReplayCharSequences. Reuse of this processor
 * elsewhere should ensure a similar cleanup call to Recorder.endReplays()
 * occurs. 
 * 
 * TODO: Replace with a system for actually executing Javascript in a 
 * browser-workalike DOM, such as via HtmlUnit or remote-controlled 
 * browser engines. 
 * 
 * @contributor gojomo
 */
public class IcelandicExtractorJS extends org.archive.modules.extractor.ExtractorJS {

    long foundFalsePositives = 0;

    private static Logger LOGGER =
        Logger.getLogger("org.archive.crawler.extractor.ExtractorJS");

    /**
     * The list of regular expressions to evalute potential relative url against, rejecting any that match
     */
    {
        setRejectRelativeMatchingRegexList(new ArrayList<Pattern>());
    }
    @SuppressWarnings("unchecked")
    public List<Pattern> getRejectRelativeMatchingRegexList() {
        return (List<Pattern>) kp.get("rejectRelativeMatchingRegexList");
    }
    public void setRejectRelativeMatchingRegexList(List<Pattern> patterns) {
        kp.put("rejectRelativeMatchingRegexList", patterns);
    }

    
    // finds whitespace-free strings in Javascript
    // (areas between paired ' or " characters, possibly backslash-quoted
    // on the ends, but not in the middle)
    static final String JAVASCRIPT_STRING_EXTRACTOR =
        "(\\\\{0,8}+(?:\"|\'))(\\S{0,"+UURI.MAX_URL_LENGTH+"}?)(?:\\1)";
    // GROUPS:
    // (G1) ' or " with optional leading backslashes
    // (G2) whitespace-free string delimited on boths ends by G1

    // determines whether a string is likely URI
    // (no whitespace or '<' '>',  has an internal dot or some slash,
    // begins and ends with either '/' or a word-char)
    static final String STRING_URI_DETECTOR =
        "(?:\\w|[\\.]{0,2}/)[\\S&&[^<>]]*(?:\\.|/)[\\S&&[^<>]]*(?:\\w|/)";

    
    protected long numberOfCURIsHandled = 0;

    // URIs known to produce false-positives with the current JS extractor.
    // e.g. currently (2.0.3) the JS extractor produces 13 false-positive 
    // URIs from http://www.google-analytics.com/urchin.js and only 2 
    // good URIs, which are merely one pixel images.
    // TODO: remove this blacklist when JS extractor is improved 
    protected final static String[] EXTRACTOR_URI_EXCEPTIONS = {
        "http://www.google-analytics.com/urchin.js"
        };
    
    /**
     * @param name
     */
    public IcelandicExtractorJS() {
    }

    
    protected boolean shouldExtract(CrawlURI uri) {
        
        // special-cases, for when we know our current JS extractor does poorly.
        // TODO: remove this test when JS extractor is improved 
        for (String s: EXTRACTOR_URI_EXCEPTIONS) {
            if (uri.toString().equals(s))
                return false;
        }
        
        String contentType = uri.getContentType();
        if ((contentType == null)) {
            return false;
        }

        // If the content-type indicates js, we should process it.
        if (contentType.indexOf("javascript") >= 0) {
            return true;
        }
        if (contentType.indexOf("jscript") >= 0) {
            return true;
        }
        if (contentType.indexOf("ecmascript") >= 0) {
            return true;
        }
        
        // If the filename indicates js, we should process it.
        if (uri.toString().toLowerCase().endsWith(".js")) {
            return true;
        }
        
        // If the viaContext indicates a script, we should process it.
        LinkContext context = uri.getViaContext();
        if (context == null) {
            return false;
        }
        String s = context.toString().toLowerCase();
        return s.startsWith("script");
    }
    

    @Override
    protected boolean innerExtract(CrawlURI curi) {
        this.numberOfCURIsHandled++;
        ReplayCharSequence cs = null;
        try {
            cs = curi.getRecorder().getContentReplayCharSequence();
            try {
                numberOfLinksExtracted.getAndAdd(considerStrings(this, curi, cs, true));
            } catch (StackOverflowError e) {
                DevUtils.warnHandle(e, "ExtractorJS StackOverflowError");
            }
            // Set flag to indicate that link extraction is completed.
            return true;
        } catch (IOException e) {
            curi.getNonFatalFailures().add(e);
        }
        return false;
    }

    public long considerStrings(Extractor ext, 
            CrawlURI curi, CharSequence cs, boolean handlingJSFile) {
        long foundLinks = 0;
        Matcher strings =
            TextUtils.getMatcher(JAVASCRIPT_STRING_EXTRACTOR, cs);
        int startIndex = 0;
        while (strings.find(startIndex)) {
            CharSequence subsequence =
                cs.subSequence(strings.start(2), strings.end(2));
            Matcher uri =
                TextUtils.getMatcher(STRING_URI_DETECTOR, subsequence);
            if(uri.matches()) {
                String string = uri.group();
                boolean falsePositive = false;
                try {
                    string = StringEscapeUtils.unescapeJavaScript(string);
                } catch (NestableRuntimeException e) {
                    LOGGER.log(Level.WARNING, "problem unescaping some javascript", e);
                }
                string = UriUtils.speculativeFixup(string, curi.getUURI());
                
                // Filter out some bad false positives (should really fix regexp for URI detection) 
                if (string.contains("/.") || string.contains("@") || string.length() > 150) {
                	// While legal in URIs, these are rare and usually an indication of a false positive
                	// in the speculative extraction.
                	falsePositive = true;
                }
                
                if (!falsePositive) {
                    falsePositive = shouldIgnorePossibleRelativeLink(string);
                }
                
                
                if (falsePositive) {
                	foundFalsePositives++;
                } else {
	                foundLinks++;
	                try {
	                    int max = ext.getExtractorParameters().getMaxOutlinks();
	                    if (handlingJSFile) {
	                        addRelativeToVia(curi, max, string, JS_MISC, SPECULATIVE);
	                    } else {
	                        addRelativeToBase(curi, max, string, JS_MISC, SPECULATIVE);
	                    }
	                } catch (URIException e) {
	                    ext.logUriError(e, curi.getUURI(), string);
	                }
                }
            } else {
               foundLinks += considerStrings(ext, curi, subsequence, handlingJSFile);
            }
            
            // reconsider the last closing quote as possible opening quote
            startIndex = strings.end(2);
        }
        TextUtils.recycleMatcher(strings);
        return foundLinks;
    }
    
    private boolean shouldIgnorePossibleRelativeLink(String str) {
        if (str.matches("^[a-zA-Z]://.*$")) {
            // Absolute path. Assume it is ok.
            return false;
        }
        
        List<Pattern> regexes = getRejectRelativeMatchingRegexList();
        if(regexes.size()==0){
            return false;
        }

        for (Pattern p: regexes) {
            boolean matches = p.matcher(str).matches();
            if(matches){
                return true;
            } 
        }
        
        return false;
    }
    


	@Override
	public String report() {
        StringBuffer report = new StringBuffer();
        report.append(super.report());
        report.append("  False positives eliminated: " + foundFalsePositives + "\n"); 
		return report.toString();
	}
    
    
}