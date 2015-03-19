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

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.Test;

import com.atomicleopard.expressive.Expressive;
import com.threewks.thundr.view.View;
import com.threewks.thundr.view.string.StringView;

public class DocraptorRequestTest {

	@Test
	public void shouldRetainNameAndUrl() throws MalformedURLException, URISyntaxException {
		assertThat(DocraptorRequest.ForUrl("name", "url").name, is("name"));
		assertThat(DocraptorRequest.ForUrl("name", "url").document_url, is("url"));
		assertThat(DocraptorRequest.ForUrl("name", new URL("http://url.com/")).name, is("name"));
		assertThat(DocraptorRequest.ForUrl("name", new URL("http://url.com/")).document_url, is("http://url.com/"));
		assertThat(DocraptorRequest.ForUrl("name", new URI("http://url.com/")).name, is("name"));
		assertThat(DocraptorRequest.ForUrl("name", new URI("http://url.com/")).document_url, is("http://url.com/"));
	}

	@Test
	public void shouldRetainNameAndContent() {
		assertThat(DocraptorRequest.ForContent("name", "content").name, is("name"));
		assertThat(DocraptorRequest.ForContent("name", "content").document_content, is("content"));

		View stringView = new StringView("content");
		assertThat(DocraptorRequest.ForContent("name", stringView).contentView, is(stringView));
		assertThat(DocraptorRequest.ForContent("name", stringView).contentView, is(stringView));
	}

	@Test
	public void shouldProvideFluentApiReturningImmutableInstances() {
		DocraptorRequest content = DocraptorRequest.ForContent("name", "original");
		assertThat(content.document_type, is("pdf"));
		assertThat(content.javascript, is(nullValue()));
		assertThat(content.test, is(false));
		assertThat(content.prince_options, is(nullValue()));

		assertThat(content.withTestMode(true).test, is(true));
		assertThat(content.withTestMode(true), is(not(sameInstance(content))));
		assertThat(content.test, is(false));

		assertThat(content.withType("xls").document_type, is("xls"));
		assertThat(content.withType("xls"), is(not(sameInstance(content))));
		assertThat(content.document_type, is("pdf"));
		
		assertThat(content.withType("xls").withType(DocumentType.Pdf).document_type, is("pdf"));
		assertThat(content.withType("xls").withType(DocumentType.Pdf), is(not(sameInstance(content))));

		assertThat(content.withJavascript(true).javascript, is(true));
		assertThat(content.withJavascript(true), is(not(sameInstance(content))));
		assertThat(content.javascript, is(nullValue()));

		assertThat(content.withName("other").name, is("other"));
		assertThat(content.withName("other"), is(not(sameInstance(content))));
		assertThat(content.name, is("name"));

		assertThat(content.withDocumentContent("content").document_content, is("content"));
		assertThat(content.withDocumentContent("content"), is(not(sameInstance(content))));
		assertThat(content.document_content, is("original"));
	}

	@Test
	public void shouldRenderViewIfNoUrlNotContentBuView() {
		assertThat(DocraptorRequest.ForContent("name", new StringView("view")).shouldRenderView(), is(true));
		assertThat(DocraptorRequest.ForContent("name", "view").shouldRenderView(), is(false));
		assertThat(DocraptorRequest.ForUrl("name", "view").shouldRenderView(), is(false));
	}

	@Test
	public void shouldAllowSettingOfPrinceOptions() {
		DocraptorRequest content = DocraptorRequest.ForContent("name", "content");
		assertThat(content.prince_options, is(nullValue()));

		assertThat(content.withPrinceOption("option", "value").prince_options, is(hasEntry("option", (Object) "value")));
		assertThat(content.withPrinceJavascript(true).prince_options, is(hasEntry("javascript", (Object) true)));
		
		assertThat(content.withPrinceJavascript(false).withPrinceOption("option", "value").prince_options, is(Expressive.<String, Object>map("javascript", false, "option", "value")));

	}
}
