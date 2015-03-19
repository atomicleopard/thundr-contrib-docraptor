/*
 * This file is a part of thundr-contrib-docraptor, a software library from Atomic Leopard.
 *
 * Copyright (C) 2015 Atomic Leopard, <nick@atomicleopard.com.au>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.atomicleopard.thundr.docraptor;

import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.threewks.thundr.view.View;

/**
 * <p>
 * Contains the content for a docraptor request.
 * </p>
 * <p>
 * See the documentation here: <a href="http://docraptor.com/documentation">http://docraptor.com/documentation</a>.
 * </p>
 * <p>
 * Create a request using one of the factory methods, then use the fluent api to refine any parameters you need to modify.<br/>
 * For example, creating a pdf using content (document_content):
 * </p>
 * 
 * <pre>
 * <code>
 * DocraptorRequest.ForContent("my-pdf.pdf", new JspView("/my.jsp", model))
 * 		.withJavascript(true)
 * 		.withTestMode(true);
 * </code>
 * </pre>
 * <p>
 * or creating a document using a url (document_url)
 * </p>
 * 
 * <pre>
 * <code>
 * DocraptorRequest.ForUrl("my-pdf.pdf", "http://myserver.com/callback/url.html")
 * 		.withJavascript(true)
 * 		.withTestMode(testMode);
 * </code>
 * </pre>
 * 
 * 
 */
public class DocraptorRequest {
	protected String name;
	protected String document_url;
	protected String document_content;
	protected String document_type = "pdf";
	protected boolean test = false;
	protected Boolean javascript = null;
	protected Map<String, Object> prince_options;
	protected transient View contentView;

	public static DocraptorRequest ForContent(String name, String content) {
		return new DocraptorRequest(name, null, content, null, DocumentType.Pdf.apiType(), false, null, null);
	}

	public static DocraptorRequest ForContent(String name, View view) {
		return new DocraptorRequest(name, null, null, view, DocumentType.Pdf.apiType(), false, null, null);
	}

	public static DocraptorRequest ForUrl(String name, URI url) {
		return ForUrl(name, url.toString());
	}

	public static DocraptorRequest ForUrl(String name, URL url) {
		return ForUrl(name, url.toString());
	}

	public static DocraptorRequest ForUrl(String name, String url) {
		return new DocraptorRequest(name, url.toString(), null, null, DocumentType.Pdf.apiType(), false, null, null);
	}

	public DocraptorRequest withTestMode(boolean test) {
		return new DocraptorRequest(name, document_url, document_content, contentView, document_type, test, javascript, prince_options);
	}

	public DocraptorRequest withJavascript(boolean javascript) {
		return new DocraptorRequest(name, document_url, document_content, contentView, document_type, test, javascript, prince_options);
	}

	protected DocraptorRequest(String name, String document_url, String document_content, View contentView, String document_type, boolean test, Boolean javascript, Map<String, Object> prince_options) {
		super();
		this.name = name;
		this.document_url = document_url;
		this.document_content = document_content;
		this.contentView = contentView;
		this.document_type = document_type;
		this.test = test;
		this.javascript = javascript;
		this.prince_options = prince_options;
	}

	/**
	 * Specify arbitrary options to prince. These are distinct from docraptor options.
	 * 
	 * See more here: <a href="http://www.princexml.com/doc/9.0/command-line/">http://www.princexml.com/doc/9.0/command-line/</a>
	 * 
	 * @param option
	 * @param value
	 * @return
	 */
	public DocraptorRequest withPrinceOption(String option, Object value) {
		return new DocraptorRequest(name, document_url, document_content, contentView, document_type, test, javascript, princeOption(option, value));
	}

	/**
	 * Specify whether or not javascript should run in prince
	 * 
	 * @param javascript
	 * @return
	 * @see #withPrinceOption(String, Object)
	 */
	public DocraptorRequest withPrinceJavascript(boolean javascript) {
		return withPrinceOption("javascript", javascript);
	}

	public DocraptorRequest withName(String name) {
		return new DocraptorRequest(name, document_url, document_content, contentView, document_type, test, javascript, prince_options);
	}

	public DocraptorRequest withType(DocumentType type) {
		return withType(type.apiType());
	}

	public DocraptorRequest withType(String type) {
		return new DocraptorRequest(name, document_url, document_content, contentView, type, test, javascript, prince_options);
	}

	public DocraptorRequest withDocumentContent(String documentContent) {
		return new DocraptorRequest(name, null, documentContent, null, document_type, test, javascript, prince_options);
	}

	public boolean shouldRenderView() {
		return document_content == null && contentView != null;
	}

	protected Map<String, Object> princeOption(String key, Object value) {
		Map<String, Object> prince = prince_options == null ? new HashMap<String, Object>() : new HashMap<String, Object>(prince_options);
		prince.put(key, value);
		return prince;
	}
}